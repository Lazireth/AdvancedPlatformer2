package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.physics.box2d.*;
import com.github.lazireth.advancedPlatformer.objects.InteractableObject;
import com.github.lazireth.advancedPlatformer.objects.Mushroom;
import com.github.lazireth.advancedPlatformer.objects.OneUP;

public class CollisionListener implements ContactListener {


    @Override
    /// Called when two fixtures begin to touch
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
            zeroNulls(fixtureA, fixtureB, userDataA, userDataB,contact);
        }
    }
    private void zeroNulls(Fixture fixtureA, Fixture fixtureB, Object userDataA, Object userDataB,Contact contact){
        if(playerCollision(userDataA, userDataB)){
            return;
        }
        contact.setEnabled(false);
        // add other cases where both fixtureA and B need to be not null here
    }
    private void oneNull(Fixture goodFixture, Fixture nullFixture, Object object, Contact contact){
        switch (object){
            case Mushroom mushroom -> mushroom.bounce((InteractableObject)object,contact);
            case OneUP oneUP -> oneUP.bounce((InteractableObject)object,contact);
            case Player player ->player.preserveVelocityWhenLanding=true;
            case null, default -> {}
        }
    }
    // I know this method is big and has a lot of if statements, but I don't think it can get much better with the current approach
    private boolean playerCollision(Object objectA, Object objectB){
        System.out.println("playerCollision");
        if(objectA.getClass().equals(Player.class)){//true if objectA is a player
            if(objectB instanceof InteractableObject){
                ((InteractableObject)objectB).startInteractionWithPlayer((Player)objectA);
            }
            return true;
        }else
        if(objectB.getClass().equals(Player.class)){//true if objectB is a player
            if(objectA instanceof InteractableObject){
                ((InteractableObject)objectA).startInteractionWithPlayer((Player)objectB);
            }
            return true;
        }
        return false;
    }
















    @Override
    /// Called when two fixtures cease to touch
    public void endContact(Contact contact) {

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
