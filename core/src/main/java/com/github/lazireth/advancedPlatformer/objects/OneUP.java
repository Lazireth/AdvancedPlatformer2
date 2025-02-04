package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.lazireth.advancedPlatformer.Area;
import com.github.lazireth.advancedPlatformer.Direction;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.MovementStep;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.TimedMovement;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.advancedPlatformer.objects.timedMovement.CollisionFlag.*;

public class OneUP extends InteractableObject {
    final float WIDTH;
    final float HEIGHT;
    final TextureRegion mySprite;
    float x,y;
    float initialY;
    float moveSpeed=2.0f;

    boolean toCollect=false;
    TimedMovement timedMovement;
    public Direction directionToBounceTo=null;
    Area area;
    public OneUP(float inX, float inY, Area area){
        this.area=area;
        x=inX;
        y=inY;
        initialY=y;
        mySprite = getSpritesFor("OneUP").getFirst();
        WIDTH = mySprite.getRegionWidth()  * GameCore.metersPerPixel;
        HEIGHT = mySprite.getRegionHeight()* GameCore.metersPerPixel;
        area.interactableObjectsAdd.add(this);
        addToWorld();

        ArrayList<MovementStep> movementSteps=new ArrayList<>();
        movementSteps.addLast(new MovementStep(0,1,0, OFF));
        movementSteps.addLast(new MovementStep(2,0,0.75f, ON));
        timedMovement=new TimedMovement(movementSteps,body,true, BodyDef.BodyType.DynamicBody);
    }
    public void levelReset(){
        body.getWorld().destroyBody(body);
        area.interactableObjectsRemove.add(this);
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
    public void update(float delta) {
        if(toCollect){

            area.player.collectItem("OneUP");

            body.getWorld().destroyBody(body);
            area.interactableObjectsRemove.add(this);
        }
        timedMovement.update(delta);
        if(timedMovement.finished){
            if(body.getLinearVelocity().x==0){
                bounce();
            }
        }
    }
    //todo
    //actual make it do something
    public void startInteractionWithPlayer(Player player) {
        toCollect=true;
        System.out.println("collected 1UP");
    }

    private void addToWorld() {
        Rectangle rectangle = new Rectangle(x,y,WIDTH,HEIGHT);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.fixedRotation=true;
        bodyDef.position.set(rectangle.x,rectangle.y);

        body = area.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH/2, HEIGHT/2);

        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.friction=0;
        fixtureDef.density=0.1f;
        fixtureDef.isSensor= true;

        body.createFixture(fixtureDef).setUserData(this);

        shape.setAsBox(0.1f,HEIGHT/2-0.05f,new Vector2(-WIDTH/2,0),0);
        fixtureDef.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,(short)(FilterCategory.WALL.categoryBits | FilterCategory.ENEMY.categoryBits));
        body.createFixture(fixtureDef).setUserData(new ObjectSensor("oneUPLeftSide",this));

        shape.setAsBox(0.1f,HEIGHT/2-0.05f,new Vector2(WIDTH/2,0),0);
        fixtureDef.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,(short)(FilterCategory.WALL.categoryBits | FilterCategory.ENEMY.categoryBits));
        body.createFixture(fixtureDef).setUserData(new ObjectSensor("oneUPRightSide",this));
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
