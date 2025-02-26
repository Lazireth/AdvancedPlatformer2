package com.github.lazireth.AdvancedPlatformer2.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.lazireth.AdvancedPlatformer2.Player;
import com.github.lazireth.AdvancedPlatformer2.render.TextureMapObjectRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.github.lazireth.AdvancedPlatformer2.GameCore.GlobalVariables.*;

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
    public static float pixelsToMeters(float pixels){return pixels*metersPerPixel;}
    public static float unitsToMeters(float meters){return Math.round(meters*pixelsPerMeter);}
    public abstract void render(TextureMapObjectRenderer renderer);
    public abstract void update(float delta);
    public abstract void startInteractionWithPlayer(Player player);
}
