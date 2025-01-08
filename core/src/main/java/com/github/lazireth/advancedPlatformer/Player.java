package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;


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

    int state=0;
    // 0 is normal
    // 1 is big (caused by mushroom)

    boolean canJump=true;//player can jump after their vertical velocity has been 0 for 5 frames
    int framesOnGround=0;
    int health=1;
    int lives=5;

    private Body body;

    public TiledMapTile[][]  playerSpriteTiles;
    public TextureRegion[][] playerSprites;


    private Vector2 lastVelocity=new Vector2(0,0);
    private Vector2 lastPosition=new Vector2(0,0);
    boolean preserveVelocityWhenLanding=false;
    boolean preservedVelocityWhenLandingLastTick=false;

    int ticksJumping;
    boolean isJumping;
    Vector2 startingPosition;//relative to the bottom left of the player

    boolean isRunning;

    public Player(TiledMapTileMapObject playerObject, TiledMapTileSet playerSpriteTileSet){
        startingPosition=new Vector2(playerObject.getX()*GameCore.unitsPerPixel,playerObject.getY()*GameCore.unitsPerPixel);

        int maxPlayerState = 0; int maxStateIndex=0;
        for(TiledMapTile tile:playerSpriteTileSet){//gets the largest of player state and the max number of sprites associated with a state
            try{
                if(tile.getProperties().get("Player State",int.class)>maxPlayerState){
                    maxPlayerState=tile.getProperties().get("Player State",int.class);
                }
                if(tile.getProperties().get("State Index",int.class)>maxStateIndex){
                    maxStateIndex=tile.getProperties().get("State Index",int.class);
                }
            } catch (Exception ignore) {}
        }
        playerSpriteTiles=new TiledMapTile[maxPlayerState+1][maxStateIndex+1];// +1 because arrays need to big 1 bigger than their largest index
        playerSprites=new TextureRegion[maxPlayerState+1][maxStateIndex+1];

        for(TiledMapTile tile:playerSpriteTileSet){//gets the largest of player state and the max number of sprites associated with a state
            try{
                playerSpriteTiles[tile.getProperties().get("Player State",int.class)][tile.getProperties().get("State Index",int.class)]
                    =tile;
                playerSprites[tile.getProperties().get("Player State",int.class)][tile.getProperties().get("State Index",int.class)]
                    =tile.getTextureRegion();
            } catch (Exception ignore) {}
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
        switch (state){
            case 0->{
                renderer.renderObject(playerSprites[0][0],getXPosition(),getYPosition(),1,1);//width and height are in gameUnits
            }
            case 1->{
                renderer.renderObject(playerSprites[1][0],getXPosition(),getYPosition()+0.5f,1,1);//top tile
                renderer.renderObject(playerSprites[1][1],getXPosition(),getYPosition()-0.5f,1,1);//bottom tile
            }
        }

    }
    public boolean checkIfFallingOffMap(){
        return body.getPosition().y<-1;
    }
    public void manageFallingOffMap(){
        System.out.println("death by falling off map");
        death();
    }
    // todo
    // implement GameOver
    public void death(){
        lives--;
        if(lives<0){
            System.out.println("You ran out of lives");
            //loadScreenGameOver();
            //return;
        }
        resetPlayer();
    }
    public void resetPlayer(){
        resetPlayer(startingPosition);
    }
    public void resetPlayer(Vector2 locationToSpawnAt){
        canJump=true;
        framesOnGround=0;
        state=0;
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

    public void collectItem(String item){
        switch (item){
            case "Mushroom"->{
                if(health>1){
                    //add score
                    return;
                }
                Vector2 startingPosition=body.getPosition().add(0,0.5f);
                body.getWorld().destroyBody(body);
                state =1;
                health=2;
                addToWorld(startingPosition);
            }
            case "OneUP"->{
                System.out.println("You got a 1UP");
                lives++;
            }
        }
    }
    private void addToWorld(Vector2 startingPosition) {
        WIDTH = (playerSpriteTiles[state][0].getProperties().get("WIDTH",int.class)-2)  * GameCore.unitsPerPixel;// the -2 is so the player appears to be touching objects when colliding
        HEIGHT= (playerSpriteTiles[state][0].getProperties().get("HEIGHT",int.class)-2) * GameCore.unitsPerPixel;
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
