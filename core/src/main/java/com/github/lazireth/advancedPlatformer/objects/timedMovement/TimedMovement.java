package com.github.lazireth.advancedPlatformer.objects.timedMovement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import java.util.ArrayList;

public class TimedMovement {
    // while running do NOT change the body's position, velocity
    ArrayList<MovementStep> movementSteps;
    public int currentMovementStep=-1;
    Vector2 initialPosition;
    float timeSinceStarted=0;
    final Body body;

    private boolean initialCollisionState;
    boolean collisionCurrentlyEnabled;
    BodyDef.BodyType finalBodyType=null;

    public boolean started=false;
    public boolean running=false;
    public boolean finished=false;
    public boolean hasReset=false;
    public boolean resetting=false;

    private boolean autoResetTimedMovement=false;
    private boolean autoResetBodyPosition=false;
    public int timesRun=0;

    public TimedMovement(ArrayList<MovementStep> movementStepsInput,Body bodyIn){
        movementSteps=movementStepsInput;
        body=bodyIn;
    }
    public TimedMovement(ArrayList<MovementStep> movementStepsInput,Body bodyIn,boolean autoStart){
        movementSteps=movementStepsInput;
        body=bodyIn;
        if(autoStart){
            start();
        }
    }
    public TimedMovement(ArrayList<MovementStep> movementStepsInput,Body bodyIn, BodyDef.BodyType finalBodyTypeIn){
        movementSteps=movementStepsInput;
        finalBodyType=finalBodyTypeIn;
        body=bodyIn;
    }
    public TimedMovement(ArrayList<MovementStep> movementStepsInput,Body bodyIn,boolean autoStart, BodyDef.BodyType finalBodyTypeIn){
        movementSteps=movementStepsInput;
        finalBodyType=finalBodyTypeIn;
        body=bodyIn;
        if(autoStart){
            start();
        }
    }
    public void start(){//start the timed movement
        //System.out.println("start");
        if(!body.getType().equals(BodyDef.BodyType.KinematicBody)){
            body.setType(BodyDef.BodyType.KinematicBody);
            Gdx.app.log("TimedMovement","body at "+body.getPosition().x+","+body.getPosition().y+" is set to Kinematic");
        }
        initialCollisionState=body.getFixtureList().get(0).isSensor();
        collisionCurrentlyEnabled =initialCollisionState;
        initialPosition=body.getPosition().cpy();

        timesRun++;
        hasReset=false;
        started=true;
        running=true;
    }
    public void update(float delta){
        if(resetting){
            if(Math.abs(body.getPosition().sub(initialPosition).x)==0&&Math.abs(body.getPosition().sub(initialPosition).y)==0){
                //System.out.println("stop");
                body.setLinearVelocity(0,0);
                resetting=false;
                running=false;
                hasReset=true;
                if(finalBodyType!=null){
                    body.setType(finalBodyType);
                }
                return;
            }
            Vector2 distanceToMove=initialPosition.cpy().sub(body.getPosition());
            Vector2 neededVel=new Vector2(distanceToMove.x*60,distanceToMove.y*60);
            //System.out.println("neededVel "+neededVel);
            body.setLinearVelocity(neededVel);
        }

        if(finished||!started){return;}
        if(currentMovementStep+1>=movementSteps.size()){
            //if there is not a next movement step
            Gdx.app.log("TimedMovement","body at "+body.getPosition().x+","+body.getPosition().y+" finished its TimedMovement");
            running=false;
            finished=true;

            if(autoResetTimedMovement){
                resetTimedMovement();
            }else if(autoResetBodyPosition){
                resetBodyPosition();
            }else{
                if(finalBodyType!=null){
                    body.setType(finalBodyType);
                }
            }
            return;
        }

        timeSinceStarted+=delta;
        if(timeSinceStarted>movementSteps.get(currentMovementStep+1).startTime){
            currentMovementStep+=1;
            if(movementSteps.get(currentMovementStep).velocity!=null){
                body.setLinearVelocity(movementSteps.get(currentMovementStep).velocity);
            }
            handleCollisionFlags();
        }
    }
    private void handleCollisionFlags(){
        switch (movementSteps.get(currentMovementStep).flag){
            case ON -> setBodyCollisionTo(true);
            case OFF -> setBodyCollisionTo(false);
            case TOGGLE -> setBodyCollisionTo(!collisionCurrentlyEnabled);
            case RESET -> {
                if(collisionCurrentlyEnabled!=initialCollisionState){
                    setBodyCollisionTo(initialCollisionState);
                }
            }
        }
    }
    private void setBodyCollisionTo(boolean newState){
        if(body.getFixtureList().get(0).isSensor()==newState){
            return;
        }
        for(int i=0;i<body.getFixtureList().size;i++){
            body.getFixtureList().get(i).setSensor(newState);
        }
    }
    public void resetBodyPosition(){
        resetting=true;
        running=true;
    }
    public void resetTimedMovement(){
        currentMovementStep=-1;
        timeSinceStarted=0;

        started=false;
        finished=false;

        resetting=true;
        running=true;
    }
    public void autoResetBodyPosition(boolean bool){
        autoResetBodyPosition=bool;
        autoResetTimedMovement=false;
    }
    public void autoResetTimedMovement(boolean bool){
        autoResetTimedMovement=bool;
        autoResetBodyPosition=false;
    }
}
