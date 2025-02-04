package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Area;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class InteractableObject{
    public Body body;

    public static final Map<String, ArrayList<TiledMapTile>> tilesByRelatedObject=new HashMap<>();

    public static ArrayList<TiledMapTile> getTilesFor(String relatedObject){
        return tilesByRelatedObject.get(relatedObject);
    }
    public static ArrayList<TextureRegion> getSpritesFor(String relatedObject){
        ArrayList<TextureRegion> textureList=new ArrayList<>();
        for(TiledMapTile tile: tilesByRelatedObject.get(relatedObject)){
            textureList.add(tile.getTextureRegion());
        }
        return textureList;
    }
    public static float pixelsToUnits(float pixels){
        return pixels*GameCore.metersPerPixel;
    }
    public static float unitsToPixels(float units){
        return Math.round(units*GameCore.pixelsPerMeter);
    }

    public static void loadTiles(TiledMap tiledMap){
        TiledMapTileSets tileSets = tiledMap.getTileSets();
        for(TiledMapTileSet tileSet:tileSets){
            loadTileset(tileSet);
        }
        for(String key : tilesByRelatedObject.keySet()){
            orderArrayListByTileState(tilesByRelatedObject.get(key));
        }
    }
    private static void orderArrayListByTileState(ArrayList<TiledMapTile> arrayList){
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
    @SuppressWarnings("Java8MapApi")
    private static void loadTileset(TiledMapTileSet tileSet){
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

    public abstract void render(TextureMapObjectRenderer renderer);
    public abstract void update(float delta);
    public abstract void startInteractionWithPlayer(Player player);
    public abstract void levelReset();
}
