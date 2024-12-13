package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.lazireth.advancedPlatformer.FontManager;
import com.github.lazireth.advancedPlatformer.GameCore;

import static com.github.lazireth.advancedPlatformer.GameCore.*;
import static com.github.lazireth.advancedPlatformer.GameCore.renderer;
import static com.github.lazireth.advancedPlatformer.InputHandler.keys;

public class StartScreen extends ScreenAdapter {
    final private GameCore game;

    BitmapFont calibri64;
    BitmapFont calibri128;
    public StartScreen(final GameCore game){
        this.game=game;
        calibri64= FontManager.getFont("Calibri",64);
        calibri128= FontManager.getFont("Calibri",128);
    }
    @Override
    public void render(float delta) {
        input();
        update(delta);
        draw();
    }
    void input(){
        if(keys[Input.Keys.SPACE]){
            game.loadGameScreen();
        }
    }
    private void update(float delta){

    }
    private void draw(){
        renderer.begin();
        ScreenUtils.clear(Color.BLACK);
        renderer.drawText("Advanced Platformer",calibri128,WIDTH/2,HEIGHT/4*3);
        renderer.drawText("Move with W A S D",calibri64,WIDTH/2,HEIGHT/4*2);
        renderer.drawText("Run with K",calibri64,WIDTH/2,HEIGHT/4*2);
        renderer.drawText("Press SPACE To Start",calibri64,WIDTH/2,HEIGHT/4*1);

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
