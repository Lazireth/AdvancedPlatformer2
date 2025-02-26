package com.github.lazireth.AdvancedPlatformer2.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.AdvancedPlatformer2.LevelMap;
import com.github.lazireth.AdvancedPlatformer2.Player;
import com.github.lazireth.AdvancedPlatformer2.objects.timedMovement.MovementStep;
import com.github.lazireth.AdvancedPlatformer2.objects.timedMovement.TimedMovement;
import com.github.lazireth.AdvancedPlatformer2.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.AdvancedPlatformer2.objects.timedMovement.CollisionFlag.NONE;
import static com.github.lazireth.AdvancedPlatformer2.GameCore.GlobalVariables.*;

public class Brick extends InteractableObject{
    final float WIDTH;
    final float HEIGHT;
    TiledMapTileMapObject brick;

    int currentSprite=0;
    public ArrayList<TextureRegion> sprites;

    String heldObject;

    TimedMovement timedMovement;
    LevelMap levelMap;
    public Brick(TiledMapTileMapObject brick, LevelMap levelMap){
        this.brick=brick;
        this.levelMap=levelMap;
        try{
            heldObject=brick.getProperties().get("Held Object",String.class);
        } catch (Exception e) {
            heldObject=null;
        }
        sprites=getSpritesFor("Brick");

        WIDTH = pixelsToMeters(sprites.getFirst().getRegionWidth());
        HEIGHT= pixelsToMeters(sprites.getFirst().getRegionHeight());

        // create sensor
        makeSensor();

        // set up timedMovement
        ArrayList<MovementStep> movementSteps=new ArrayList<>();
        movementSteps.addLast(new MovementStep(0,1,0, NONE));
        movementSteps.addLast(new MovementStep(0,-1,0.25f, NONE));
        movementSteps.addLast(new MovementStep(0,0,0.5f, NONE));
        timedMovement=new TimedMovement(movementSteps,body);
        timedMovement.autoResetTimedMovement(true);
    }

    @Override
    public void render(TextureMapObjectRenderer renderer){
        renderer.renderObject(sprites.get(currentSprite), getXPosition(), getYPosition(),WIDTH,HEIGHT);
    }

    @Override
    public void update(float delta) {
        timedMovement.update(delta);
        if(timedMovement.currentMovementStep==1){
            //dropItem();
        }
    }

    @Override
    public void startInteractionWithPlayer(Player player) {
        if(player.getYPosition()+player.HEIGHT/2<getYPosition()-HEIGHT/2&&!timedMovement.running){
            timedMovement.start();
        }
    }

    public void levelReset() {
        // create sensor
        makeSensor();

        // set up timedMovement
        ArrayList<MovementStep> movementSteps=new ArrayList<>();
        movementSteps.addLast(new MovementStep(0,1,0, NONE));
        movementSteps.addLast(new MovementStep(0,-1,0.25f, NONE));
        movementSteps.addLast(new MovementStep(0,0,0.5f, NONE));
        timedMovement=new TimedMovement(movementSteps,body);
        timedMovement.autoResetTimedMovement(true);
    }
    private void makeSensor() {
        float width=WIDTH+ 2*metersPerPixel;//need to be slightly larger so they can be touched by the player
        float height=HEIGHT+ 2*metersPerPixel;
        float x= pixelsToMeters(brick.getX());
        float y= pixelsToMeters(brick.getY());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x+WIDTH/2.0f,y+HEIGHT/2.0f);


        body = LevelMap.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2.0f, height/2.0f);

        FixtureDef fixtureDefRect=new FixtureDef();
        fixtureDefRect.shape=shape;
        fixtureDefRect.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDefRect.filter,FilterCategory.PLAYER);

        body.createFixture(fixtureDefRect).setUserData(this);

        shape.dispose();
    }
    float getYPosition(){
        //body position got changed because detection box is slightly larger than sprite
        return body.getPosition().y;
    }
    float getXPosition(){
        //body position got changed because detection box is slightly larger than sprite
        return body.getPosition().x;
    }
}
