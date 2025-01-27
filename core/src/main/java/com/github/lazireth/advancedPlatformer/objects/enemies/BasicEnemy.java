package com.github.lazireth.advancedPlatformer.objects.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.FilterCategory;
import com.github.lazireth.advancedPlatformer.objects.InteractableObject;
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
    public BasicEnemy (TiledMapTileMapObject enemy) {
        myMapObject=enemy;

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
        body.getWorld().destroyBody(body);

        enemyType= myMapObject.getTile().getProperties().get("enemyType",String.class);
        state= myMapObject.getTile().getProperties().get("state",int.class);

        sprites=getSpritesFor("BasicEnemy");

        WIDTH = pixelsToUnits(sprites.getFirst().getRegionWidth());
        HEIGHT= pixelsToUnits(sprites.getFirst().getRegionHeight());
        addToWorld();
        if( myMapObject.getProperties().get("startGoingRight",boolean.class)){
            body.setLinearVelocity(moveSpeed,0);
        }else{
            body.setLinearVelocity(-moveSpeed,0);
        }
    }
    @Override
    public void death() {
        GameCore.gameScreen.level.interactableObjectsRemove.add(this);
        body.getWorld().destroyBody(body);
    }

    @Override
    public void takeDamage(int damageTaken) {
        health-=damageTaken;
        if(health<=0){
            timedMovement.start();
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
        if(timedMovement.finished){
            death();
            return;
        }
        if(timedMovement.started){
            return;
        }
        if(running){
            //start moving

            if(Math.abs(body.getLinearVelocity().y)<0.2){
                if(body.getLinearVelocity().x>0){
                    body.setLinearVelocity(moveSpeed,0);
                }else{
                    body.setLinearVelocity(-moveSpeed,0);
                }
            }
        }else{
            if(Math.abs(GameScreen.player.getXPosition()-body.getPosition().x)<18){
                running=true;
                if( myMapObject.getProperties().get("startGoingRight",boolean.class)){
                    body.setLinearVelocity(moveSpeed,0);
                }else{
                    body.setLinearVelocity(-moveSpeed,0);
                }
            }
        }
    }

    @Override
    public void startInteractionWithPlayer(Player player) {

        if(player.getYPosition()-Player.HEIGHT/2>=body.getPosition().y+HEIGHT/2-0.2){
            //If the top of the player is higher than some distance below the top of this object
            takeDamage(1);
        }else{
            damagePlayer(player);
        }
    }
    private void addToWorld() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation=true;
        bodyDef.position.set(pixelsToUnits(myMapObject.getX()),pixelsToUnits(myMapObject.getY()));

        body = GameScreen.world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH/2, HEIGHT/2);

        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.friction=0;
        fixtureDef.density=0.8f;
        FilterCategory.ENEMY.makeFilter(fixtureDef.filter);

        body.createFixture(fixtureDef).setUserData(this);

//        shape.setAsBox(WIDTH/2-0.05f,0.05f,new Vector2(0,-HEIGHT/2),0);
//        fixtureDef.isSensor=true;
//        FilterCategory.SENSOR.makeSensorFilter(fixtureDef.filter,  FilterCategory.WALL);
//        playerFootSensor=body.createFixture(fixtureDef);
//        playerFootSensor.setUserData(objectSensor);
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
                    //TR and point 1    BR and point 0
                    if(diffLessThan(corners[0],contact.getWorldManifold().getPoints()[0],0.1f)&&
                        diffLessThan(corners[1],contact.getWorldManifold().getPoints()[1],0.1f)){
                        //TR and point 0    BR and point 1
                        return true;
                    }else return diffLessThan(corners[0], contact.getWorldManifold().getPoints()[1], 0.1f) &&
                        diffLessThan(corners[1], contact.getWorldManifold().getPoints()[0], 0.1f);
                }
            }else{
                //moving to the left
                if(diffLessThan(corners[3].x,contact.getWorldManifold().getPoints()[0].x,0.1f)){
                    //left side is colliding
                    //TL and point 1    BL and point 0
                    if(diffLessThan(corners[3],contact.getWorldManifold().getPoints()[0],0.1f)&&
                        diffLessThan(corners[2],contact.getWorldManifold().getPoints()[1],0.1f)){
                        //TL and point 0    BL and point 1
                        return true;
                    }else return diffLessThan(corners[3], contact.getWorldManifold().getPoints()[1], 0.1f) &&
                        diffLessThan(corners[2], contact.getWorldManifold().getPoints()[0], 0.1f);
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
