package com.github.lazireth.AdvancedPlatformer2.overworld;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.github.lazireth.AdvancedPlatformer2.GameCore;
import com.github.lazireth.AdvancedPlatformer2.MapBodyBuilder;
import com.github.lazireth.AdvancedPlatformer2.objects.InteractableObject;
import com.github.lazireth.AdvancedPlatformer2.render.TextureMapObjectRenderer;

import java.util.ArrayList;

import static com.github.lazireth.AdvancedPlatformer2.GameCore.GlobalVariables.*;
import static com.github.lazireth.AdvancedPlatformer2.InputHandler.keys;

public class Overworld extends ScreenAdapter {
    public static final Box2DDebugRenderer debugRenderer=new Box2DDebugRenderer();

    private final static ArrayList<InteractableObject> interactableObjects=new ArrayList<>();
    public final static ArrayList<InteractableObject> interactableObjectsAdd=new ArrayList<>();
    public final static ArrayList<InteractableObject> interactableObjectsRemove=new ArrayList<>();

    public static TextureMapObjectRenderer renderer;
    public static OrthographicCamera camera;
    public static int[] renderLayer;

    public static int worldNumber;

    private static float accumulator=0;
    public static TiledMap tiledMap;
    public static World world;

    public Overworld(int worldNumberIn){System.out.println("Overworld");
        worldNumber=worldNumberIn;
        tiledMap=new TmxMapLoader().load("Map/Worlds/Overworlds/World "+worldNumber+".tmx");
        TiledMapTileLayer tiledMapTileLayer=(TiledMapTileLayer)tiledMap.getLayers().get("Background");

        camera=new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);


        renderer=new TextureMapObjectRenderer(tiledMap,metersPerPixel);
        camera.translate(0,-2,0);
        camera.update();
        renderer.setView(camera);
        makeRenderLayer();

        world=new World(new Vector2(0,0),true);

        MapBodyBuilder.buildShapes(tiledMap, world);



        loadMapObjects();
    }

    private static void makeRenderLayer(){
        if(tiledMap.getLayers().getIndex("Background")==-1){
            renderLayer=new int[]{tiledMap.getLayers().getIndex("Foreground")};// make array of Tile Layers to render
        }else{
            renderLayer=new int[]{
                tiledMap.getLayers().getIndex("Background"),
                tiledMap.getLayers().getIndex("Foreground")};// make array of Tile Layers to render
        }
    }
    @Override
    public void render(float deltaTime) {
        OverworldPlayer.input(deltaTime);

        doPhysicsStep(deltaTime);


        //update interactable objects and pipe
        for(InteractableObject object:interactableObjects){ object.update(deltaTime); }

        //add and remove from interactableObjects ArrayList
        while(!interactableObjectsAdd.isEmpty()){interactableObjects.addFirst(interactableObjectsAdd.removeFirst());}
        while(!interactableObjectsRemove.isEmpty()){interactableObjects.remove(interactableObjectsRemove.removeFirst());}

        renderer.renderFullFrame(renderLayer,interactableObjects);

    }
    private static void doPhysicsStep(float deltaTime){
        float frameTime=Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if(accumulator > -TIME_STEP){
            System.out.println("world step");
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
        if(accumulator > TIME_STEP/2){
            System.out.println("world step");
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }
    private void loadMapObjects(){
        MapObjects mapObjects = tiledMap.getLayers().get("Level Objects").getObjects();

        for(MapObject mapObject:mapObjects){
            switch(((TiledMapTileMapObject)mapObject).getTile().getProperties().get("relatedObject","no relatedObject",String.class)){
                case "Player"-> new OverworldPlayer((TiledMapTileMapObject) mapObject);

                case "no relatedObject"->throw new RuntimeException("A MapObject has no relatedObject");
                case null, default -> throw new RuntimeException("A MapObject has an invalid relatedObject");
            }
        }
    }
    public static float pixelsToMeters(float pixels){return pixels*metersPerPixel;}
    public static float unitsToMeters(float meters){return Math.round(meters*pixelsPerMeter);}
    public static void debugRender(){debugRenderer.render(world,camera.combined);}
    public static void updateCamera(){camera.update();}
    public void resize(int width, int height) {GameCore.viewport.update(width, height, true);}
    public void dispose() {
        interactableObjects.clear();interactableObjectsAdd.clear();interactableObjectsRemove.clear();
        accumulator=0;world.dispose();tiledMap.dispose();
    }

    public static class OverworldPlayer implements Disposable {
        public static TextureRegion sprite;
        public static Body body;

        public static Vector2 targetPlayerPosition;
        public static boolean moving;
        private OverworldPlayer(TiledMapTileMapObject playerObject){
            PolygonShape shape=new PolygonShape();
            shape.setAsBox(0.45f,0.45f);

            BodyDef bodyDef=new BodyDef();
            bodyDef.type=BodyDef.BodyType.DynamicBody;
            bodyDef.fixedRotation=true;
            bodyDef.position.set(pixelsToMeters(playerObject.getX())+0.5f, pixelsToMeters(playerObject.getY())+0.5f);

            body=world.createBody(bodyDef);

            FixtureDef fixtureDef=new FixtureDef();
            fixtureDef.shape=shape;
            //FilterCategory.PLAYER.makeFilter(fixtureDef.filter);

            body.createFixture(fixtureDef);
            targetPlayerPosition=body.getPosition();

            sprite=playerObject.getTextureRegion();
            moving=false;
        }
        public static void input(float deltaTime){
            System.out.println("Player input");
            float speedCap=5;
            float acceleration=0.3f;
            float decelerationFactor=0.7f;
            //player input
            if(keys[Keys.UP]&&!keys[Keys.DOWN]&&body.getLinearVelocity().y<speedCap){//up
                body.setLinearVelocity(body.getLinearVelocity().cpy().add(0,acceleration));
            }
            if(keys[Keys.RIGHT]&&!keys[Keys.LEFT]&&body.getLinearVelocity().x<speedCap){//right
                body.setLinearVelocity(body.getLinearVelocity().cpy().add(acceleration,0));
            }
            if(keys[Keys.DOWN]&&!keys[Keys.UP]&&body.getLinearVelocity().y>-speedCap){//down
                body.setLinearVelocity(body.getLinearVelocity().cpy().add(0,-acceleration));
            }
            if(keys[Keys.LEFT]&&!keys[Keys.RIGHT]&&body.getLinearVelocity().x>-speedCap){//left
                body.setLinearVelocity(body.getLinearVelocity().cpy().add(-acceleration,0));
            }

            //drag if both keys for an axis not press
            if(!keys[Keys.UP]&&!keys[Keys.DOWN]){
                body.setLinearVelocity(body.getLinearVelocity().x,body.getLinearVelocity().y*decelerationFactor);
            }
            if(!keys[Keys.LEFT]&&!keys[Keys.RIGHT]){
                body.setLinearVelocity(body.getLinearVelocity().x*decelerationFactor,body.getLinearVelocity().y);
            }
//            //stop if slow enough
//            if(Math.abs(body.getLinearVelocity().y)<0.2){
//                body.setLinearVelocity(0,body.getLinearVelocity().y);
//            }
//            if(Math.abs(body.getLinearVelocity().x)<0.2){
//                body.setLinearVelocity(body.getLinearVelocity().x,0);
//            }

        }
        public static void render(TextureMapObjectRenderer renderer){
            renderer.renderObject(sprite,body.getPosition().x,body.getPosition().y,1,1);
        }
        public void dispose() {
            targetPlayerPosition=new Vector2();
            body=null;
        }
    }
}
