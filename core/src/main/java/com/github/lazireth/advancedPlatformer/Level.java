package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.InteractableObject;
import com.github.lazireth.advancedPlatformer.objects.QuestionBlock;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Level implements Disposable{
    public AssetManager assetManager=new AssetManager();
    public TiledMap map;
    public int[] firstRenderLayer;

    private final ArrayList<InteractableObject> interactableObjects=new ArrayList<>();
    public final ArrayList<InteractableObject> interactableObjectsAdd=new ArrayList<>();
    public final ArrayList<InteractableObject> interactableObjectsRemove=new ArrayList<>();

    public TiledMapTile[] playerTextureTiles;
    public Vector2 playerStartingPos;
    public Level(int levelNumber){
        getMapToLoad(levelNumber);
        // set up renderer
        GameCore.renderer=new TextureMapObjectRenderer(map,GameCore.unitsPerPixel);
        GameCore.renderer.setView(GameCore.camera);

        // build primary level collision
        MapBodyBuilder.buildShapes(map, GameScreen.world,"Primary Level Collision");

        // make array of Tile Layers to render
        firstRenderLayer=new int[]{map.getLayers().getIndex("Tile Layer 1")};

        // load all tilesets
        TiledMapTileSets tileSets = map.getTileSets();

        // get helper variable to give interactable objects their other states
        Map<String, TiledMapTile[]> specialTilesMap = getTilesMapArray(tileSets.getTileSet("Special Tiles"));
        Map<String, TiledMapTile>   itemTiles       = getTilesMapSingle(tileSets.getTileSet("Item Sprites"));

        // grab some stuff for setting up the player
        playerTextureTiles=specialTilesMap.get("Player");
        TiledMapTileMapObject playerObject=(TiledMapTileMapObject)(map.getLayers().get("Player Layer").getObjects().get("Player"));
        playerStartingPos=new Vector2(playerObject.getX()*GameCore.unitsPerPixel,playerObject.getY()*GameCore.unitsPerPixel);// also need to convert to game units

        // get the layer Group Layer that contains layers of each interactable tile type
        MapLayers objectSets = ((MapGroupLayer)map.getLayers().get("Interactive Objects")).getLayers();

        // for each type of interactable tile
        for(int objectTypeIndex=0;objectTypeIndex<objectSets.size();objectTypeIndex++){

            // get which interactable object this layer contains
            switch(objectSets.get(objectTypeIndex).getProperties().get("WhichInteractableObject",String.class)){

                case "QuestionBlock"->{//do this for questionBlocks
                    loadQuestionBlocks(objectSets.get(objectTypeIndex).getObjects(), specialTilesMap.get("QuestionBlock"), itemTiles);
                }

            }
        }

    }
    // there is no good way to get a specific tile with iterating through tileSet
    // because the only way to get a tile is to call getTile(int id) and the ids are generated for each tile at runtime
    // I hate it, but it is necessary
    // also I just learned that IntelliJ has grammar check, you see that comma on the previous line, IntelliJ suggested that
    public TiledMapTile[] getTilesArray(TiledMapTileSet tileSet){//use when a file contains tiles only one thing
        TiledMapTile[] tiles=new TiledMapTile[tileSet.size()];
        for(TiledMapTile tile:tileSet){
            try{
                tiles[tile.getProperties().get("Local ID",int.class)]=tile;
            } catch (Exception ignore) {}
        }
        return tiles;
    }
    // very good spaghetti
    public Map<String, TiledMapTile[]> getTilesMapArray(TiledMapTileSet tileSet){//use when a file contains tiles used for multiple things

        //first chunk of code gets it in a map of arraylists
        Map<String, ArrayList<TiledMapTile>> objectTilesMap=new HashMap<>();
        for(TiledMapTile tile:tileSet){
            try{

                String relatedObject=tile.getProperties().get("Related Object",String.class);
                if(relatedObject==null){
                    continue;
                }
                if(objectTilesMap.get(relatedObject)==null){
                    objectTilesMap.put(relatedObject,new ArrayList<>());
                }
                objectTilesMap.get(relatedObject).add(tile);
            } catch (Exception ignore) {}
        }
        Map<String, TiledMapTile[]> objectTilesMapArray=new HashMap<>();

        //this converts it from an arraylist to an array and sorts the tiles
        for(String relatedObject:objectTilesMap.keySet()){
            TiledMapTile[] tempTiledMapTile=new TiledMapTile[objectTilesMap.get(relatedObject).size()];
            for(int i=0;i<tempTiledMapTile.length;i++){
                tempTiledMapTile[objectTilesMap.get(relatedObject).get(i).getProperties().get("State",int.class)]=objectTilesMap.get(relatedObject).get(i);
            }
            objectTilesMapArray.put(relatedObject,tempTiledMapTile);
        }

        return objectTilesMapArray;
    }
    public Map<String, TiledMapTile> getTilesMapSingle(TiledMapTileSet tileSet){//use when a file contains tiles used for multiple things

        //first chunk of code gets it in a map of arraylists
        Map<String, TiledMapTile> objectTilesMap=new HashMap<>();
        for(TiledMapTile tile:tileSet){
            try{
                String relatedObject=tile.getProperties().get("Related Object",String.class);
                if(relatedObject==null){
                    continue;
                }
                objectTilesMap.put(relatedObject,tile);
            } catch (Exception ignore) {}
        }


        return objectTilesMap;
    }
    public void update(){
        for(InteractableObject object:interactableObjects){
            object.update();
        }
        while(!interactableObjectsAdd.isEmpty()){
            interactableObjects.addFirst(interactableObjectsAdd.removeFirst());
        }
        while(!interactableObjectsRemove.isEmpty()){
            interactableObjects.remove(interactableObjectsRemove.removeFirst());
        }
    }

    public void render(){
        GameCore.renderer.render(firstRenderLayer);//render tiles
        GameCore.renderer.begin();
        GameCore.renderer.renderInteractableObjects(interactableObjects);
        GameScreen.player.render(GameCore.renderer);
        GameCore.renderer.end();
    }
    private void loadQuestionBlocks(MapObjects questionBlocks, TiledMapTile[] tileSprites, Map<String,TiledMapTile> itemTiles){
        for(int questionBlockIndex=0;questionBlockIndex< questionBlocks.getCount() ;questionBlockIndex++){//for each question block
            // add it to interactableObjects
            interactableObjects.add(
                new QuestionBlock(questionBlocks.get("QB "+questionBlockIndex),
                    tileSprites, itemTiles));
        }
    }
    private void getMapToLoad(int levelNumber){
        if(levelNumber==-2){
            map=new TmxMapLoader().load("Map/testLevel.tmx");
        }else if(levelNumber>0){
            map=new TmxMapLoader().load("Map/level"+levelNumber+".tmx");
        }
    }
    public void dispose(){
        map.dispose();
        assetManager.dispose();
        GameCore.renderer.dispose();
    }
}
