package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.lazireth.advancedPlatformer.Direction;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.MovementStep;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.TimedMovement;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.advancedPlatformer.objects.timedMovement.CollisionFlag.ON;
import static com.github.lazireth.advancedPlatformer.objects.timedMovement.CollisionFlag.OFF;

public class Mushroom extends InteractableObject {
    final float WIDTH;
    final float HEIGHT;
    final TextureRegion mySprite;
    float x,y;
    float moveSpeed=2.0f;

    boolean toCollect=false;

    TimedMovement timedMovement;
    public Direction directionToBounceTo=null;
    public Mushroom(float inX, float inY){
        // todo
        // gets stuck on interactable blocks
        x=inX;
        y=inY;
        mySprite = getSpritesFor("Mushroom").getFirst();
        WIDTH = mySprite.getRegionWidth()  * GameCore.metersPerPixel;
        HEIGHT = mySprite.getRegionHeight()* GameCore.metersPerPixel;
        GameCore.gameScreen.level.interactableObjectsAdd.add(this);
        addToWorld(BodyDef.BodyType.KinematicBody,true);

        ArrayList<MovementStep> movementSteps=new ArrayList<>();
        movementSteps.addLast(new MovementStep(0,1,0, OFF));
        movementSteps.addLast(new MovementStep(2,0,0.75f, ON));
        timedMovement=new TimedMovement(movementSteps,body,true);
    }
    public void levelReset(){
        body.getWorld().destroyBody(body);
        GameCore.gameScreen.level.interactableObjectsRemove.add(this);
    }
    @Override
    public void render(TextureMapObjectRenderer renderer) {
        if(body==null){
            renderer.renderObject(mySprite,x,y,WIDTH,HEIGHT);
        }else{
            renderer.renderObject(mySprite,body.getPosition(),WIDTH,HEIGHT);
        }
    }

    @Override
    public boolean update(float delta) {

        if(toCollect){

            GameScreen.player.collectItem("Mushroom");

            body.getWorld().destroyBody(body);
            GameCore.gameScreen.level.interactableObjectsRemove.add(this);
            return false;
        }
        timedMovement.update(delta);
        if(timedMovement.finished&&body.getType().equals(BodyDef.BodyType.KinematicBody)){
            x=body.getPosition().x;
            y=body.getPosition().y;
            body.getWorld().destroyBody(body);
            addToWorld(BodyDef.BodyType.DynamicBody,false);
            body.setLinearVelocity(2,0);
        }
        if(timedMovement.finished&&body.getLinearVelocity().x==0){
            bounce();
        }
        return false;
    }

    //todo
    //actual make it do something
    public void startInteractionWithPlayer(Player player) {
        toCollect=true;
        System.out.println("collected Mushroom");
    }
    private void addToWorld(BodyDef.BodyType bodyType,boolean isSensor) {
        Rectangle rectangle = new Rectangle(x,y,WIDTH,HEIGHT);


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.fixedRotation=true;
        bodyDef.position.set(rectangle.x,rectangle.y);

        body = GameScreen.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH/2, HEIGHT/2);

        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.friction=0;
        fixtureDef.density=0.1f;
        fixtureDef.isSensor=isSensor;
        FilterCategory.ITEM.makeFilter(fixtureDef.filter);

        body.createFixture(fixtureDef).setUserData(this);

        shape.setAsBox(0.1f,HEIGHT/2-0.05f,new Vector2(-WIDTH/2,0),0);
        fixtureDef.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,(short)(FilterCategory.WALL.categoryBits | FilterCategory.ENEMY.categoryBits));
        body.createFixture(fixtureDef).setUserData(new ObjectSensor("mushroomLeftSide",this));

        shape.setAsBox(0.1f,HEIGHT/2-0.05f,new Vector2(WIDTH/2,0),0);
        fixtureDef.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,(short)(FilterCategory.WALL.categoryBits | FilterCategory.ENEMY.categoryBits));
        body.createFixture(fixtureDef).setUserData(new ObjectSensor("mushroomRightSide",this));
        shape.dispose();
    }
    private void bounce(){
        switch (directionToBounceTo){
            case LEFT -> body.setLinearVelocity(-moveSpeed,0);
            case RIGHT -> body.setLinearVelocity(moveSpeed,0);
            case null, default -> {}
        }
        directionToBounceTo=null;
    }
}
