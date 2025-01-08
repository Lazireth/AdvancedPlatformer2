package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

public class Mushroom extends Bounceable {
    final float WIDTH;
    final float HEIGHT;
    final TextureRegion mySprite;
    float x,y;
    float initialY;

    boolean doBounce=false;
    boolean toCollect=false;
    public Mushroom(float inX, float inY, TextureRegion mySprite){
        // todo
        // gets stuck on interactable blocks
        x=inX;
        y=inY;
        initialY=y;
        this.mySprite = mySprite;
        WIDTH = mySprite.getRegionWidth()  * GameCore.unitsPerPixel;
        HEIGHT = mySprite.getRegionHeight()* GameCore.unitsPerPixel;
        GameCore.gameScreen.level.interactableObjectsAdd.add(this);
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
    public void update() {
        if(toCollect){

            GameScreen.player.collectItem("Mushroom");

            body.getWorld().destroyBody(body);
            GameCore.gameScreen.level.interactableObjectsRemove.add(this);
            return;
        }
        if(body==null){
            y+=1/32.0f;
            if(y>initialY+HEIGHT){
                addToWorld();
                body.setLinearVelocity(new Vector2(2f,0));
            }
        }else if(!doBounce){
            y=body.getPosition().y;
        }
    }

    //todo
    //actual make it do something
    public void startInteraction(Player player) {
        toCollect=true;
        System.out.println("collected Mushroom");
    }

    private void addToWorld() {
        Rectangle rectangle = new Rectangle(x,y,WIDTH,HEIGHT);


        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(rectangle.x,rectangle.y);

        body = GameScreen.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH/2, HEIGHT/2);

        FixtureDef fixtureDefRect=new FixtureDef();
        fixtureDefRect.shape=shape;
        fixtureDefRect.friction=0;
        fixtureDefRect.density=0.1f;

        body.createFixture(fixtureDefRect);
        body.setUserData(this);

        shape.dispose();
    }
}
