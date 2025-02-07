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
import com.github.lazireth.advancedPlatformer.objects.Pipe;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.MovementStep;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.TimedMovement;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;


import java.util.ArrayList;

import static com.github.lazireth.advancedPlatformer.InputHandler.keys;
import static com.github.lazireth.advancedPlatformer.Player.PlayerPersistentData.*;
import static com.github.lazireth.advancedPlatformer.objects.timedMovement.CollisionFlag.NONE;

public class Player{
    public float WIDTH;
    public float HEIGHT;

    static final float DENSITY=1;
    static final float FRICTION=0.8f;

    public Vector2 PLAYER_JUMP_IMPULSE;
    public static final int JUMP_TICKS=6;
    public Vector2 PLAYER_MOVE_LEFT_IMPULSE;
    public Vector2 PLAYER_MOVE_RIGHT_IMPULSE;

    public static final float ACCELERATION=0.1f;
    public static final float MAX_WALK_SPEED=5;
    public static final float MAX_RUN_SPEED=9;
    public static final float DECELERATION_FACTOR=0.98f;

    public final ArrayList<TiledMapTile> tiles;
    public final ArrayList<TextureRegion> sprites;

    Vector2 startingPosition;//relative to the bottom left of the player
    public Body body;
    public static GameCore game;
    Area area;

    private Vector2 lastVelocity=new Vector2(0,0);
    public boolean preserveVelocityWhenLanding=false;

    int ticksJumping;
    boolean isJumping;
    float jumpTimeout=0;

    boolean isRunning;

    boolean hasDied =false;
    boolean updateBodySize=false;
    public int numFootContacts=0;
    public boolean disable;
    public boolean render=true;

    long canTakeDamageAfter=0;

    boolean canEnterPipe=false;
    Pipe pipePlayerCanEnter=null;
    boolean pipeIsHorizontal=true;
    TimedMovement timedMovement;
    public Player(TiledMapTileMapObject playerObject, ArrayList<TiledMapTile> playerTilesIn, Area area){
        this.area=area;
        tiles=playerTilesIn;

        sprites =new ArrayList<>();

        for(TiledMapTile tile: tiles){
            sprites.add(tile.getTextureRegion());
        }
        startingPosition=new Vector2(pixelsToUnits(playerObject.getX()), pixelsToUnits(playerObject.getY()));
        addToWorld(startingPosition.x,startingPosition.y);
    }
    public Player(TiledMapTileMapObject playerObject, ArrayList<TiledMapTile> playerTilesIn, Area area,Vector2 startingPositionIn){
        this.area=area;
        tiles=playerTilesIn;

        sprites =new ArrayList<>();

        for(TiledMapTile tile: tiles){
            sprites.add(tile.getTextureRegion());
        }
        startingPosition=startingPositionIn;
        addToWorld(startingPosition.x,startingPosition.y);
        ArrayList<MovementStep> movementSteps=new ArrayList<>();
        if(health==0){
            movementSteps.addLast(new MovementStep(0,2,0, NONE));
            movementSteps.addLast(new MovementStep(0,0,0.6f, NONE));
        }else{
            movementSteps.addLast(new MovementStep(0,2,0, NONE));
            movementSteps.addLast(new MovementStep(0,0,0.9f, NONE));
        }

        timedMovement=new TimedMovement(movementSteps,body);
        timedMovement.start();
        disable=true;
    }

    public void input(float delta){
        System.out.println("numFootContacts "+numFootContacts);
        if(body.getType().equals(BodyType.KinematicBody)||disable){
            System.out.println("disabled");
            return;
        }
        jumpTimeout-=delta;
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
        if(!keys[Input.Keys.D]&&!keys[Input.Keys.A]){
            body.setLinearVelocity(body.getLinearVelocity().x*DECELERATION_FACTOR,body.getLinearVelocity().y);
        }
        if(Math.abs(getXVelocity())<MAX_RUN_SPEED){
            isRunning=false;
        }
        if(keys[Input.Keys.S]&&body.getLinearVelocity().y==0&&Math.abs(body.getLinearVelocity().x)<0.2&&canEnterPipe&&!pipeIsHorizontal){
            body.setLinearVelocity(0,0);
            pipePlayerCanEnter.playerEnterPipe(this);
        }
        if(keys[Input.Keys.D]&&body.getLinearVelocity().y==0&&body.getLinearVelocity().x>0&&body.getLinearVelocity().x<1&&canEnterPipe&&pipeIsHorizontal){
            System.out.println("horizontal pipe");
            body.setLinearVelocity(0,0);
            pipePlayerCanEnter.playerEnterPipe(this);
        }
        if(keys[Input.Keys.L]){
            keys[Input.Keys.L]=false;
            ScreenshotFactory.saveScreenshot();
        }
    }
    public void render(TextureMapObjectRenderer renderer){
        if(render){
            switch (health){
                case 0-> renderer.renderObject(sprites.get(health),getXPosition(),getYPosition(),1,1);//width and height are in gameUnits
                case 1-> renderer.renderObject(sprites.get(health),getXPosition(),getYPosition(),1,2);
            }
        }
    }
    public void deathCheck(){
        if(hasDied||body.getPosition().y<-1){
            death();
            numFootContacts=0;
        }
    }
    // todo
    // implement GameOver
    private void death(){
        System.out.println("death");
        lives--;
        if(lives <0){
            System.out.println("You ran out of lives");
            game.loadGameOverScreen();
            return;
        }
        System.out.println("end");
        try{
            body.getWorld().destroyBody(body);
        }catch (Exception e){
            throw new RuntimeException();
        }
        health=0;
        numFootContacts=0;
        area.level.reset();
        //resetPlayer();
        game.loadLevelStartScreen();
    }
    public void takeDamage(int damageTaken){
        if(System.nanoTime()>=canTakeDamageAfter){
            health -=damageTaken;
            if(health <0){
                hasDied =true;
            }else{
                updateBodySize=true;
            }
            canTakeDamageAfter=System.nanoTime()+3L*1000000000;// number of seconds to nanoseconds
        }
    }

    public void resetPlayer(){
        health =0;
        body.getWorld().destroyBody(body);
        addToWorld(startingPosition.x,startingPosition.y);
        area.cameraPos=new Vector3(startingPosition.x,area.camera.position.y,area.camera.position.z);

        lastVelocity=new Vector2(0,0);
        preserveVelocityWhenLanding=false;

        ticksJumping=0;
        isJumping=false;
        jumpTimeout=0;

        isRunning=false;

        hasDied =false;
        updateBodySize=false;
        numFootContacts=0;

        canTakeDamageAfter=0;
    }

    public void update(float delta){
        if(timedMovement!=null){
            timedMovement.update(delta);
            if(timedMovement.finished){
                timedMovement=null;
                disable=false;
                Vector2 pos=body.getPosition();
                System.out.println("pos "+pos);
                body.getWorld().destroyBody(body);
                addToWorld(pos.x,pos.y);
            }
        }
        if(body.getType().equals(BodyType.KinematicBody)||disable){
            return;
        }
        if(updateBodySize){
            Vector2 position=body.getPosition();
            Vector2 velocity=body.getLinearVelocity();
            body.getWorld().destroyBody(body);
            addToWorld(position.x,position.y);
            body.setLinearVelocity(velocity);
            ticksJumping=0;
            isJumping=false;
            jumpTimeout=0;

            isRunning=false;

            hasDied =false;
            updateBodySize=false;
            numFootContacts=0;
        }
        if(preserveVelocityWhenLanding){
            if(numFootContacts>0){
                if(lastVelocity.y<-1){// need Math.abs(...)<0.1 instead of ...==0 in case ... is an incredibly small value
                    body.setLinearVelocity(lastVelocity.x,body.getLinearVelocity().y);
                }
            }
            preserveVelocityWhenLanding=false;
        }
        lastVelocity=body.getLinearVelocity().cpy();
    }
    public void pipeContactBegin(Pipe pipe){
        System.out.println("pipeContactBegin");
        pipePlayerCanEnter=pipe;
        canEnterPipe=true;
        pipeIsHorizontal=pipe.isHorizontal;
    }
    public void pipeContactEnd(){
        pipePlayerCanEnter=null;
        canEnterPipe=false;
    }
    public void collectItem(String item){
        switch (item){
            case "Mushroom"->{
                if(health >0){
                    //add score
                    return;
                }
                Vector2 position=body.getPosition();
                body.getWorld().destroyBody(body);
                health=1;
                addToWorld(position.x,position.y);
            }
            case "OneUP"->{
                System.out.println("You got a 1UP");
                lives++;
            }
        }
    }
    public void bounceOffEnemy() {
        body.setLinearVelocity(body.getLinearVelocity().x,0);
        body.applyLinearImpulse(PLAYER_JUMP_IMPULSE,body.getPosition(),true);
    }

    private void addToWorld(float x, float y) {
        WIDTH = (tiles.get(health).getProperties().get("WIDTH",int.class)-2)  * GameCore.metersPerPixel;// the -2 is so the player appears to be touching objects when colliding
        HEIGHT= (tiles.get(health).getProperties().get("HEIGHT",int.class)-2) * GameCore.metersPerPixel;
        float yOffset=0;
        if(health==1){
            yOffset+=0.25f;//add a quarter of a meter
        }

        PolygonShape shape=new PolygonShape();
        shape.setAsBox(WIDTH/2,HEIGHT/2);



        BodyDef bodyDef=new BodyDef();
        bodyDef.type=BodyType.DynamicBody;
        bodyDef.fixedRotation=true;
        bodyDef.position.set(x,y+yOffset);

        body=area.world.createBody(bodyDef);


        FixtureDef fixtureDef =new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.density=DENSITY;
        fixtureDef.friction=FRICTION;
        FilterCategory.PLAYER.makeFilter(fixtureDef.filter);

        body.createFixture(fixtureDef).setUserData(this);
        body.setGravityScale(2.0f);

        fixtureDef.density=0;
        fixtureDef.friction=0;
        shape.setAsBox(WIDTH/2-0.05f,0.2f,new Vector2(0,-HEIGHT/2),0);
        fixtureDef.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,  FilterCategory.WALL);
        body.createFixture(fixtureDef).setUserData(new ObjectSensor("playerFootSensor",this));
        shape.dispose();

        PLAYER_JUMP_IMPULSE=new Vector2(0,15*body.getMass()/JUMP_TICKS);
        PLAYER_MOVE_LEFT_IMPULSE=new Vector2(-0.6f*body.getMass(),0);
        PLAYER_MOVE_RIGHT_IMPULSE=new Vector2(0.6f*body.getMass(),0);
    }
    public static class PlayerPersistentData {
        public static int lives=5;
        public static int health=0;
        // 0 is normal
        // 1 is big (caused by mushroom)
    }
    public float bottomOfPlayer(){return getYPosition()-HEIGHT/2;}
    public float getYVelocity(){return body.getLinearVelocity().y;}
    public float getXVelocity(){return body.getLinearVelocity().x;}
    public Vector2 getVelocity(){return body.getLinearVelocity().cpy();}
    public float getYPosition(){return body.getPosition().y;}
    public float getXPosition(){return body.getPosition().x;}
    public Vector2 getPosition(){return body.getPosition().cpy();}
    public float pixelsToUnits(float pixels){return pixels*GameCore.metersPerPixel;}
}
