package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.lazireth.advancedPlatformer.objects.Bounceable;
import com.github.lazireth.advancedPlatformer.objects.InteractableObject;
import com.github.lazireth.advancedPlatformer.objects.Mushroom;
import com.github.lazireth.advancedPlatformer.objects.OneUP;

import java.util.Arrays;

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
            oneNull(fixtureA,fixtureB,userDataA,contact);
        }else if(userDataA==null&&userDataB!=null){
            oneNull(fixtureB,fixtureA,userDataB,contact);
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
    private void oneNull(Fixture goodFixture, Fixture nullFixture, Object object, Contact contact){
        if(object.getClass().isInstance(Bounceable.class)){
            //if the object is bounceable
            if(checksIfObjectShouldBounce((InteractableObject)object,contact)){
                //if it should bounce
                switch ((InteractableObject)object){
                    case Mushroom mushroom ->mushroom.bounce();
                    case OneUP oneUP ->oneUP.bounce();
                    default -> throw new IllegalStateException("Unexpected value: " + (object));
                }
            }
        }
        switch (object){
            case Player player ->player.preserveVelocityWhenLanding=true;
            case null, default -> {}
        }
    }
    // I know this method is big and has a lot of if statements, but I don't think it can get much better with the current approach
    boolean checksIfObjectShouldBounce(InteractableObject object, Contact contact){
        Vector2 pos=object.body.getPosition();
        Vector2[] corners={pos.cpy().add(0.5f,0.5f),pos.cpy().add(0.5f,-0.5f),pos.cpy().add(-0.5f,-0.5f),pos.cpy().add(-0.5f,0.5f)};

        if(contact.getWorldManifold().getNumberOfContactPoints()==2){//one full side is colliding
            if(object.body.getLinearVelocity().x>0){
                //moving to the right
                if(diffLessThan(corners[0].x,contact.getWorldManifold().getPoints()[0].x,0.1f)){
                    //right side is colliding
                    if(diffLessThan(corners[0],contact.getWorldManifold().getPoints()[0],0.1f)&&
                        diffLessThan(corners[1],contact.getWorldManifold().getPoints()[1],0.1f)){
                        //TR and point 0    BR and point 1
                        return true;
                    }else if(diffLessThan(corners[0],contact.getWorldManifold().getPoints()[1],0.1f)&&
                        diffLessThan(corners[1],contact.getWorldManifold().getPoints()[0],0.1f)){
                        //TR and point 1    BR and point 0
                        return true;
                    }
                }
            }else{
                //moving to the left
                if(diffLessThan(corners[3].x,contact.getWorldManifold().getPoints()[0].x,0.1f)){
                    //left side is colliding
                    if(diffLessThan(corners[3],contact.getWorldManifold().getPoints()[0],0.1f)&&
                        diffLessThan(corners[2],contact.getWorldManifold().getPoints()[1],0.1f)){
                        //TL and point 0    BL and point 1
                        return true;
                    }else if(diffLessThan(corners[3],contact.getWorldManifold().getPoints()[1],0.1f)&&
                        diffLessThan(corners[2],contact.getWorldManifold().getPoints()[0],0.1f)){
                        //TL and point 1    BL and point 0
                        return true;
                    }
                }
            }
        }
        return false;
    }
    boolean diffLessThan(float a, float b, float threshold){
        return Math.abs(a-b)<threshold;
    }
    boolean diffLessThan(Vector2 a, Vector2 b, float threshold){
        return Math.abs(a.x-b.x)<threshold&&Math.abs(a.y-b.y)<threshold;
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
