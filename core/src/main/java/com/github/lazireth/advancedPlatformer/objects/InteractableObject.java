package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Level;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

public abstract class InteractableObject{
    public Body body;
    public static Level currentLevel;//why the fuck is this needed
    public ArrayList<TiledMapTile> getTilesFor(String relatedObject){
        return currentLevel.getTilesFor(relatedObject);
    }
    public ArrayList<TextureRegion> getSpritesFor(String relatedObject){
        ArrayList<TextureRegion> textureList=new ArrayList<>();
        for(TiledMapTile tile: currentLevel.getTilesFor(relatedObject)){
            textureList.add(tile.getTextureRegion());
        }
        return textureList;
    }
    public ArrayList<TextureRegion> getSpritesFor(ArrayList<TiledMapTile> arrayList){
        ArrayList<TextureRegion> textureList=new ArrayList<>();
        for(TiledMapTile tile: arrayList){
            textureList.add(tile.getTextureRegion());
        }
        return textureList;
    }
    public float pixelsToUnits(float pixels){
        return pixels*GameCore.metersPerPixel;
    }
    public float unitsToPixels(float units){
        return Math.round(units*GameCore.pixelsPerMeter);
    }
    public abstract void render(TextureMapObjectRenderer renderer);
    public abstract void update(float delta);
    public abstract void startInteractionWithPlayer(Player player);
    public abstract void levelReset();
}
