package com.github.lazireth.advancedPlatformer.objects.timedMovement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;

public class TimedMovement {
    // while running do NOT change the body's position, velocity
    ArrayList<MovementStep> movementSteps;
    public int currentMovementStep=-1;
    Vector2 initialPosition;
    float timeSinceStarted=0;
    Body body;

    boolean initialCollisionState;
    boolean collisionCurrentlyEnabled;

    public boolean started=false;
    public boolean finished=false;
    public boolean resetting=false;


    public TimedMovement(ArrayList<MovementStep> movementStepsInput,Body bodyIn){
        movementSteps=movementStepsInput;
        body=bodyIn;
    }
    public TimedMovement(ArrayList<MovementStep> movementStepsInput,Body bodyIn,boolean autoStart){
        movementSteps=movementStepsInput;
        body=bodyIn;
        if(autoStart){
            initialCollisionState=body.getFixtureList().get(0).isSensor();
            collisionCurrentlyEnabled =initialCollisionState;
            initialPosition=body.getPosition().cpy();

            started=true;
        }
    }
    public void start(){//start the timed movement
        System.out.println("start");
        initialCollisionState=body.getFixtureList().get(0).isSensor();
        collisionCurrentlyEnabled =initialCollisionState;
        initialPosition=body.getPosition().cpy();

        started=true;
    }
    public void update(float delta){

        if(!started){return;}

        if(resetting){
            Vector2 neededPos=body.getPosition().sub(initialPosition);
            Vector2 neededVel=new Vector2(neededPos.x*60,neededPos.y*60);
            body.setLinearVelocity(neededVel);
            if(body.getPosition().sub(initialPosition).x==0&&body.getPosition().sub(initialPosition).y==0){
                body.setLinearVelocity(0,0);
            }
        }
        if(finished){return;}
        if(currentMovementStep+1>=movementSteps.size()){
            System.out.println("finished");
            //if there is not a next movement step
            finished=true;
            return;
        }

        timeSinceStarted+=delta;
        if(timeSinceStarted>movementSteps.get(currentMovementStep+1).startTime){
            currentMovementStep+=1;
            body.setLinearVelocity(movementSteps.get(currentMovementStep).velocity);
            handleCollisionFlags();
        }

    }
    private void handleCollisionFlags(){
        switch (movementSteps.get(currentMovementStep).flag){
            case ON -> {
                setBodyCollisionTo(true);
            }
            case OFF -> {
                setBodyCollisionTo(false);
            }
            case TOGGLE -> {
                setBodyCollisionTo(!collisionCurrentlyEnabled);
            }
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
    public void reset(){
        resetting=true;
        Vector2 neededPos=body.getPosition().sub(initialPosition);
        Vector2 neededVel=new Vector2(neededPos.x*60,neededPos.y*60);
        body.setLinearVelocity(neededVel);
    }
}
