package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.CollisionFlag;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.MovementStep;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.TimedMovement;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.advancedPlatformer.objects.timedMovement.CollisionFlag.NONE;

public class QuestionBlock extends InteractableObject{
    final float WIDTH;
    final float HEIGHT;
    TiledMapTileMapObject questionBlock;

    int currentSprite=0;
    public ArrayList<TextureRegion> sprites;

    String heldObject;

    TimedMovement timedMovement;
    public QuestionBlock(TiledMapTileMapObject questionBlock){
        this.questionBlock = questionBlock;
        try{
            heldObject=questionBlock.getProperties().get("Held Object",String.class);
        } catch (Exception e) {
            heldObject=null;
        }

        sprites=getSpritesFor("QuestionBlock");

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
    }

    private void dropItem(){
        switch(heldObject){
            case "Mushroom"->new Mushroom(getXPosition(),getYPosition());
            case "OneUP"->new OneUP(getXPosition(),getYPosition());
            case null, default -> {}
        }
        heldObject=null;
    }
    @Override
    public void update(float delta) {
        timedMovement.update(delta);
        if(timedMovement.currentMovementStep==1){
            dropItem();
            currentSprite=1;
        }
    }

    public void startInteractionWithPlayer(Player player){
        if(player.getYPosition()+Player.HEIGHT/2<getYPosition()-HEIGHT/2&&!timedMovement.started){
            timedMovement.start();
        }
    }
    private void makeSensor() {
        float width=WIDTH+ 2*GameCore.metersPerPixel;//need to be slightly larger so they can be touched by the player
        float height=HEIGHT+ 2*GameCore.metersPerPixel;
        float x=pixelsToUnits(questionBlock.getX());
        float y=pixelsToUnits(questionBlock.getY());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x+width/2.0f,y+height/2.0f);


        body = GameScreen.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2.0f, height/2.0f);

        FixtureDef fixtureDefRect=new FixtureDef();
        fixtureDefRect.shape=shape;
        fixtureDefRect.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDefRect.filter,FilterCategory.PLAYER);

        body.createFixture(fixtureDefRect);
        body.setUserData(this);

        shape.dispose();
    }
    public void render(TextureMapObjectRenderer renderer){
        renderer.renderObject(sprites.get(currentSprite), getXPosition(), getYPosition(),WIDTH,HEIGHT);
    }
    float getYVelocity(){
        return body.getLinearVelocity().y;
    }
    float getXVelocity(){
        return body.getLinearVelocity().x;
    }
    float getYPosition(){
        //body position got changed because detection box is slightly larger than sprite
        return body.getPosition().y-GameCore.metersPerPixel;
    }
    float getXPosition(){
        //body position got changed because detection box is slightly larger than sprite
        return body.getPosition().x-GameCore.metersPerPixel;
    }
    public void setVelocity(Vector2 velocity) {
        body.setLinearVelocity(velocity);
    }
    public void setVelocity(float dx, float dy) {
        setVelocity(new Vector2(dx,dy));
    }
    Vector2 getVelocity(){
        return body.getLinearVelocity();
    }
    Vector2 getPosition(){
        return body.getPosition();
    }
}
