package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.Disposable;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.objects.InteractableObject;
import com.github.lazireth.advancedPlatformer.objects.QuestionBlock;
import com.github.lazireth.advancedPlatformer.objects.enemies.BasicEnemy;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.*;
import java.util.Map;

public class Level implements Disposable{
    public AssetManager assetManager=new AssetManager();
    public TiledMap map;
    public int[] firstRenderLayer;

    private final ArrayList<InteractableObject> interactableObjects=new ArrayList<>();
    public final ArrayList<InteractableObject> interactableObjectsAdd=new ArrayList<>();
    public final ArrayList<InteractableObject> interactableObjectsRemove=new ArrayList<>();

    private final Map<String, ArrayList<TiledMapTile>> tilesByRelatedObject =new HashMap<>();

    public TiledMapTileMapObject playerObject;

    public Level(int levelNumber){
        getMapToLoad(levelNumber);
        TiledMapTileSets tileSets = map.getTileSets();// load all tilesets
        loadTilesets(tileSets);
        InteractableObject.currentLevel=this;

        GameCore.renderer=new TextureMapObjectRenderer(map,GameCore.metersPerPixel);
        GameCore.renderer.setView(GameCore.camera);
        firstRenderLayer=new int[]{map.getLayers().getIndex("Tile Layer 1")};// make array of Tile Layers to render

        MapBodyBuilder.buildShapes(map, GameScreen.world,"Primary Level Collision");// build primary level collision


        playerObject=(TiledMapTileMapObject)(map.getLayers().get("Player Layer").getObjects().get("Player"));


        // get the folder of layers (each layer has a different interactable object)
        MapLayers objectSets = ((MapGroupLayer)map.getLayers().get("InteractableObjects")).getLayers();

        // for each type of interactable tile in the folder "Interactive Objects"
        for(MapLayer mapLayer:objectSets){

            // gets the layer's name to determine what type of object it holds
            switch(mapLayer.getName()){

                case "QuestionBlock"->{
                    loadQuestionBlocks(mapLayer.getObjects());
                }
                case "Enemy"->{
                    //loadEnemies(mapLayer.getObjects());
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

    public ArrayList<TiledMapTile> getTilesFor(String relatedObject){
        return tilesByRelatedObject.get(relatedObject);
    }
    public void loadTilesets(TiledMapTileSets tileSetsToLoad){
        for(TiledMapTileSet tileSet:tileSetsToLoad){
            loadTileset(tileSet);
        }
        for(String key : tilesByRelatedObject.keySet()){
            orderArrayListByTileState(tilesByRelatedObject.get(key));
        }
    }
    public void loadTileset(TiledMapTileSet tileSet){
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
    public void orderArrayListByTileState(ArrayList<TiledMapTile> arrayList){
        if(arrayList.size()==1){
            return;
        }
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
    private void loadQuestionBlocks(MapObjects questionBlocks){

        for (MapObject questionBlock : questionBlocks) {
            interactableObjects.add(new QuestionBlock((TiledMapTileMapObject)questionBlock));
        }
    }
    private void loadEnemies(MapObjects enemies){
        for (MapObject enemy : enemies) {
            switch (((TiledMapTileMapObject)enemy).getTile().getProperties().get("Related Object",String.class)){
                case "BasicEnemy"->{
                    interactableObjects.add(new BasicEnemy((TiledMapTileMapObject)enemy));
                }
            }
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
