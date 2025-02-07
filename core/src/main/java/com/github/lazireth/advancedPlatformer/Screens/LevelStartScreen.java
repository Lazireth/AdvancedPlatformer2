package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.lazireth.advancedPlatformer.FontManager;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Player;

import static com.github.lazireth.advancedPlatformer.GameCore.*;
import static com.github.lazireth.advancedPlatformer.InputHandler.keys;

public class LevelStartScreen extends ScreenAdapter {
    final private GameCore game;

    float restartTimer=5;
    boolean countDown=false;
    BitmapFont calibri64;
    BitmapFont calibri128;
    public LevelStartScreen(final GameCore game){
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
        renderer.drawText("Level "+GameScreen.levels[GameScreen.currentLevel],calibri128,WIDTH/2,HEIGHT/4*3);
        renderer.drawText("You have "+ Player.PlayerPersistentData.lives+" lives",calibri64,WIDTH/2,HEIGHT/16*8);
        if(countDown){
            renderer.drawText((int)restartTimer+"",calibri64,WIDTH/2,HEIGHT/16*4);
        }else{
            renderer.drawText("Press SPACE To Start",calibri64,WIDTH/2,HEIGHT/16*4);
        }

        if(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate>61){
            renderer.drawText("Playing with a display refresh rate higher than 60",calibri64,WIDTH/2,HEIGHT/16*2f);
            renderer.drawText("can lead to unintended behavior",calibri64,WIDTH/2,HEIGHT/16*0.6f);
        }
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
