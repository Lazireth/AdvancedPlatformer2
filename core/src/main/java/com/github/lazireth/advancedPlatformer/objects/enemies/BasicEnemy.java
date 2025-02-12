package com.github.lazireth.advancedPlatformer.objects.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.Area;
import com.github.lazireth.advancedPlatformer.Direction;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.objects.FilterCategory;
import com.github.lazireth.advancedPlatformer.objects.ObjectSensor;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.MovementStep;
import com.github.lazireth.advancedPlatformer.objects.timedMovement.TimedMovement;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.advancedPlatformer.objects.timedMovement.CollisionFlag.NONE;

public class BasicEnemy extends Enemy {
    //BasicEnemies move until they hit a wall then turn around
    //and can only deal damage on contact
    TiledMapTileMapObject myMapObject;
    ArrayList<TextureRegion> sprites;
    String enemyType;
    int state;

    float WIDTH;
    float HEIGHT;

    int health=1;
    int damage=1;
    float moveSpeed=2;

    boolean running=false;
    TimedMovement timedMovement;
    boolean dying=false;
    public Direction directionToBounceTo=null;
    Area area;
    public BasicEnemy (TiledMapTileMapObject enemy, Area area) {
        myMapObject=enemy;
        this.area=area;

        enemyType= myMapObject.getTile().getProperties().get("enemyType",String.class);
        state= myMapObject.getTile().getProperties().get("state",int.class);

        sprites=getSpritesFor("BasicEnemy");

        WIDTH = pixelsToUnits(sprites.getFirst().getRegionWidth());
        HEIGHT= pixelsToUnits(sprites.getFirst().getRegionHeight());
        addToWorld();

        ArrayList<MovementStep> movementSteps=new ArrayList<>();
        movementSteps.addLast(new MovementStep(null,0, NONE));
        movementSteps.addLast(new MovementStep(null,0.25f, NONE));
        movementSteps.addLast(new MovementStep(null,0.5f, NONE));
        timedMovement=new TimedMovement(movementSteps,body);
    }
    public void levelReset(){
        enemyType= myMapObject.getTile().getProperties().get("enemyType",String.class);
        state= myMapObject.getTile().getProperties().get("state",int.class);

        sprites=getSpritesFor("BasicEnemy");

        WIDTH = pixelsToUnits(sprites.getFirst().getRegionWidth());
        HEIGHT= pixelsToUnits(sprites.getFirst().getRegionHeight());
        addToWorld();
        ArrayList<MovementStep> movementSteps=new ArrayList<>();
        movementSteps.addLast(new MovementStep(null,0, NONE));
        movementSteps.addLast(new MovementStep(null,0.25f, NONE));
        movementSteps.addLast(new MovementStep(null,0.5f, NONE));
        timedMovement=new TimedMovement(movementSteps,body);
        if( myMapObject.getProperties().get("startGoingRight",boolean.class)){
            body.setLinearVelocity(moveSpeed,0);
        }else{
            body.setLinearVelocity(-moveSpeed,0);
        }
    }
    @Override
    public void death() {
        System.out.println("death");
        area.interactableObjectsRemove.add(this);
        body.getWorld().destroyBody(body);
    }

    @Override
    public void takeDamage(int damageTaken) {
        health-=damageTaken;
        if(health<=0){
            body.setLinearVelocity(0,0);
            dying=true;
        }
    }

    @Override
    public void damagePlayer(Player player) {
        System.out.println("damagePlayer");
        player.takeDamage(damage);
    }

    @Override
    public void render(TextureMapObjectRenderer renderer) {
        renderer.renderObject(sprites.get(Math.max(0,timedMovement.currentMovementStep)), body.getPosition(),WIDTH,HEIGHT);
    }

    @Override
    public void update(float delta) {
        if(dying){
            death();
            //timedMovement.start();
            dying=false;
        }
        if(timedMovement.finished){
            death();
            return;
        }
        if(timedMovement.started){
            return;
        }
        if(running){
            //start moving
            if(body.getLinearVelocity().x==0){
                bounce();
                directionToBounceTo=null;
            }else
            if(body.getLinearVelocity().x>0&&body.getLinearVelocity().x<moveSpeed*0.9){
                body.setLinearVelocity(moveSpeed,0);
            }else
            if(body.getLinearVelocity().x<0&&body.getLinearVelocity().x>-moveSpeed*0.9){
                body.setLinearVelocity(-moveSpeed,0);
            }
        }else{
            if(Math.abs(area.player.getXPosition()-body.getPosition().x)<18){
                running=true;
                if(myMapObject.getProperties().containsKey("startGoingRight")){
                    if( myMapObject.getProperties().get("startGoingRight",boolean.class)){
                        body.setLinearVelocity(moveSpeed,0);
                    }else{
                        body.setLinearVelocity(-moveSpeed,0);
                    }
                }else{
                    body.setLinearVelocity(-moveSpeed,0);
                }
            }
        }
    }

    @Override
    public void startInteractionWithPlayer(Player player) {
        if(player.getYPosition()-player.HEIGHT/2>=body.getPosition().y+HEIGHT/2-0.25){
            //If the top of the player is higher than some distance below the top of this object
            takeDamage(1);
//            if(dying){
//                //player.bounceOffEnemy();
//            }
        }else{
            damagePlayer(player);
        }
    }
    private void addToWorld() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation=true;
        bodyDef.position.set(pixelsToUnits(myMapObject.getX()),pixelsToUnits(myMapObject.getY()));

        body = area.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH/2, HEIGHT/2);

        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.friction=0;
        fixtureDef.density=0.8f;
        FilterCategory.ENEMY.makeFilter(fixtureDef.filter);

        body.createFixture(fixtureDef).setUserData(this);

        shape.setAsBox(0.1f,HEIGHT/2-0.05f,new Vector2(-WIDTH/2,0),0);
        fixtureDef.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,(short)(FilterCategory.WALL.categoryBits | FilterCategory.ENEMY.categoryBits));
        body.createFixture(fixtureDef).setUserData(new ObjectSensor("enemyLeftSide",this));

        shape.setAsBox(0.1f,HEIGHT/2-0.05f,new Vector2(WIDTH/2,0),0);
        fixtureDef.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,(short)(FilterCategory.WALL.categoryBits | FilterCategory.ENEMY.categoryBits));
        body.createFixture(fixtureDef).setUserData(new ObjectSensor("enemyRightSide",this));
        shape.dispose();
    }
    private void bounce(){
        switch (directionToBounceTo){
            case LEFT -> body.setLinearVelocity(-moveSpeed,0);
            case RIGHT -> body.setLinearVelocity(moveSpeed,0);
            case null, default -> {}
        }
        directionToBounceTo=null;
    }
}
