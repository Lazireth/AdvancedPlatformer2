package com.github.lazireth.advancedPlatformer.objects.timedMovement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;

public class TimedMovement {
    // while running do NOT change the body's position, velocity, or collision state
    ArrayList<MovementStep> movementSteps;
    int currentMovementStep=-1;
    Vector2 initialPosition;
    float timeSinceStarted=0;
    Body body;

    boolean initialCollisionState;
    boolean collisionCurrentlyEnabled;

    boolean started=false;
    boolean finished=false;
    boolean resetting=false;


    public TimedMovement(ArrayList<MovementStep> movementStepsInput,Body bodyIn){
        movementSteps=movementStepsInput;
        body=bodyIn;
    }
    void start(){//start the timed movement
        initialCollisionState=body.getFixtureList().get(0).isSensor();
        collisionCurrentlyEnabled =initialCollisionState;
        initialPosition=body.getPosition().cpy();

        started=true;
    }
    void update(float delta){
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
            //if there is not a next movement step
            finished=true;
            return;
        }
        timeSinceStarted+=delta;
        if(movementSteps.get(currentMovementStep+1).startTime>timeSinceStarted){
            body.setLinearVelocity(movementSteps.get(currentMovementStep).velocity);
            handleCollisionFlags();
            currentMovementStep+=1;
        }

    }
    private void handleCollisionFlags(){
        switch (movementSteps.get(currentMovementStep).flag){
            case ON -> {
                if(!collisionCurrentlyEnabled){
                    setBodyCollisionTo(true);
                }
            }
            case OFF -> {
                if(collisionCurrentlyEnabled){
                    setBodyCollisionTo(false);
                }
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
        for(int i=0;i<body.getFixtureList().size;i++){
            body.getFixtureList().get(i).setSensor(newState);
        }
    }
    void reset(){
        resetting=true;
        Vector2 neededPos=body.getPosition().sub(initialPosition);
        Vector2 neededVel=new Vector2(neededPos.x*60,neededPos.y*60);
        body.setLinearVelocity(neededVel);
    }
}
