package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.zip.Deflater;

import static com.github.lazireth.advancedPlatformer.InputHandler.keys;

public class Player{
    public static float WIDTH;
    public static float HEIGHT;

    static final float DENSITY=1;
    static final float FRICTION=0.8f;
    float MASS;

    private static final float MAX_RIGHT_WALK_SPEED=5;
    private static final float MAX_RIGHT_RUN_SPEED=9;
    private static final float MAX_LEFT_WALK_SPEED=-5;
    private static final float MAX_LEFT_RUN_SPEED=-9;

    private static final float MAX_RIGHT_VELOCITY=9;// m/s
    private static final float MAX_LEFT_VELOCITY=-9;// m/s
    private static final float MIN_VELOCITY=0.2f;   // m/s

    static final int TIME_ON_GROUND_BEFORE_JUMP=5;
    private Vector2 PLAYER_JUMP_IMPULSE;
    private static final int JUMP_TICKS=6;
    private Vector2 PLAYER_MOVE_LEFT_IMPULSE;
    private Vector2 PLAYER_MOVE_RIGHT_IMPULSE;

    boolean canJump=true;//player can jump after their vertical velocity has been 0 for 5 frames
    int framesOnGround=0;
    int spriteState=0;
    int health=1;
    int lives=5;

    private Body body;
    public TiledMapTile[]  playerMapTiles;
    public TextureRegion[] playerTextures;
    public Vector2[] playerTextureSizes;

    private Vector2 lastVelocity=new Vector2(0,0);
    private Vector2 lastPosition=new Vector2(0,0);
    boolean preserveVelocityWhenLanding=false;
    boolean preservedVelocityWhenLandingLastTick=false;

    int ticksJumping;
    boolean isJumping;
    Vector2 startingPosition;//relative to the bottom left of the player

    boolean isRunning;

    public Player(Vector2 startingPosition, TiledMapTile[] playerMapTiles){
        this.startingPosition=startingPosition;
        this.playerMapTiles=playerMapTiles;

        playerTextures=new TextureRegion[playerMapTiles.length];
        playerTextureSizes=new Vector2[playerMapTiles.length];
        for(int i=0;i<playerMapTiles.length;i++){
            playerTextures[i]=playerMapTiles[i].getTextureRegion();
            playerTextureSizes[i]=new Vector2(playerTextures[i].getRegionWidth() * GameCore.unitsPerPixel, playerTextures[i].getRegionHeight() * GameCore.unitsPerPixel);

        }
        addToWorld(startingPosition);
    }
    public void input(float delta){
        if(keys[Input.Keys.L]){
            keys[Input.Keys.L]=false;
            ScreenshotFactory.saveScreenshot();
        }
        if(isJumping){
            if(keys[Input.Keys.W]){
                if(ticksJumping<JUMP_TICKS){
                    body.applyLinearImpulse(PLAYER_JUMP_IMPULSE,body.getPosition(),true);
                    ticksJumping++;
                }else{
                    isJumping =false;
                }
            }else{
                isJumping =false;
            }
        }
        if(canJump){
            if(body.getLinearVelocity().y!=0){
                canJump=false;
                framesOnGround=0;
            }else
            if(keys[Input.Keys.W]){
                body.applyLinearImpulse(PLAYER_JUMP_IMPULSE,body.getPosition(),true);
                canJump=false;
                isJumping =true;
                ticksJumping=1;
                framesOnGround=0;
            }
        }else if(body.getLinearVelocity().y==0){
            framesOnGround++;
            if(framesOnGround>=TIME_ON_GROUND_BEFORE_JUMP){
                canJump=true;
            }
        }

        if(keys[Input.Keys.A]&&!keys[Input.Keys.D]){//check right
            if(isRunning){
                if(getXVelocity()>MAX_LEFT_RUN_SPEED){
                    body.applyLinearImpulse(PLAYER_MOVE_LEFT_IMPULSE,getPosition(),true);
                }
            }else{
                if(getXVelocity()>MAX_LEFT_WALK_SPEED){
                    body.applyLinearImpulse(PLAYER_MOVE_LEFT_IMPULSE,getPosition(),true);
                }else if(keys[Input.Keys.K]){
                    body.applyLinearImpulse(PLAYER_MOVE_LEFT_IMPULSE,getPosition(),true);
                    isRunning=true;
                }
            }
        }
        if(keys[Input.Keys.D]&&!keys[Input.Keys.A]){//check right
            if(isRunning){
                if(getXVelocity()<MAX_RIGHT_RUN_SPEED){
                    body.applyLinearImpulse(PLAYER_MOVE_RIGHT_IMPULSE,getPosition(),true);
                }
            }else{
                if(getXVelocity()<MAX_RIGHT_WALK_SPEED){
                    body.applyLinearImpulse(PLAYER_MOVE_RIGHT_IMPULSE,getPosition(),true);
                }else if(keys[Input.Keys.K]){
                    body.applyLinearImpulse(PLAYER_MOVE_RIGHT_IMPULSE,getPosition(),true);
                    isRunning=true;
                }
            }
        }

        if(Math.abs(getXVelocity())<MAX_RIGHT_RUN_SPEED){
            isRunning=false;
        }
        if(!keys[Input.Keys.A]&&!keys[Input.Keys.D]&&Math.abs(body.getLinearVelocity().x)<MIN_VELOCITY){
            body.setLinearVelocity(0,body.getLinearVelocity().y);
        }
    }
    public void render(TextureMapObjectRenderer renderer){
        switch (spriteState){
            case 0->{
                renderer.renderObject(playerTextures[0],getXPosition(),getYPosition(),playerTextureSizes[0].x,playerTextureSizes[0].y);
            }
            case 1->{
                renderer.renderObject(playerTextures[1],getXPosition(),getYPosition()+playerTextureSizes[1].y/2,playerTextureSizes[1].x,playerTextureSizes[1].y);//top tile
                renderer.renderObject(playerTextures[2],getXPosition(),getYPosition()-playerTextureSizes[1].y/2,playerTextureSizes[2].x,playerTextureSizes[2].y);//bottom tile
            }
        }

    }
    public boolean checkIfFallingOffMap(){
        return body.getPosition().y<-1;
    }
    public void manageFallingOffMap(){
        System.out.println("death by falling off map");
        resetPlayer();
        lives--;
    }
    public void resetPlayer(){
        resetPlayer(startingPosition);
    }
    public void resetPlayer(Vector2 locationToSpawnAt){
        canJump=true;
        framesOnGround=0;
        spriteState=0;
        health=1;
        ticksJumping=0;
        isJumping =false;
        body.getWorld().destroyBody(body);
        addToWorld(locationToSpawnAt);
        GameCore.cameraPos=new Vector3(locationToSpawnAt.x,GameCore.camera.position.y,GameCore.camera.position.z);
    }

    public void update(float delta){
        if(preservedVelocityWhenLandingLastTick){
            preservedVelocityWhenLandingLastTick=false;
            preserveVelocityWhenLanding=false;
            return;
        }
        if(preserveVelocityWhenLanding){
            if(lastVelocity.y<1&&Math.abs(body.getLinearVelocity().y)<0.1){// need Math.abs(...)<0.1 instead of ...==0 in case ... is an incredibly small value
                body.setLinearVelocity(lastVelocity.x,body.getLinearVelocity().y);
            }
            preservedVelocityWhenLandingLastTick=true;
            preserveVelocityWhenLanding=false;
        }

        lastVelocity=body.getLinearVelocity().cpy();
        lastPosition=body.getPosition().cpy();
    }
    public void wallCollide(){
        preserveVelocityWhenLanding=true;
    }
    public void collectMushroom(){
        if(health>1){
            //add score
            return;
        }
        Vector2 startingPosition=body.getPosition().add(0,0.5f);
        body.getWorld().destroyBody(body);
        spriteState=1;
        health=2;
        addToWorld(startingPosition);
    }

    private void addToWorld(Vector2 startingPosition) {
        WIDTH = (playerMapTiles[spriteState].getProperties().get("WIDTH",int.class)-2)  * GameCore.unitsPerPixel;
        HEIGHT= (playerMapTiles[spriteState].getProperties().get("HEIGHT",int.class)-2) * GameCore.unitsPerPixel;
        startingPosition.add(0.5f,HEIGHT/2);//cannot do WIDTH/2 for x because player is 0.75 units wide

        PolygonShape rectangle=new PolygonShape();
        rectangle.setAsBox(WIDTH/2,HEIGHT/2);

        MASS=WIDTH*HEIGHT*DENSITY;

        PLAYER_JUMP_IMPULSE=new Vector2(0,15*MASS/JUMP_TICKS);
        PLAYER_MOVE_LEFT_IMPULSE=new Vector2(-0.6f*MASS,0);
        PLAYER_MOVE_RIGHT_IMPULSE=new Vector2(0.6f*MASS,0);

        BodyDef bodyDef=new BodyDef();
        bodyDef.type=BodyType.DynamicBody;
        bodyDef.fixedRotation=true;
        bodyDef.position.set(startingPosition);

        body= GameScreen.world.createBody(bodyDef);


        FixtureDef fixtureDefRect=new FixtureDef();
        fixtureDefRect.shape=rectangle;
        fixtureDefRect.density=DENSITY;
        fixtureDefRect.friction=FRICTION;

        body.createFixture(fixtureDefRect);
        body.setUserData(this);

        rectangle.dispose();
    }

    public float getYVelocity(){return body.getLinearVelocity().y;}
    public float getXVelocity(){return body.getLinearVelocity().x;}
    public Vector2 getVelocity(){return body.getLinearVelocity().cpy();}
    public float getYPosition(){return body.getPosition().y;}
    public float getXPosition(){return body.getPosition().x;}
    public Vector2 getPosition(){return body.getPosition().cpy();}
}
