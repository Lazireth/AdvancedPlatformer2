package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.advancedPlatformer.Area;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.advancedPlatformer.objects.LevelEndFlag.FlagPoleSequenceStates.*;

public class LevelEndFlag extends InteractableObject{
    float flagWidth;
    float flagHeight;
    float flagPoleWidth;
    float flagPoleHeight;
    TiledMapTileMapObject flag;
    TiledMapTileMapObject flagPole;
    public Body flagBody;
    public Body flagPoleBody;

    public ArrayList<TextureRegion> sprites;

    public Player player;
    public FlagPoleSequenceStates flagPoleSequenceState=notStarted;

    boolean playerNeedsToGoDown=true;
    boolean flagNeedsToGoDown=true;

    float doLevelTransitionAfter;
    Area area;
    public LevelEndFlag(TiledMapTileMapObject flag, TiledMapTileMapObject flagPole, Area area){

        body=null;
        this.flag = flag;
        this.flagPole=flagPole;
        this.area=area;

        sprites=getSpritesFor("LevelEndFlag");

        flagWidth = pixelsToUnits(sprites.get(0).getRegionWidth());
        flagHeight= pixelsToUnits(sprites.get(0).getRegionHeight());
        flagPoleWidth = pixelsToUnits(sprites.get(1).getRegionWidth());
        flagPoleHeight= pixelsToUnits(sprites.get(1).getRegionHeight());

        // create sensor
        buildBody();
    }
    public void levelReset(){
        sprites=getSpritesFor("LevelEndFlag");

        flagWidth = pixelsToUnits(sprites.get(0).getRegionWidth());
        flagHeight= pixelsToUnits(sprites.get(0).getRegionHeight());
        flagPoleWidth = pixelsToUnits(sprites.get(1).getRegionWidth());
        flagPoleHeight= pixelsToUnits(sprites.get(1).getRegionHeight());

        // create sensor
        buildBody();
    }
    @Override
    public void update(float delta) {
        float descentSpeed=-5;
        float playerBottomStopPosition=3.1f;
        float flagStopPosition=4.6f;
        float alignFlagLeftPos=197.0f;
        float alignFlagRightPos=198.0f;
        float walkToEndSpeed=3;
        System.out.println("flagPoleSequenceState "+flagPoleSequenceState);
        switch (flagPoleSequenceState){
            case starting -> {
                player.body.setType(BodyDef.BodyType.KinematicBody);
                player.body.setLinearVelocity(0,0);
                player.disable=true;
                flagPoleSequenceState=alignFlagPoleLeft;
            }
            case alignFlagPoleLeft -> {
                if(alignFlagLeftPos-player.body.getPosition().x==0){
                    player.body.setLinearVelocity(0,0);
                    flagPoleSequenceState=playerAndFlagGoDown;
                }else{
                    float distanceToMove=alignFlagLeftPos-player.body.getPosition().x;
                    float neededVel=distanceToMove*60;
                    player.body.setLinearVelocity(neededVel,0);
                }
            }
            case playerAndFlagGoDown -> {
                if(playerNeedsToGoDown&&player.bottomOfPlayer()<=playerBottomStopPosition){
                    //if player is going down and should not anymore
                    playerNeedsToGoDown=false;
                    player.body.setLinearVelocity(0,0);
                }
                if(flagNeedsToGoDown&&flagBody.getPosition().y<=flagStopPosition){
                    //if flag is going down and should not anymore
                    flagNeedsToGoDown=false;
                    flagBody.setLinearVelocity(0,0);
                }
                if(!playerNeedsToGoDown&&!flagNeedsToGoDown){
                    //if neither need to go down anymore
                    flagPoleSequenceState=alignFlagPoleRight;
                    break;
                }
                if(playerNeedsToGoDown){
                    if(player.bottomOfPlayer()+descentSpeed/60f>=playerBottomStopPosition){
                        player.body.setLinearVelocity(0,descentSpeed);
                    }else{
                        player.body.setLinearVelocity(0,(playerBottomStopPosition-player.bottomOfPlayer())*60);
                    }
                }
                if(flagNeedsToGoDown){
                    if(flagBody.getPosition().y+descentSpeed/60f>=flagStopPosition){
                        flagBody.setLinearVelocity(0,descentSpeed);
                    }else{
                        flagBody.setLinearVelocity(0,(flagStopPosition-flagBody.getPosition().y)*60);
                    }
                }
            }
            case alignFlagPoleRight -> {
                if(alignFlagRightPos-player.body.getPosition().x==0){
                    player.body.setLinearVelocity(0,0);
                    flagPoleSequenceState=dropOffFlagpole;
                }else{
                    float distanceToMove=alignFlagRightPos-player.body.getPosition().x;
                    float neededVel=distanceToMove*60;
                    player.body.setLinearVelocity(neededVel,0);
                }
            }
            case dropOffFlagpole -> {
                player.body.setType(BodyDef.BodyType.DynamicBody);
                player.body.applyLinearImpulse(player.PLAYER_MOVE_RIGHT_IMPULSE.cpy().add(player.PLAYER_JUMP_IMPULSE.cpy()).scl(2),player.body.getPosition(),true);
                flagPoleSequenceState=waitTillPlayerLanded;
                player.numFootContacts=0;
            }
            case waitTillPlayerLanded -> {
                if(player.body.getLinearVelocity().y==0){
                    player.body.setType(BodyDef.BodyType.KinematicBody);
                    player.body.setLinearVelocity(walkToEndSpeed,0);
                    flagPoleSequenceState=walkToLevelEnd;
                }
            }
            case walkToLevelEnd -> {
                if(player.body.getPosition().x>203.62566f){
                    flagPoleSequenceState=finished;
                    player.body.setLinearVelocity(0,0);
                    player.render=false;
                    doLevelTransitionAfter=System.nanoTime()+3L*1000000000;// number of seconds to nanoseconds
                }
            }
            case finished -> {
                if(System.nanoTime()>doLevelTransitionAfter){
                    GameScreen.doLevelTransition=true;
                }
            }
        }
    }
    public void contactWithPlayer(Player playerIn){
        if(flagPoleSequenceState==notStarted){
            player=playerIn;
            flagPoleSequenceState=starting;
        }
    }
    public void startInteractionWithPlayer(Player player){

    }

    public enum FlagPoleSequenceStates{
        notStarted,
        starting,
        alignFlagPoleLeft,
        playerAndFlagGoDown,
        alignFlagPoleRight,
        dropOffFlagpole,
        waitTillPlayerLanded,
        walkToLevelEnd,
        finished
    }
    private void buildBody() {
        //build flag
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDefRect=new FixtureDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(pixelsToUnits(flag.getX())+flagWidth/2.0f,pixelsToUnits(flag.getY())+flagHeight/2.0f);


        shape.setAsBox(flagWidth/2.0f, flagHeight/2.0f);
        fixtureDefRect.shape=shape;
        fixtureDefRect.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDefRect.filter,(short)0);

        flagBody = area.world.createBody(bodyDef);
        flagBody.createFixture(fixtureDefRect);

        //build flagPole
        bodyDef.position.set(pixelsToUnits(flagPole.getX())+flagPoleWidth/2.0f,pixelsToUnits(flagPole.getY())+flagPoleHeight/2.0f+(2));


        shape.setAsBox(flagPoleWidth/8.0f, flagPoleHeight/2.0f+2);
        fixtureDefRect.shape=shape;
        fixtureDefRect.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDefRect.filter,FilterCategory.PLAYER);

        flagPoleBody = area.world.createBody(bodyDef);
        flagPoleBody.createFixture(fixtureDefRect).setUserData(new ObjectSensor("levelEndFlagFlagPole",this));

        shape.dispose();
    }
    public void render(TextureMapObjectRenderer renderer){
        renderer.renderObject(sprites.get(0), flagBody.getPosition(),flagWidth,flagHeight);
        renderer.renderObject(sprites.get(1), flagPoleBody.getPosition().x,flagPoleBody.getPosition().y-2,flagPoleWidth,flagPoleHeight);
    }
}
