package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

public abstract class InteractableObject{
    public Body body;
    public ArrayList<TiledMapTile> getTilesFor(String relatedObject){
        return GameCore.gameScreen.level.getTilesFor(relatedObject);
    }
    public ArrayList<TextureRegion> getSpritesFor(String relatedObject){
        ArrayList<TextureRegion> textureList=new ArrayList<>();
        for(TiledMapTile tile: GameCore.gameScreen.level.getTilesFor(relatedObject)){
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
    public abstract void render(TextureMapObjectRenderer renderer);
    public abstract void update();
    public abstract void startInteraction(Player player);
}
