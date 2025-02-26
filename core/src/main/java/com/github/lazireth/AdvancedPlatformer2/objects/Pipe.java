package com.github.lazireth.AdvancedPlatformer2.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.github.lazireth.AdvancedPlatformer2.LevelMap;
import com.github.lazireth.AdvancedPlatformer2.Player;
import com.github.lazireth.AdvancedPlatformer2.objects.timedMovement.MovementStep;
import com.github.lazireth.AdvancedPlatformer2.objects.timedMovement.TimedMovement;
import com.github.lazireth.AdvancedPlatformer2.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.AdvancedPlatformer2.objects.timedMovement.CollisionFlag.NONE;

public class Pipe extends InteractableObject{
    public TiledMapTileMapObject pipe;
    public TextureRegion textureRegion;
    public boolean isHorizontal=false;

    float width;
    float height;
    LevelMap levelMap;
    int myID=-1;
    int destinationArea=-1;
    int destinationPipe=-1;
    boolean destinationAreaReset=false;
    boolean startedTransition=false;

    Player player;

    TimedMovement timedMovement;

    Vector2 renderPosition;
    public Pipe(TiledMapTileMapObject pipe, LevelMap levelMap){
        this.pipe=pipe;
        this.levelMap=levelMap;
        textureRegion=pipe.getTextureRegion();
        if(pipe.getProperties().containsKey("pipeID")){
            myID=pipe.getProperties().get("pipeID",Integer.class);
            LevelMap.pipeMap.put(myID,this);

            if(pipe.getProperties().containsKey("destinationArea")){
                destinationArea=pipe.getProperties().get("destinationArea",Integer.class);

                if(pipe.getProperties().containsKey("destinationPipe")){
                    destinationPipe=pipe.getProperties().get("destinationPipe",Integer.class);

                    if(pipe.getProperties().containsKey("destinationAreaReset")){
                        destinationAreaReset=pipe.getProperties().get("destinationAreaReset",Boolean.class);
                    }
                }
            }
            if(pipe.getProperties().containsKey("isHorizontal")){
                isHorizontal=pipe.getProperties().get("isHorizontal",Boolean.class);
            }
        }

        width = pixelsToMeters(pipe.getTextureRegion().getRegionWidth());
        height= pixelsToMeters(pipe.getTextureRegion().getRegionHeight());
        buildBody();
    }

    @Override
    public void render(TextureMapObjectRenderer renderer) {
        renderer.renderObject(textureRegion,renderPosition,width,height);
    }
    // todo
    @Override
    public void update(float delta) {
        if(timedMovement!=null){
            timedMovement.update(delta);
            if(timedMovement.finished&&!startedTransition){
                startedTransition=true;

                //levelMap.level.loadArea(destinationArea,destinationPipe,destinationAreaReset);
            }
        }

    }
    public void playerEnterPipe(Player player){
        this.player=player;
        player.disable=true;
        ArrayList<MovementStep> movementSteps=new ArrayList<>();
        if(!isHorizontal){
            movementSteps.addLast(new MovementStep(0,-2,0, NONE));
            movementSteps.addLast(new MovementStep(0,0,0.5f, NONE));
        }else{
            movementSteps.addLast(new MovementStep(2,0,0, NONE));
            movementSteps.addLast(new MovementStep(0,0,0.5f, NONE));
        }


        timedMovement=new TimedMovement(movementSteps,player.body);
        timedMovement.start();
    }
    private void buildBody() {
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDefRect=new FixtureDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        renderPosition=new Vector2(pixelsToMeters(pipe.getX())+width/2.0f, pixelsToMeters(pipe.getY())+height/2.0f);
        bodyDef.position.set(renderPosition);

        fixtureDefRect.shape=shape;
        fixtureDefRect.isSensor=true;
        FilterCategory.SENSOR.makeSensorFilter(fixtureDefRect.filter,FilterCategory.PLAYER);

        if(!isHorizontal){
            shape.setAsBox((width/2)*0.3f,0.1f,new Vector2(0,height/2),0);
        }else{
            shape.setAsBox(0.1f,(height/2)*0.4f,new Vector2(-width/2,0),0);
        }


        body = LevelMap.world.createBody(bodyDef);
        body.createFixture(fixtureDefRect).setUserData(new ObjectSensor("pipeSensor",this));
        shape.dispose();
    }
    public void contactBegin(Player player){if(destinationArea!=-1||destinationPipe!=-1){player.pipeContactBegin(this);}}
    public void contactEnd(Player player){if(myID!=-1){player.pipeContactEnd();}}
    public Vector2 getPlayerExitPoint(){return new Vector2(pixelsToMeters(pipe.getX())+width/2.0f, pixelsToMeters(pipe.getY())+height/4.0f*3);}
    public void levelReset() {buildBody();}
    public void startInteractionWithPlayer(Player player) {}
}
