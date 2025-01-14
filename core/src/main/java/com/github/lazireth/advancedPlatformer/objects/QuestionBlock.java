package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Level;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;
import java.util.Map;

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

    public QuestionBlock(TiledMapTileMapObject questionBlock, Level level){
        this.questionBlock = questionBlock;
        try{
            heldObject=questionBlock.getProperties().get("Held Object",String.class);
        } catch (Exception e) {
            heldObject=null;
        }
        // load sprites

        sprites=getSpritesFor("QuestionBlock");

        //get with and convert from pixel to game units
        WIDTH = sprites.getFirst().getRegionWidth()  * GameCore.unitsPerPixel;
        HEIGHT = sprites.getFirst().getRegionHeight()* GameCore.unitsPerPixel;

        // build collision
        addToWorld();

        // needed for interactions
        body.setUserData(this);
    }

    private void dropItem(){
        if(heldObject==null){
            return;
        }
        switch(heldObject){
            case "Mushroom"->new Mushroom(getXPosition(),getYPosition()+yPositionModifier);
            case "OneUP"->new OneUP(getXPosition(),getYPosition()+yPositionModifier);
        }
    }

    @Override
    public void update() {
        if(ticksAfterInteraction>=0&&ticksAfterInteraction<=24){
            if(ticksAfterInteraction<12){
                yPositionModifier+=1/48.0f;
                if(ticksAfterInteraction==11){
                    dropItem();
                }
            }else
            if(ticksAfterInteraction<24){
                yPositionModifier-=1/48.0f;

            }else{
                currentSprite=1;
                yPositionModifier=0;
            }
            ticksAfterInteraction++;
        }
    }


    public void startInteraction(Player player){
        if(player.getYVelocity()>0&&player.getYPosition()+Player.HEIGHT/2<getYPosition()-HEIGHT/2&&ticksAfterInteraction==-1){
            ticksAfterInteraction=0;
        }
    }
    private void addToWorld() {
        Rectangle rectangle = new Rectangle(questionBlock.getX()/GameCore.pixelsPerUnit,questionBlock.getY()/GameCore.pixelsPerUnit,WIDTH,HEIGHT);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH/2, HEIGHT/2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(rectangle.x + rectangle.width/2 , rectangle.y + HEIGHT/2);
        body = GameScreen.world.createBody(bodyDef);
        body.createFixture(shape, 0);
    }
    public void render(TextureMapObjectRenderer renderer){
        renderer.renderObject(sprites.get(currentSprite), body.getPosition().x, body.getPosition().y+yPositionModifier,WIDTH,HEIGHT);
    }
    float getYVelocity(){
        return body.getLinearVelocity().y;
    }
    float getXVelocity(){
        return body.getLinearVelocity().x;
    }
    float getYPosition(){
        return body.getPosition().y;
    }
    float getXPosition(){
        return body.getPosition().x;
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
