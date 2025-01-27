package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.FilterCategory;
import com.github.lazireth.advancedPlatformer.objects.ObjectSensor;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;


import java.util.ArrayList;

import static com.github.lazireth.advancedPlatformer.InputHandler.keys;

public class Player{
    public static float WIDTH;
    public static float HEIGHT;

    static final float DENSITY=1;
    static final float FRICTION=0.8f;
    float MASS;

    private Vector2 PLAYER_JUMP_IMPULSE;
    private static final int JUMP_TICKS=6;
    private Vector2 PLAYER_MOVE_LEFT_IMPULSE;
    private Vector2 PLAYER_MOVE_RIGHT_IMPULSE;

    private static final float ACCELERATION=0.1f;
    private static final float MAX_WALK_SPEED=5;
    private static final float MAX_RUN_SPEED=9;
    private static final float DECELERATION_FACTOR=0.98f;

    int state=0;
    // 0 is normal
    // 1 is big (caused by mushroom)

    private int health=1;
    private int lives=5;

    private Body body;

    public ArrayList<TiledMapTile> tiles;
    public ArrayList<TextureRegion> sprites;


    private Vector2 lastVelocity=new Vector2(0,0);
    public boolean preserveVelocityWhenLanding=false;

    int ticksJumping;
    boolean isJumping;
    float jumpTimeout=0;
    Vector2 startingPosition;//relative to the bottom left of the player

    boolean isRunning;

    boolean hasDied =false;
    public static int numFootContacts=0;

    long canTakeDamageAfter=0;
    public Player(TiledMapTileMapObject playerObject, ArrayList<TiledMapTile> playerTilesIn){
        tiles =playerTilesIn;

        sprites =new ArrayList<>();

        for(TiledMapTile tile: tiles){
            sprites.add(tile.getTextureRegion());
        }
        startingPosition=new Vector2(pixelsToUnits(playerObject.getX()) , pixelsToUnits(playerObject.getY()));
        addToWorld( pixelsToUnits(playerObject.getX()) , pixelsToUnits(playerObject.getY()) );
    }


    public void input(float delta){
        jumpTimeout-=delta;
        if(isJumping){
            if(keys[Input.Keys.W]){
                if(ticksJumping<JUMP_TICKS){
                    System.out.println("ticksJumping "+ticksJumping);
                    body.applyLinearImpulse(PLAYER_JUMP_IMPULSE,body.getPosition(),true);
                    ticksJumping++;
                }else{
                    isJumping =false;

                }
            }else{
                isJumping =false;
            }
        }
        if(numFootContacts>0&&jumpTimeout<0){
            if(keys[Input.Keys.W]){
                body.applyLinearImpulse(PLAYER_JUMP_IMPULSE,body.getPosition(),true);
                isJumping=true;
                ticksJumping=1;
                jumpTimeout=0.25f;
            }
        }
        if(keys[Input.Keys.A]&&!keys[Input.Keys.D]){//check right
            if(isRunning){
                if(getXVelocity()>-MAX_RUN_SPEED){
                    body.applyLinearImpulse(PLAYER_MOVE_LEFT_IMPULSE,getPosition(),true);
                }
            }else{
                if(getXVelocity()>-MAX_WALK_SPEED){
                    body.applyLinearImpulse(PLAYER_MOVE_LEFT_IMPULSE,getPosition(),true);
                }else if(keys[Input.Keys.K]){
                    body.applyLinearImpulse(PLAYER_MOVE_LEFT_IMPULSE,getPosition(),true);
                    isRunning=true;
                }
            }
        }
        if(keys[Input.Keys.D]&&!keys[Input.Keys.A]){//check right
            if(isRunning){
                if(getXVelocity()<MAX_RUN_SPEED){
                    body.applyLinearImpulse(PLAYER_MOVE_RIGHT_IMPULSE,getPosition(),true);
                }
            }else{
                if(getXVelocity()<MAX_WALK_SPEED){
                    body.applyLinearImpulse(PLAYER_MOVE_RIGHT_IMPULSE,getPosition(),true);
                }else if(keys[Input.Keys.K]){
                    body.applyLinearImpulse(PLAYER_MOVE_RIGHT_IMPULSE,getPosition(),true);
                    isRunning=true;
                }
            }
        }

        if(Math.abs(getXVelocity())<MAX_RUN_SPEED){
            isRunning=false;
        }
        if(keys[Input.Keys.L]){
            keys[Input.Keys.L]=false;
            ScreenshotFactory.saveScreenshot();
        }
    }
    public void render(TextureMapObjectRenderer renderer){
        switch (state){
            case 0-> renderer.renderObject(sprites.get(state),getXPosition(),getYPosition(),1,1);//width and height are in gameUnits
            case 1-> renderer.renderObject(sprites.get(state),getXPosition(),getYPosition(),1,2);
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
        System.out.println("death");
        lives--;
        if(lives<0){
            System.out.println("You ran out of lives");
            //loadScreenGameOver();
            //return;
        }
        GameCore.gameScreen.level.levelReset();
        resetPlayer();

    }
    public void takeDamage(int damageTaken){
        if(System.nanoTime()>=canTakeDamageAfter){
            health-=damageTaken;
            if(health<=0){
                hasDied =true;
            }else if(health==1){
                state=0;
                canTakeDamageAfter=System.nanoTime()+3L*1000000000;// number of seconds to nanoseconds
            }
        }
    }
    public void resetPlayer(){
        resetPlayer(startingPosition);
    }
    public void resetPlayer(Vector2 locationToSpawnAt){
        state=0;
        health=1;
        ticksJumping=0;
        isJumping =false;
        hasDied =false;
        numFootContacts=0;
        body.getWorld().destroyBody(body);
        addToWorld(locationToSpawnAt.x,locationToSpawnAt.y);
        GameCore.cameraPos=new Vector3(locationToSpawnAt.x,GameCore.camera.position.y,GameCore.camera.position.z);
    }

    public void update(float delta){
        if(hasDied){
            death();
            return;
        }
        if(preserveVelocityWhenLanding){
            System.out.println("numFootContacts "+numFootContacts);
            if(numFootContacts>0){
                System.out.println("preserveVelocityWhenLanding");
                if(lastVelocity.y<-1){// need Math.abs(...)<0.1 instead of ...==0 in case ... is an incredibly small value
                    body.setLinearVelocity(lastVelocity.x,body.getLinearVelocity().y);
                }
            }
            preserveVelocityWhenLanding=false;
        }

        lastVelocity=body.getLinearVelocity().cpy();
    }

    public void collectItem(String item){
        switch (item){
            case "Mushroom"->{
                if(health>1){
                    //add score
                    return;
                }
                Vector2 position=body.getPosition();
                body.getWorld().destroyBody(body);
                state =1;
                health=2;
                addToWorld(position.x,position.y);
            }
            case "OneUP"->{
                System.out.println("You got a 1UP");
                lives++;
            }
        }
    }
    private void addToWorld(float x, float y) {
        WIDTH = (tiles.get(state).getProperties().get("WIDTH",int.class)-2)  * GameCore.metersPerPixel;// the -2 is so the player appears to be touching objects when colliding
        HEIGHT= (tiles.get(state).getProperties().get("HEIGHT",int.class)-2) * GameCore.metersPerPixel;
        float yOffset=0;
        if(state==1){
            yOffset+=0.25f;//add a quarter of a meter
        }

        PolygonShape shape=new PolygonShape();
        shape.setAsBox(WIDTH/2,HEIGHT/2);

        MASS=WIDTH*HEIGHT*DENSITY;

        PLAYER_JUMP_IMPULSE=new Vector2(0,20*MASS/JUMP_TICKS);
        PLAYER_MOVE_LEFT_IMPULSE=new Vector2(-0.6f*MASS,0);
        PLAYER_MOVE_RIGHT_IMPULSE=new Vector2(0.6f*MASS,0);

        BodyDef bodyDef=new BodyDef();
        bodyDef.type=BodyType.DynamicBody;
        bodyDef.fixedRotation=true;
        bodyDef.position.set(x,y+yOffset);

        body= GameScreen.world.createBody(bodyDef);


        FixtureDef fixtureDef =new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.density=DENSITY;
        fixtureDef.friction=FRICTION;
        FilterCategory.PLAYER.makeFilter(fixtureDef.filter);

        body.createFixture(fixtureDef).setUserData(this);
        body.setGravityScale(2.0f);

        shape.setAsBox(WIDTH/2-0.05f,0.2f,new Vector2(0,-HEIGHT/2),0);
        fixtureDef.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,  FilterCategory.WALL);
        body.createFixture(fixtureDef).setUserData(new ObjectSensor("playerFootSensor",this));
        shape.dispose();
    }
    //ObjectSensor objectSensor=new ObjectSensor("playerFootSensor",this);
    public float getYVelocity(){return body.getLinearVelocity().y;}
    public float getXVelocity(){return body.getLinearVelocity().x;}
    public Vector2 getVelocity(){return body.getLinearVelocity().cpy();}
    public float getYPosition(){return body.getPosition().y;}
    public float getXPosition(){return body.getPosition().x;}
    public Vector2 getPosition(){return body.getPosition().cpy();}
    public float pixelsToUnits(float pixels){return pixels*GameCore.metersPerPixel;}
}
