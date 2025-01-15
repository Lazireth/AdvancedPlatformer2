package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

public class Mushroom extends InteractableObject {
    final float WIDTH;
    final float HEIGHT;
    final TextureRegion mySprite;
    float x,y;
    float initialY;
    float moveSpeed=1.0f;

    boolean doBounce=false;
    boolean toCollect=false;
    public Mushroom(float inX, float inY){
        // todo
        // gets stuck on interactable blocks
        x=inX;
        y=inY;
        initialY=y;
        mySprite = getSpritesFor("Mushroom").getFirst();
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
    public void update(float delta) {
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
    public void startInteractionWithPlayer(Player player) {
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
    public void bounce(InteractableObject object, Contact contact){
        System.out.println("bounce");

        if(checksIfObjectShouldBounce(object,contact)){
            if(body.getLinearVelocity().x<0){
                body.setLinearVelocity(moveSpeed,0);
            }else{
                body.setLinearVelocity(-moveSpeed,0);
            }
        }
    }
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
}
