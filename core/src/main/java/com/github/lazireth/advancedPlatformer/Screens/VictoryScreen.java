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

public class VictoryScreen extends ScreenAdapter {
    final private GameCore game;

    float restartTimer=5;
    boolean countDown=false;
    BitmapFont calibri64;
    BitmapFont calibri128;
    public VictoryScreen(final GameCore game){
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
            game.restartGame();
            keys[Input.Keys.SPACE]=false;
        }
    }
    private void update(float delta){
        if(countDown){
            restartTimer-=delta;
        }
        if(restartTimer<=0){
            game.loadGameScreen();
        }
    }
    private void draw(){
        renderer.begin();
        ScreenUtils.clear(Color.BLACK);
        renderer.drawText("Victory",calibri128,WIDTH/2,HEIGHT/16*12);
        renderer.drawText("You win!",calibri64,WIDTH/2,HEIGHT/16*10);
        renderer.drawText("More content may be added",calibri64,WIDTH/2,HEIGHT/16*8);
        renderer.drawText("at a later date",calibri64,WIDTH/2,HEIGHT/16*5);
        renderer.drawText("Close the game with Alt+F4",calibri64,WIDTH/2,HEIGHT/16*2f);
        renderer.end();
    }

    @Override
    public void show() {
        restartTimer=5;
        countDown=false;
    }

    @Override
    public void resize(int width, int height) {
        game.resize(width,height);
    }
}
