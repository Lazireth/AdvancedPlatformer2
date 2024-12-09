package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.lazireth.advancedPlatformer.GameCore;

import static com.github.lazireth.advancedPlatformer.GameCore.*;
import static com.github.lazireth.advancedPlatformer.InputHandler.keys;

public class DeathScreen extends ScreenAdapter {
    float restartTimer;
    SpriteBatch spriteBatch;
    //BitmapFont font=new BitmapFont(Gdx.files.internal("Calibri.fnt"),Gdx.files.internal("Calibri.png"),false);
    public DeathScreen(){
        spriteBatch=new SpriteBatch();

        //set up rendered
    }
    @Override
    public void render(float delta) {
        input(delta);
        draw();
        super.render(delta);
    }
    void input(float delta){
        if(keys[Keys.SPACE]){
            restartTimer=5;
        }
    }
    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(GameCore.camera.combined);
        spriteBatch.begin();
        //font.getData().setScale(0.2f);
        //font.draw(spriteBatch, "test",WIDTH/2,HEIGHT/2);
        spriteBatch.end();
    }
    @Override
    public void resize(int width, int height) {
        GameCore.viewport.update(width, height, true);
        GameCore.camera.setToOrtho(false, WIDTH, HEIGHT);
        GameCore.gameScreen.level.updateRenderer();
        Gdx.app.debug("GameScreen.resize","New screen size"+GameCore.viewport.getScreenWidth()+","+ GameCore.viewport.getScreenHeight());
    }

}
