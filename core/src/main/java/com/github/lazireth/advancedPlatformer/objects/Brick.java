package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.MovementStep;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.TimedMovement;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.advancedPlatformer.objects.timedMovement.CollisionFlag.NONE;

public class Brick extends InteractableObject{
    final float WIDTH;
    final float HEIGHT;
    TiledMapTileMapObject brick;

    int currentSprite=0;
    public ArrayList<TextureRegion> sprites;

    String heldObject;

    TimedMovement timedMovement;
    public Brick(TiledMapTileMapObject brick){
        this.brick=brick;
        try{
            heldObject=brick.getProperties().get("Held Object",String.class);
        } catch (Exception e) {
            heldObject=null;
        }
        sprites=getSpritesFor("Brick");

        WIDTH = pixelsToUnits(sprites.getFirst().getRegionWidth());
        HEIGHT= pixelsToUnits(sprites.getFirst().getRegionHeight());

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
        if(player.getYPosition()+Player.HEIGHT/2<getYPosition()-HEIGHT/2&&!timedMovement.running){
            timedMovement.start();
        }
    }

    @Override
    public void levelReset() {}
    private void makeSensor() {
        float width=WIDTH+ 2*GameCore.metersPerPixel;//need to be slightly larger so they can be touched by the player
        float height=HEIGHT+ 2*GameCore.metersPerPixel;
        float x=pixelsToUnits(brick.getX());
        float y=pixelsToUnits(brick.getY());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x+WIDTH/2.0f,y+HEIGHT/2.0f);


        body = GameScreen.world.createBody(bodyDef);
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
