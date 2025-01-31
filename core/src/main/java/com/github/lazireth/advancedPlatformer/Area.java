package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.LevelEndFlag;
import com.github.lazireth.advancedPlatformer.objects.Brick;
import com.github.lazireth.advancedPlatformer.objects.InteractableObject;
import com.github.lazireth.advancedPlatformer.objects.QuestionBlock;
import com.github.lazireth.advancedPlatformer.objects.enemies.BasicEnemy;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.*;
import java.util.Map;

public class Area implements Disposable{
    public AssetManager assetManager=new AssetManager();
    public TiledMap tiledMap;
    public int[] firstRenderLayer;

    private final ArrayList<InteractableObject> interactableObjects=new ArrayList<>();
    public final ArrayList<InteractableObject> interactableObjectsAdd=new ArrayList<>();
    public final ArrayList<InteractableObject> interactableObjectsRemove=new ArrayList<>();

    private final Map<String, ArrayList<TiledMapTile>> tilesByRelatedObject =new HashMap<>();

    public TiledMapTileMapObject playerObject;

    private static final float TIME_STEP=1/60f;
    private static final int VELOCITY_ITERATIONS=4;
    private static final int POSITION_ITERATIONS=6;
    private static float accumulator=0;
    public static World world;
    Box2DDebugRenderer debugRenderer;

    public static Player player;

    public Area(String map){
        tiledMap =new TmxMapLoader().load("Map/"+map+".tmx");
        TiledMapTileSets tileSets = tiledMap.getTileSets();// load all tilesets
        loadTilesets(tileSets);
        InteractableObject.currentArea =this;

        GameCore.renderer=new TextureMapObjectRenderer(tiledMap,GameCore.metersPerPixel);
        GameCore.renderer.setView(GameCore.camera);
        firstRenderLayer=new int[]{tiledMap.getLayers().getIndex("Tile Layer 1")};// make array of Tile Layers to render

        MapBodyBuilder.buildShapes(tiledMap, GameScreen.world,"Primary Level Collision");// build primary level collision


        playerObject=(TiledMapTileMapObject)(tiledMap.getLayers().get("Player Layer").getObjects().get("Player"));
        if(playerObject==null){
            throw new NullPointerException("playerObject is null");
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
            }
        }

    }
    public void doUpdateTick(float delta){

    }

    public void reset(){
        for(InteractableObject object:interactableObjects){
            object.levelReset();
        }
    }
    public ArrayList<TiledMapTile> getTilesFor(String relatedObject){
        return tilesByRelatedObject.get(relatedObject);
    }

    private void orderArrayListByTileState(ArrayList<TiledMapTile> arrayList){
        if(arrayList.size()==1){return;}

        for(int i=1;i<arrayList.size();i++){
            TiledMapTile key=arrayList.get(i);
            int j=i-1;

            while(j>=0&&arrayList.get(j).getProperties().get("state",int.class)>key.getProperties().get("state",int.class)){
                arrayList.set(j+1,arrayList.get(j));
                j--;
            }
            arrayList.set(j+1,key);
        }
    }

    public void update(float delta){
        for(InteractableObject object:interactableObjects){
            object.update(delta);
        }
        while(!interactableObjectsAdd.isEmpty()){interactableObjects.addFirst(interactableObjectsAdd.removeFirst());}
        while(!interactableObjectsRemove.isEmpty()){interactableObjects.remove(interactableObjectsRemove.removeFirst());}
    }

    public void render(){
        GameCore.renderer.render(firstRenderLayer);//render tiles
        GameCore.renderer.begin();
        GameCore.renderer.renderInteractableObjects(interactableObjects);
        GameScreen.player.render(GameCore.renderer);
        GameCore.renderer.end();
    }
    private void loadQuestionBlocks(MapObjects questionBlocks){
        for (MapObject questionBlock : questionBlocks) {
            interactableObjects.add(new QuestionBlock((TiledMapTileMapObject)questionBlock));
        }
    }
    private void loadEnemies(MapObjects enemies){
        for (MapObject enemy : enemies) {
            switch (((TiledMapTileMapObject)enemy).getTile().getProperties().get("relatedObject",String.class)){
                case "BasicEnemy"->{
                    interactableObjects.add(new BasicEnemy((TiledMapTileMapObject)enemy));
                }
            }
        }
    }
    private void loadBricks(MapObjects bricks){
        for (MapObject brick : bricks) {
            interactableObjects.add(new Brick((TiledMapTileMapObject)brick));
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

        interactableObjects.add(new LevelEndFlag(flag,flagPole));
    }
    private void loadTilesets(TiledMapTileSets tileSetsToLoad){
        for(TiledMapTileSet tileSet:tileSetsToLoad){
            loadTileset(tileSet);
        }
        for(String key : tilesByRelatedObject.keySet()){
            orderArrayListByTileState(tilesByRelatedObject.get(key));
        }
    }
    private void loadTileset(TiledMapTileSet tileSet){
        for(TiledMapTile tile:tileSet){
            try{
                String relatedObject=tile.getProperties().get("relatedObject",String.class);
                if(relatedObject==null){
                    continue;
                }
                if(tilesByRelatedObject.get(relatedObject)==null){
                    tilesByRelatedObject.put(relatedObject,new ArrayList<>());
                }
                tilesByRelatedObject.get(relatedObject).add(tile);
            } catch (Exception ignore) {}
        }
    }
    public void dispose(){
        tiledMap.dispose();
        assetManager.dispose();
        GameCore.renderer.dispose();
    }
}
