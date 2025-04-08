package com.github.lazireth.AdvancedPlatformer2.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.lazireth.AdvancedPlatformer2.LevelMap;
import com.github.lazireth.AdvancedPlatformer2.objects.InteractableObject;
import com.github.lazireth.AdvancedPlatformer2.overworld.Overworld;

import java.util.ArrayList;

import static com.github.lazireth.AdvancedPlatformer2.GameCore.GlobalVariables.*;

/// got the initial code for this from
/// <a href="https://gamedev.stackexchange.com/questions/103696/tiled-object-layer-draw-sprites">...</a>
public class TextureMapObjectRenderer extends OrthogonalTiledMapRenderer {
    GlyphLayout layout=new GlyphLayout();

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
    public void renderFullFrame(int[] renderLayer, ArrayList<InteractableObject> interactableObjects, ArrayList<InteractableObject> pipes){
        ScreenUtils.clear(Color.BLUE);
        beginRender();
        LevelMap.updateCamera();
        render(renderLayer);
        renderInteractableObjects(interactableObjects);
        LevelMap.player.render(this);
        renderInteractableObjects(pipes);
        endRender();
        if(debugging){
            LevelMap.debugRender();
        }
    }
    public void renderFullFrame(int[] renderLayer, ArrayList<InteractableObject> interactableObjects){
        ScreenUtils.clear(Color.BLACK);
        beginRender();
        Overworld.updateCamera();
        render(renderLayer);
        renderInteractableObjects(interactableObjects);
        Overworld.OverworldPlayer.render(this);
        endRender();
        if(debugging){
            Overworld.debugRender();
        }
    }
    public void renderInteractableObjects(ArrayList<InteractableObject> objects){
        for(InteractableObject object:objects){
            object.render(this);
        }
    }
    @Override
    public void render(int[] layers) {
        for(int layerIdx : layers) {
            MapLayer layer = this.map.getLayers().get(layerIdx);
            this.renderMapLayer(layer);
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
    public void drawText(String str, BitmapFont bitmapFont, float x, float y){drawText(str,bitmapFont,x,y,true,true);}
    public void drawText(String str, BitmapFont bitmapFont, float x, float y, boolean centerX, boolean centerY){
        // Sources for how to do what this method does
        // Deepscorn at https://gamedev.stackexchange.com/questions/73688/why-is-my-text-is-too-large-even-when-scaled-to-05f-in-libgdx
        // bemeyer at https://stackoverflow.com/questions/16600547/how-get-a-string-width-in-libgdx
        Matrix4 originalMatrix = batch.getProjectionMatrix().cpy();

        layout.setText(bitmapFont,str);
        float xPos=x* pixelsPerMeter;
        float yPos=y* pixelsPerMeter;
        if(centerX){
            xPos-=layout.width/2;
        }
        if(centerY){
            yPos+=layout.height/2;
        }
        batch.setProjectionMatrix(originalMatrix.cpy().scale(metersPerPixel, metersPerPixel,1));
        bitmapFont.draw(batch,str,xPos,yPos);
        batch.setProjectionMatrix(originalMatrix);

    }

    public void renderObject(TextureRegion textureRegion, float x, float y, float width, float height){batch.draw(textureRegion,x-width/2, y-height/2,width,height);}
    public void renderObject(TextureRegion textureRegion, Vector2 pos, float width, float height){batch.draw(textureRegion,pos.x-width/2, pos.y-height/2,width,height);}
    public void begin(){beginRender();}
    public void end(){endRender();}
}
