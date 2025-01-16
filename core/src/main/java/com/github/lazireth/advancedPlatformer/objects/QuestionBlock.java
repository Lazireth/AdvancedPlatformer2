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
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

public class QuestionBlock extends InteractableObject{
    final float WIDTH;
    final float HEIGHT;

    private Body body;
    int ticksAfterInteraction=-1;
    float yPositionModifier=0;

    TiledMapTileMapObject questionBlock;

    int currentSprite=0;
    public ArrayList<TextureRegion> sprites;

    String heldObject;

    int animationState=0;
    //0 is before animation
    //1 is going up
    //2 is going down
    //3 is after animation
    float[] animationVelocities={0,1.25f,-1.25f,0};
    float[] animationPositions ={0,0.25f,0,0};
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

        // needed for interactions
        body.setUserData(this);
    }

    private void dropItem(){
        switch(heldObject){
            case "Mushroom"->new Mushroom(getXPosition(),getYPosition()+yPositionModifier);
            case "OneUP"->new OneUP(getXPosition(),getYPosition()+yPositionModifier);
        }
    }
    float deltaSum=0;
    @Override
    public void update(float delta) {
        if(ticksAfterInteraction>=0&&ticksAfterInteraction<=24){
            if(ticksAfterInteraction==0){
                System.out.println("nano "+System.nanoTime());
            }
            if(ticksAfterInteraction<12){
                deltaSum+=delta;
                System.out.println("\ndelta "+delta);
                System.out.println(3.7756023596f*delta);
                yPositionModifier+=1.25f*delta;
                if(ticksAfterInteraction==11){
                    System.out.println("deltaSum "+deltaSum);
                    System.out.println("nano "+System.nanoTime());
                    dropItem();
                }
            }else
            if(ticksAfterInteraction<24){
                System.out.println("yPositionModifier "+yPositionModifier);
                yPositionModifier-=1.25f*delta;

            }else{
                currentSprite=1;
                yPositionModifier=0;
            }
            ticksAfterInteraction++;
        }
    }
    public void update2(float delta) {
        if(animationState==1){
            body.setLinearVelocity(0,animationVelocities[animationState]);
        }
        if(animationState==2){
            body.setLinearVelocity(0,animationVelocities[animationState]);
        }

        if(ticksAfterInteraction>=0&&ticksAfterInteraction<=24){
            if(ticksAfterInteraction==0){
                System.out.println("nano "+System.nanoTime());
            }
            if(ticksAfterInteraction<12){
                deltaSum+=delta;
                System.out.println("\ndelta "+delta);
                System.out.println(3.7756023596f*delta);
                yPositionModifier+=1.25f*delta;
                if(ticksAfterInteraction==11){
                    System.out.println("deltaSum "+deltaSum);
                    System.out.println("nano "+System.nanoTime());
                    dropItem();
                }
            }else
            if(ticksAfterInteraction<24){
                System.out.println("yPositionModifier "+yPositionModifier);
                yPositionModifier-=1.25f*delta;

            }else{
                currentSprite=1;
                yPositionModifier=0;
            }
            ticksAfterInteraction++;
        }
    }

    public void startInteractionWithPlayer(Player player){
        if(player.getYPosition()+Player.HEIGHT/2<getYPosition()-HEIGHT/2&&ticksAfterInteraction==-1){
            ticksAfterInteraction=0;
            animationState=1;
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

        body.createFixture(fixtureDefRect);
        body.setUserData(this);

        shape.dispose();
    }
    public void render(TextureMapObjectRenderer renderer){
        renderer.renderObject(sprites.get(currentSprite), getXPosition(), getYPosition()+yPositionModifier,WIDTH,HEIGHT);
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
