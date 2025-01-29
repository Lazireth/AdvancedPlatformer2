package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.physics.box2d.*;
import com.github.lazireth.advancedPlatformer.objects.*;
import com.github.lazireth.advancedPlatformer.objects.enemies.BasicEnemy;

public class CollisionListener implements ContactListener {
    @Override
    /// Called when two fixtures begin to touch
    public void beginContact(Contact contact) {
        Fixture fixtureA=contact.getFixtureA();
        Fixture fixtureB=contact.getFixtureB();

        if(fixtureA.getUserData()!=null&&fixtureB.getUserData()!=null){
            zeroNullsBegin(fixtureA, fixtureB);
        }else{
            System.out.println("one or no nulls begin \t one or no nulls begin \t one or no nulls begin \t one or no nulls begin");
        }
    }
    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA=contact.getFixtureA();
        Fixture fixtureB=contact.getFixtureB();

        if(fixtureA.getUserData()!=null&&fixtureB.getUserData()!=null){
            zeroNullsEnd(fixtureA, fixtureB);
        }else{
            System.out.println("one or no nulls end \t one or no nulls end \t one or no nulls end \t one or no nulls end");
        }
    }
    private void zeroNullsBegin(Fixture fixtureA, Fixture fixtureB){
        if(playerCollisionBegin(fixtureA.getUserData(), fixtureB.getUserData())){return;}
        switch (fixtureA.getUserData()){
            //case Wall ignored->System.out.println("wall");
            case ObjectSensor objectSensor-> {
                switch (objectSensor.sensorName){
                    case "playerFootSensor"-> ((Player)objectSensor.relatedObject).numFootContacts++;
                    case "enemyLeftSide"-> ((BasicEnemy)objectSensor.relatedObject).directionToBounceTo=Direction.R;
                    case "enemyRightSide"-> ((BasicEnemy)objectSensor.relatedObject).directionToBounceTo=Direction.L;
                    case "oneUPLeftSide"-> ((OneUP)objectSensor.relatedObject).directionToBounceTo=Direction.R;
                    case "oneUPRightSide"-> ((OneUP)objectSensor.relatedObject).directionToBounceTo=Direction.L;
                    case "mushroomLeftSide"-> ((Mushroom)objectSensor.relatedObject).directionToBounceTo=Direction.R;
                    case "mushroomRightSide"-> ((Mushroom)objectSensor.relatedObject).directionToBounceTo=Direction.L;
                }
            }
            case null, default -> {}
        }
        switch (fixtureB.getUserData()){
            case ObjectSensor objectSensor-> {
                switch (objectSensor.sensorName){
                    case "playerFootSensor"-> ((Player)objectSensor.relatedObject).numFootContacts++;
                    case "enemyLeftSide"-> ((BasicEnemy)objectSensor.relatedObject).directionToBounceTo=Direction.R;
                    case "enemyRightSide"-> ((BasicEnemy)objectSensor.relatedObject).directionToBounceTo=Direction.L;
                    case "oneUPLeftSide"-> ((OneUP)objectSensor.relatedObject).directionToBounceTo=Direction.R;
                    case "oneUPRightSide"-> ((OneUP)objectSensor.relatedObject).directionToBounceTo=Direction.L;
                    case "mushroomLeftSide"-> ((Mushroom)objectSensor.relatedObject).directionToBounceTo=Direction.R;
                    case "mushroomRightSide"-> ((Mushroom)objectSensor.relatedObject).directionToBounceTo=Direction.L;
                }
            }
            case null, default -> {}
        }
    }
    private boolean playerCollisionBegin(Object objectA, Object objectB){
        Player player=null;
        Object notPlayer=null;
        if(objectA.getClass().equals(Player.class)){//if objectA is a player
            player=((Player) objectA);
            notPlayer=objectB;
        }else if(objectB.getClass().equals(Player.class)){//if objectB is a player
            player=((Player) objectB);
            notPlayer=objectA;
        }
        switch (notPlayer){
            case InteractableObject interactableObject->interactableObject.startInteractionWithPlayer(player);
            case Wall ignored->player.preserveVelocityWhenLanding=true;
            case ObjectSensor objectSensor->{
                switch (objectSensor.sensorName){
                    case "levelEndFlagFlagPole"-> ((LevelEndFlag)objectSensor.relatedObject).contactWithPlayer(player);
                    case null, default -> {
                        return false;
                    }
                }

            }
            case null, default -> {
                return false;
            }
        }
        return true;
    }
    private void zeroNullsEnd(Fixture fixtureA, Fixture fixtureB){
        switch (fixtureA.getUserData()){
            case ObjectSensor objectSensor-> {
                switch (objectSensor.sensorName){
                    case "playerFootSensor"-> ((Player)objectSensor.relatedObject).numFootContacts--;
                }
            }
            case null, default -> {}
        }
        switch (fixtureB.getUserData()){
            case ObjectSensor objectSensor-> {
                switch (objectSensor.sensorName){
                    case "playerFootSensor"-> ((Player)objectSensor.relatedObject).numFootContacts--;
                }
            }
            case null, default -> {}
        }
    }
    @Override
    /// This is called after a contact is updated. This allows you to inspect a contact before it goes to the solver. If you are
    /// careful, you can modify the contact manifold (e.g. disable contact). A copy of the old manifold is provided so that you can
	/// detect changes. Note: this is called only for awake bodies. Note: this is called even when the number of contact points is
	/// zero. Note: this is not called for sensors. Note: if you set the number of contact points to zero, you will not get an
	/// EndContact callback. However, you may get a BeginContact callback the next step.
    public void preSolve(Contact contact, Manifold manifold) {// what this is https://stackoverflow.com/a/27335246
    }

    @Override
    /// This lets you inspect a contact after the solver is finished. This is useful for inspecting impulses. Note: the contact
    /// manifold does not include time of impact impulses, which can be arbitrarily large if the sub-step is small. Hence, the
    /// impulse is provided explicitly in a separate data structure. Note: this is only called for contacts that are touching,
    /// solid, and awake.
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
    }
    // taken from https://stackoverflow.com/questions/23309326/implement-nand-only-by-and
    // simplified to what it is by IntelliJ
}
