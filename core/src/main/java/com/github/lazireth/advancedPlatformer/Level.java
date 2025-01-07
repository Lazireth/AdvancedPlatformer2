package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
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
import java.util.Arrays;
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
    public TiledMapTileSet playerSpriteTiles;

    public Level(int levelNumber){
        getMapToLoad(levelNumber);
        TiledMapTileSets tileSets = map.getTileSets();// load all tilesets

        GameCore.renderer=new TextureMapObjectRenderer(map,GameCore.unitsPerPixel);
        GameCore.renderer.setView(GameCore.camera);
        firstRenderLayer=new int[]{map.getLayers().getIndex("Tile Layer 1")};// make array of Tile Layers to render

        MapBodyBuilder.buildShapes(map, GameScreen.world,"Primary Level Collision");// build primary level collision



        // get helper variable to give interactable objects their data and sprites

        Map<String, TiledMapTile[]> environmentTileset=getTilesMapArray(tileSets.getTileSet("Environment Tileset"));
        playerSpriteTiles = tileSets.getTileSet("Player Tileset");
        System.out.println("playerSpriteTiles "+playerSpriteTiles.size());

        // get player start position
        TiledMapTileMapObject playerObject=(TiledMapTileMapObject)(map.getLayers().get("Player Layer").getObjects().get("Player"));
        playerStartingPos=new Vector2(playerObject.getX()*GameCore.unitsPerPixel,playerObject.getY()*GameCore.unitsPerPixel);// also need to convert to game units

        // get the folder of layers (each layer has a different interactable object)
        MapLayers objectSets = ((MapGroupLayer)map.getLayers().get("InteractableObjects")).getLayers();

        // for each type of interactable tile in the folder "Interactive Objects"
        for(int objectTypeIndex=0;objectTypeIndex<objectSets.size();objectTypeIndex++){

            // gets the layer's name to determine what type of object it holds
            switch(objectSets.get(objectTypeIndex).getName()){

                case "QuestionBlock"->{
                    loadQuestionBlocks(objectSets.get(objectTypeIndex).getObjects(), environmentTileset);
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
    public Map<String, TiledMapTile[]> getTilesMapArray(TiledMapTileSet tileSet){//file contains sprites for multiple things with multiple states

        //first chunk of code gets it in a map of arraylists each arraylist is mapped to an object type
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
        System.out.println("objectTilesMap "+ Arrays.toString(objectTilesMap.keySet().toArray()));
        for(String relatedObject:objectTilesMap.keySet()){
            TiledMapTile[] tempTiledMapTile=new TiledMapTile[objectTilesMap.get(relatedObject).size()];
            for(int i=0;i<tempTiledMapTile.length;i++){
                try{
                    tempTiledMapTile[objectTilesMap.get(relatedObject).get(i).getProperties().get("State",int.class)]=objectTilesMap.get(relatedObject).get(i);
                } catch (Exception e) {
                    tempTiledMapTile[0]=objectTilesMap.get(relatedObject).get(i);
                }
            }
            objectTilesMapArray.put(relatedObject,tempTiledMapTile);
        }

        return objectTilesMapArray;
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
    private void loadQuestionBlocks(MapObjects questionBlocks, Map<String,TiledMapTile[]> environmentTileset){
        for(int questionBlockIndex=0;questionBlockIndex< questionBlocks.getCount() ;questionBlockIndex++){//for each question block
            // add it to interactableObjects
            interactableObjects.add(
                new QuestionBlock(questionBlocks.get("QB "+questionBlockIndex),
                    environmentTileset));
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
