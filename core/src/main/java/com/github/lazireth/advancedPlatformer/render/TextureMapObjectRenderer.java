package com.github.lazireth.advancedPlatformer.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.lazireth.advancedPlatformer.objects.InteractableObject;

import java.util.ArrayList;

/// got the initial code for this from
/// <a href="https://gamedev.stackexchange.com/questions/103696/tiled-object-layer-draw-sprites">...</a>
public class TextureMapObjectRenderer extends OrthogonalTiledMapRenderer {

    public TextureMapObjectRenderer(TiledMap map) {
        super(map);
    }

    public TextureMapObjectRenderer(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public TextureMapObjectRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public TextureMapObjectRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }
    @Override
    public void renderObjects (MapLayer layer) {
        batch.begin();
        for (MapObject object : layer.getObjects()) {
            renderObject(object);
        }
        batch.end();
    }
    public void renderInteractableObjects(ArrayList<InteractableObject> objects){
        for(InteractableObject object:objects){
            object.render(this);
        }
    }
    @Override
    public void renderObject(MapObject object) {
        if(object instanceof TextureMapObject textureObj) {
            batch.draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY(),
                textureObj.getOriginX(), textureObj.getOriginY(), textureObj.getTextureRegion().getRegionWidth(), textureObj.getTextureRegion().getRegionHeight(),
                textureObj.getScaleX(), textureObj.getScaleY(), textureObj.getRotation());
            if(textureObj.getProperties().containsKey("this")) System.out.println(textureObj.getRotation());
        } else if(object instanceof RectangleMapObject rectObject){
            Rectangle rect = rectObject.getRectangle();
            ShapeRenderer sr = new ShapeRenderer();
            sr.setProjectionMatrix(batch.getProjectionMatrix());
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.rect(rect.x, rect.y, rect.width, rect.height);
            sr.end();
        }
    }

    public void renderObject(TextureRegion textureRegion, float x, float y, float width, float height){
        batch.draw(textureRegion,x-width/2, y-height/2,width,height);
    }
    public void renderObject(TextureRegion textureRegion, Vector2 pos, float width, float height){
        batch.draw(textureRegion,pos.x-width/2, pos.y-height/2,width,height);
    }
    public void begin(){
        batch.begin();
    }
    public void end(){
        batch.end();
    }
}
