package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.lazireth.advancedPlatformer.FontManager;
import com.github.lazireth.advancedPlatformer.GameCore;


import static com.github.lazireth.advancedPlatformer.GameCore.*;
import static com.github.lazireth.advancedPlatformer.GameCore.renderer;
import static com.github.lazireth.advancedPlatformer.InputHandler.keys;

public class GameStartScreen extends ScreenAdapter {
    final private GameCore game;

    BitmapFont calibri64;
    BitmapFont calibri128;
    int refreshRate;
    public GameStartScreen(final GameCore game){
        this.game=game;
        refreshRate=Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate;
        calibri64= FontManager.getFont("Calibri",64);
        calibri128= FontManager.getFont("Calibri",128);
    }
    @Override
    public void render(float delta) {
        input();
        draw();
    }
    void input(){
        if(keys[Input.Keys.SPACE]){
            game.loadLevelStartScreen();
            keys[Input.Keys.SPACE]=false;
        }
    }
    private void draw(){
        renderer.begin();
        ScreenUtils.clear(Color.BLACK);
        renderer.drawText("Advanced Platformer",calibri128,WIDTH/2,HEIGHT/4*3);
        renderer.drawText("Move with A&D",calibri64,WIDTH*0.4f,HEIGHT/16*10f);
        renderer.drawText("Run with K",calibri64,WIDTH*0.6f,HEIGHT/16*10f);
        renderer.drawText("Jump with W (hold longer for higher jump)",calibri64,WIDTH/2,HEIGHT/16*8.75f);
        renderer.drawText("Close game with Alt+F4",calibri64,WIDTH/2,HEIGHT/16*7.5f);
        renderer.drawText("Press SPACE To Start",calibri64,WIDTH/2,HEIGHT/16*5.5f);
        renderer.drawText("NOTE: Gameplay is being recorded for development purposes",calibri64,WIDTH/2,HEIGHT/16*3.5f);

        if(refreshRate>61){
            renderer.drawText("If you see this message the update rate has not been updated",calibri64,WIDTH/2,HEIGHT/16*3f);
            renderer.drawText("this can cause significant bugs, inform the developer",calibri64,WIDTH/2,HEIGHT/16*2f);
            renderer.drawText("and it will be very quickly fixed",calibri64,WIDTH/2,HEIGHT/16f);
        }
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
