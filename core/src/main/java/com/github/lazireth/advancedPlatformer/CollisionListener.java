package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.physics.box2d.*;
import com.github.lazireth.advancedPlatformer.objects.InteractableObject;
import com.github.lazireth.advancedPlatformer.objects.Mushroom;
import com.github.lazireth.advancedPlatformer.objects.OneUP;

public class CollisionListener implements ContactListener {


    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA;
        Fixture fixtureB;

        Object userDataA;
        Object userDataB;
        fixtureA=contact.getFixtureA();
        fixtureB=contact.getFixtureB();
        userDataA=fixtureA.getBody().getUserData();
        userDataB=fixtureB.getBody().getUserData();

        //handles cases where only one is null
        if(userDataA!=null&&userDataB==null){
            oneNull(fixtureA,fixtureB,userDataA);
        }else if(userDataA==null&&userDataB!=null){
            oneNull(fixtureB,fixtureA,userDataB);
        }
        //handles cases where zero are null
        if(userDataA!=null&&userDataB!=null){
            zeroNulls(fixtureA, fixtureB, userDataA, userDataB);
        }
    }
    private void zeroNulls(Fixture fixtureA, Fixture fixtureB, Object userDataA, Object userDataB){
        if(playerCollision(userDataA, userDataB)){
            return;
        }
        // add other cases where both fixtureA and B need to be not null here
    }
    private void oneNull(Fixture goodFixture, Fixture nullFixture, Object object){
        switch (object){
            case Mushroom mushroom ->mushroom.bounce();
            case OneUP oneUP ->oneUP.bounce();
            case Player player ->player.wallCollide();
            case null, default -> {}
        }
    }

    private boolean playerCollision(Object objectA, Object objectB){
        if(objectA.getClass().equals(Player.class)){//true if objectA is a player
            if(objectB instanceof InteractableObject){
                ((InteractableObject)objectB).startInteraction((Player)objectA);
            }
            return true;
        }else
        if(objectB.getClass().equals(Player.class)){//true if objectB is a player
            if(objectA instanceof InteractableObject){
                ((InteractableObject)objectA).startInteraction((Player)objectB);
            }
            return true;
        }
        return false;
    }



















    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
    }
    // taken from https://stackoverflow.com/questions/23309326/implement-nand-only-by-and
    // simplified to what it is by IntelliJ
    public boolean nand(boolean a, boolean b){
        return (!a || !b);
    }
}
