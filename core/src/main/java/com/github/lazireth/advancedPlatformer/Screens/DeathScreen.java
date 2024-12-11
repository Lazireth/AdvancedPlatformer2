package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import com.badlogic.gdx.utils.ScreenUtils;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import static com.github.lazireth.advancedPlatformer.GameCore.*;
import static com.github.lazireth.advancedPlatformer.InputHandler.keys;

public class DeathScreen extends ScreenAdapter {
    final private GameCore game;

    float restartTimer=-1;

    BitmapFont fontCalibri;
    TextureMapObjectRenderer textureMapObjectRenderer;
    public DeathScreen(final GameCore game){
        this.game=game;
        System.out.println(WIDTH*pixelsPerUnit+","+HEIGHT*pixelsPerUnit);
        FreeTypeFontGenerator generator=new FreeTypeFontGenerator(Gdx.files.internal("fonts/Calibri.ttf"));
        FreeTypeFontParameter parameter=new FreeTypeFontParameter();
        parameter.size=64;
        fontCalibri=generator.generateFont(parameter);
        generator.dispose();
    }
    @Override
    public void render(float delta) {
        input(delta);
        update(delta);
        draw();
    }
    void input(float delta){
        if(keys[Keys.SPACE]&&restartTimer==-1){
            restartTimer=5;
        }
    }
    private void update(float delta){

        if(restartTimer>0){
            restartTimer-=delta;
        }
    }
    private void draw(){
        renderer.begin();
        ScreenUtils.clear(Color.BLACK);
        if(restartTimer>0){
            renderer.drawText((int)restartTimer+"",fontCalibri,WIDTH/2,HEIGHT/4*2);
        }
        renderer.drawText("Press SPACE To Start",fontCalibri,WIDTH/2,HEIGHT/4*1);

        renderer.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        game.resize(width,height);
    }
}
