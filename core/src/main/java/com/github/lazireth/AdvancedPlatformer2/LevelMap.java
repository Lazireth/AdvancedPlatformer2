package com.github.lazireth.AdvancedPlatformer2;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.github.lazireth.AdvancedPlatformer2.objects.*;
import com.github.lazireth.AdvancedPlatformer2.objects.enemies.BasicEnemy;
import com.github.lazireth.AdvancedPlatformer2.render.TextureMapObjectRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.github.lazireth.AdvancedPlatformer2.GameCore.GlobalVariables.*;

public class LevelMap extends ScreenAdapter {
    public static final Box2DDebugRenderer debugRenderer=new Box2DDebugRenderer();

    private static final ArrayList<InteractableObject> interactableObjects=new ArrayList<>();
    public static final ArrayList<InteractableObject> interactableObjectsAdd=new ArrayList<>();
    public static final ArrayList<InteractableObject> interactableObjectsRemove=new ArrayList<>();

    public static final Map<Integer, Pipe> pipeMap =new HashMap<>();
    private static final ArrayList<InteractableObject> pipes=new ArrayList<>();

    public static TextureMapObjectRenderer renderer;
    public static OrthographicCamera camera;
    public static int[] renderLayer;

    public static int worldNumber;
    public static int levelNumber;

    private static float accumulator=0;
    public static TiledMap tiledMap;
    public static World world;
    public static Player player;

    public LevelMap(int worldNumberIn,int levelNumberIn){
        if(camera==null){camera=new OrthographicCamera(); camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);}
        worldNumber=worldNumberIn;
        levelNumber=levelNumberIn;
        tiledMap=new TmxMapLoader().load("Map/Worlds/Levels/"+worldNumber+"-"+levelNumber+".tmx");

        renderer=new TextureMapObjectRenderer(tiledMap,metersPerPixel);
        renderer.setView(camera);
        makeRenderLayer();

        world=new World(new Vector2(0,-9.8f),true);
        world.setContactListener(new CollisionListener());

        MapBodyBuilder.buildShapes(tiledMap, world);

        loadMapObjects();
    }
    public static void moveCameraToPlayer(){
        camera.position.set(player.getXPosition(),7.5f,camera.position.z);
        camera.update();
        renderer.setView(camera);
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
    public static OrthographicCamera getCamera(){return camera;}

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        player.input(delta);

        doPhysicsStep(delta);
        moveCameraToPlayer();

        //update interactable objects and pipe
        for(InteractableObject object:interactableObjects){ object.update(delta); }
        for(InteractableObject pipe: pipes){ pipe.update(delta); }

        //add and remove from interactableObjects ArrayList
        while(!interactableObjectsAdd.isEmpty()){interactableObjects.addFirst(interactableObjectsAdd.removeFirst());}
        while(!interactableObjectsRemove.isEmpty()){interactableObjects.remove(interactableObjectsRemove.removeFirst());}
        player.update(delta);

        renderer.renderFullFrame(renderLayer,interactableObjects,pipes);

        player.deathCheck();
    }
    private static void doPhysicsStep(float deltaTime){
        float frameTime=Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if(accumulator > -TIME_STEP){
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
        if(accumulator > TIME_STEP/2){
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }
    private void loadMapObjects(){
        ArrayList<TiledMapTileMapObject> pipes=new ArrayList<>();
        ArrayList<TiledMapTileMapObject> bricks=new ArrayList<>();
        ArrayList<TiledMapTileMapObject> enemies=new ArrayList<>();
        ArrayList<TiledMapTileMapObject> questionBlocks=new ArrayList<>();
        TiledMapTileMapObject playerObject=null;

        MapObjects mapObjects = tiledMap.getLayers().get("Level Objects").getObjects();

        for(MapObject mapObject:mapObjects){
            switch(((TiledMapTileMapObject)mapObject).getTile().getProperties().get("relatedObject","no relatedObject",String.class)){
                case "Player"-> playerObject=(TiledMapTileMapObject) mapObject;
                case "Pipe"->   pipes.add((TiledMapTileMapObject) mapObject);
                case "Brick"->  bricks.add((TiledMapTileMapObject) mapObject);
                case "Enemy"->  enemies.add((TiledMapTileMapObject) mapObject);
                case "QuestionBlock"->questionBlocks.add((TiledMapTileMapObject) mapObject);
                case "no relatedObject"->throw new RuntimeException("A MapObject has no relatedObject");
                case null, default -> throw new RuntimeException("A MapObject has an invalid relatedObject");
            }
        }
        if(playerObject==null){
            throw new NullPointerException("Level "+worldNumber+"-"+levelNumber+" has no Player object");
        }
        loadPipes(pipes);
        loadBricks(bricks);
        loadEnemies(enemies);
        loadQuestionBlocks(questionBlocks);

        player=new Player(playerObject,InteractableObject.getTilesFor("Player"));
    }
    private void loadBricks(ArrayList<TiledMapTileMapObject> bricks){for(TiledMapTileMapObject brick:bricks){interactableObjects.add(new Brick(brick,this));}}
    private void loadPipes(ArrayList<TiledMapTileMapObject> pipes){for(TiledMapTileMapObject pipe:pipes){LevelMap.pipes.add(new Pipe(pipe,this));}}
    private void loadQuestionBlocks(ArrayList<TiledMapTileMapObject> questionBlocks){for(TiledMapTileMapObject questionBlock:questionBlocks){interactableObjects.add(new QuestionBlock(questionBlock,this));}}
    private void loadEnemies(ArrayList<TiledMapTileMapObject> enemies){
        for (TiledMapTileMapObject enemy : enemies) {
            switch (enemy.getTile().getProperties().get("enemyType",String.class)){
                case "BasicEnemy"->{
                    interactableObjects.add(new BasicEnemy(enemy,this));
                }
            }
        }
    }
    public static void debugRender(){debugRenderer.render(world,camera.combined);}
    public static void updateCamera(){camera.update();}
    public void resize(int width, int height) {GameCore.viewport.update(width, height, true);}
    public void dispose() {world.dispose();tiledMap.dispose();}
}
