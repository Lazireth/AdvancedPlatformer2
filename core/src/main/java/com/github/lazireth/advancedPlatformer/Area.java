package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.lazireth.advancedPlatformer.objects.*;
import com.github.lazireth.advancedPlatformer.objects.enemies.BasicEnemy;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.*;
import java.util.Map;

public class Area{
    public int[] firstRenderLayer;

    private final ArrayList<InteractableObject> interactableObjects=new ArrayList<>();
    public final ArrayList<InteractableObject> interactableObjectsAdd=new ArrayList<>();
    public final ArrayList<InteractableObject> interactableObjectsRemove=new ArrayList<>();

    public TiledMapTileMapObject playerObject;

    private static final float TIME_STEP=1/60f;
    private static final int VELOCITY_ITERATIONS=4;
    private static final int POSITION_ITERATIONS=6;
    private float accumulator=0;
    public World world;

    public TextureMapObjectRenderer renderer;
    public Box2DDebugRenderer debugRenderer=new Box2DDebugRenderer();
    public OrthographicCamera camera;
    public FitViewport viewport;

    public Player player;

    public final Map<Integer, Pipe> pipes=new HashMap<>();
    private final ArrayList<InteractableObject> pipesList=new ArrayList<>();

    public Level level;
    public int areaNumber;
    TiledMap tiledMap;
    String[] underGroundAreas={"1-1 1","1-2 1","1-2 2"};
    boolean underGround=false;
    public Area(String levelArea, Level level, int areaNumber){
        this.tiledMap=new TmxMapLoader().load("Map/"+levelArea+".tmx");
        this.level=level;
        this.areaNumber=areaNumber;
        world=new World(new Vector2(0,-9.8f),true);
        world.setContactListener(new CollisionListener());

        setUpRenderingStuff();

        firstRenderLayer=new int[]{tiledMap.getLayers().getIndex("Tile Layer 1")};// make array of Tile Layers to render

        MapBodyBuilder.buildShapes(tiledMap, world,"Primary Level Collision");// build primary level collision

        loadMapObjects();
        System.out.println("levelArea "+levelArea);
        for(String underGroundArea : underGroundAreas){
            if(underGroundArea.equals(levelArea)){
                underGround = true;
                break;
            }
        }


    }
    private void setUpRenderingStuff(){
        camera=new OrthographicCamera();
        camera.setToOrtho(false,GameCore.WIDTH,GameCore.HEIGHT);
        viewport=new FitViewport(GameCore.WIDTH,GameCore.HEIGHT,camera);

        renderer=new TextureMapObjectRenderer(tiledMap,GameCore.metersPerPixel);
        renderer.setView(camera);

        renderer.getBatch().setProjectionMatrix(viewport.getCamera().combined);

    }
    private void loadMapObjects(){
        playerObject=(TiledMapTileMapObject)(tiledMap.getLayers().get("Player Layer").getObjects().get("Player"));
        if(playerObject==null){
            throw new NullPointerException("playerObject is null at "+level.toString()+" "+areaNumber);
        }
        // get the folder of layers (each layer has a different interactable object)
        MapLayers objectSets = ((MapGroupLayer) tiledMap.getLayers().get("InteractableObjects")).getLayers();

        // for each type of interactable tile in the folder "Interactive Objects"
        for(MapLayer mapLayer:objectSets){

            // gets the layer's name to determine what type of object it holds
            switch(mapLayer.getName()){

                case "QuestionBlock"-> loadQuestionBlocks(mapLayer.getObjects());
                case "Enemy"-> loadEnemies(mapLayer.getObjects());
                case "Brick"-> loadBricks(mapLayer.getObjects());
                case "LevelEndFlag"->loadLevelEndFlag(mapLayer.getObjects());
                case "Pipe"->loadPipes(mapLayer.getObjects());
            }
        }
    }
    public void show(){
//        cameraPos=new Vector3(player.getXPosition(),camera.position.y,camera.position.z);
        player=new Player(playerObject,InteractableObject.getTilesFor("Player"),this);
    }
    public void show(int pipeID){
//        cameraPos=new Vector3(player.getXPosition(),camera.position.y,camera.position.z);
        player=new Player(playerObject,InteractableObject.getTilesFor("Player"),this,pipes.get(pipeID).getPlayerExitPoint());
    }
    public void hide(){
//        cameraPos=new Vector3(player.getXPosition(),camera.position.y,camera.position.z);
        world.destroyBody(player.body);
        player=null;
    }
    public Vector2 getPlayerPosition(){
        if(player!=null){
            return player.getPosition();
        }else{
            return new Vector2(pixelsToUnits(playerObject.getX()) , pixelsToUnits(playerObject.getY()));
        }
    }


    public void render(float delta){
        player.input(delta);
        Vector2 playerPositionInitial=player.getPosition();

        doPhysicsStep(delta);
        updateCamera();

        //update interactable objects holder
        for(InteractableObject object:interactableObjects){
            object.update(delta);
        }
        for(InteractableObject pipe:pipesList){
            pipe.update(delta);
        }
        while(!interactableObjectsAdd.isEmpty()){
            interactableObjects.addFirst(interactableObjectsAdd.removeFirst());
        }
        while(!interactableObjectsRemove.isEmpty()){
            interactableObjects.remove(interactableObjectsRemove.removeFirst());
        }
        player.update(delta);

        //prepare for rendering
        if(underGround){
            ScreenUtils.clear(Color.BLACK);
        }else{
            ScreenUtils.clear(new Color(92/255f,148/255f,252/255f,1));
        }

        camera.update();
        //render level
        renderer.render(firstRenderLayer);//render tiles
        renderer.begin();
        renderer.renderInteractableObjects(interactableObjects);
        player.render(renderer);
        renderer.renderInteractableObjects(pipesList);
        renderer.end();
        //debugRenderer.render(world,camera.combined);
        player.deathCheck();
    }
    public float pixelsToUnits(float pixels){return pixels*GameCore.metersPerPixel;}
    public void reset(){
        world.dispose();
        world=new World(new Vector2(0,-9.8f),true);
        MapBodyBuilder.buildShapes(tiledMap, world,"Primary Level Collision");// build primary level collision
        for(InteractableObject object:interactableObjects){
            object.levelReset();
        }
        for(InteractableObject pipe:pipesList){
            pipe.levelReset();
        }
    }
    private void loadPipes(MapObjects pipes){
        for(MapObject pipe : pipes) {
            pipesList.add(new Pipe((TiledMapTileMapObject)pipe,this));
        }
    }
    private void loadQuestionBlocks(MapObjects questionBlocks){
        for(MapObject questionBlock : questionBlocks) {
            interactableObjects.add(new QuestionBlock((TiledMapTileMapObject)questionBlock,this));
        }
    }
    private void loadEnemies(MapObjects enemies){
        for (MapObject enemy : enemies) {
            switch (((TiledMapTileMapObject)enemy).getTile().getProperties().get("relatedObject",String.class)){
                case "BasicEnemy"->{
                    interactableObjects.add(new BasicEnemy((TiledMapTileMapObject)enemy,this));
                }
            }
        }
    }
    private void loadBricks(MapObjects bricks){
        for (MapObject brick : bricks) {
            interactableObjects.add(new Brick((TiledMapTileMapObject)brick,this));
        }
    }
    private void loadLevelEndFlag(MapObjects flagParts){
        TiledMapTileMapObject flag;
        TiledMapTileMapObject flagPole;
        if((flagParts.get(0)).getName().equals("Flag")){
            flag=(TiledMapTileMapObject)flagParts.get(0);
            flagPole=(TiledMapTileMapObject)flagParts.get(1);
        }else{
            flag=(TiledMapTileMapObject)flagParts.get(1);
            flagPole=(TiledMapTileMapObject)flagParts.get(0);
        }
        interactableObjects.add(new LevelEndFlag(flag,flagPole,this));
    }
    private void doPhysicsStep(float deltaTime){
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
    private void updateCamera(){
//        Vector2 playerChange=player.getPosition().sub(playerPositionInitial);
//        Vector3 playerChangeCleaned=new Vector3(playerChange.x,0,0);
//        cameraPos.add(playerChangeCleaned);
//        camera.position.set(cameraPos);
        camera.position.set(player.getXPosition(),7.5f,camera.position.z);
        camera.update();
        renderer.setView(camera);
    }
}
