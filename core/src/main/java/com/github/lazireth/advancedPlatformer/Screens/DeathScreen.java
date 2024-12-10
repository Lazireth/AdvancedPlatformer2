package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import static com.github.lazireth.advancedPlatformer.GameCore.*;
import static com.github.lazireth.advancedPlatformer.InputHandler.keys;

public class DeathScreen extends ScreenAdapter {
    final private GameCore game;
    public OrthographicCamera myCamera;
    public FitViewport myViewport;
    SpriteBatch spriteBatch;

    float restartTimer;

    BitmapFont fontCalibri;
    TextureMapObjectRenderer textureMapObjectRenderer;
    public DeathScreen(final GameCore game){
        this.game=game;

        myCamera=new OrthographicCamera();
        System.out.println(WIDTH*pixelsPerUnit+","+HEIGHT*pixelsPerUnit);

        myCamera.setToOrtho(false, WIDTH*pixelsPerUnit, HEIGHT*pixelsPerUnit);

        myViewport=new FitViewport(WIDTH*pixelsPerUnit, HEIGHT*pixelsPerUnit,myCamera);

        System.out.println(WIDTH*pixelsPerUnit+","+HEIGHT*pixelsPerUnit);
        FreeTypeFontGenerator generator=new FreeTypeFontGenerator(Gdx.files.internal("fonts/Calibri.ttf"));
        FreeTypeFontParameter parameter=new FreeTypeFontParameter();
        parameter.size=16;
        fontCalibri=generator.generateFont(parameter);
        generator.dispose();
        spriteBatch=new SpriteBatch();
        //set up rendered
    }
    @Override
    public void render(float delta) {
        input(delta);
        draw();
    }
    void input(float delta){
        if(keys[Keys.SPACE]){
            restartTimer=5;
        }
    }
    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        myViewport.apply();
        spriteBatch.setProjectionMatrix(myCamera.combined);
        spriteBatch.begin();
        fontCalibri.draw(spriteBatch, "test",200,200);
        spriteBatch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        game.resize(width,height);
    }
}
