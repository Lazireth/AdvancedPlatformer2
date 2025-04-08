package com.github.lazireth.AdvancedPlatformer2;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.lazireth.AdvancedPlatformer2.Screens.*;
import com.github.lazireth.AdvancedPlatformer2.overworld.Overworld;
import com.github.lazireth.AdvancedPlatformer2.render.TextureMapObjectRenderer;

import static com.github.lazireth.AdvancedPlatformer2.GameCore.GlobalVariables.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameCore extends Game {

    public static class GlobalVariables {
        //debug flag
        public static final boolean debugging=true;
        //windows size
        public static final float GAME_WIDTH=16;
        public static final float GAME_HEIGHT=14;
        //unit scales
        public static final float metersPerPixel = 1/64f;
        public static final float pixelsPerMeter = 64f;

        public static final float TIME_STEP=1/144f;
        public static final int VELOCITY_ITERATIONS=4;
        public static final int POSITION_ITERATIONS=6;

        public static Overworld overworld;
        public static LevelMap levelMap;
        public static GameCore gameCore;
    }
    public static TextureMapObjectRenderer renderer;
    public static OrthographicCamera camera;
    public static FitViewport viewport;

    public static final InputHandler inputHandler=new InputHandler();

    public static LevelMap levelMap;

    private static GameStartScreen gameStartScreen;
    private static LevelStartScreen levelStartScreen;
    @Override
    public void create() {restartGame();}
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        renderer.setView(camera);
        Gdx.app.debug("GameCore.resize","New screen size"+viewport.getScreenWidth()+","+viewport.getScreenHeight());
    }
    public void restartGame(){
        gameCore=this;
        //InteractableObject.loadTiles(new TmxMapLoader().load("Map/1-1 0.tmx"));
        Gdx.app.setLogLevel(Application.LOG_NONE);
        Gdx.input.setInputProcessor(inputHandler);

        camera=new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        viewport=new FitViewport(GAME_WIDTH, GAME_HEIGHT,camera);

        renderer=new TextureMapObjectRenderer(new TmxMapLoader().load("Map/Worlds/Overworlds/World 1.tmx"),metersPerPixel);
        renderer.getBatch().setProjectionMatrix(viewport.getCamera().combined);

        gameStartScreen =new GameStartScreen(this);
        levelStartScreen=new LevelStartScreen(this);

        overworld=new Overworld(1);

        setScreenOverworld();
    }
    public void render(){super.render();}
    public void dispose(){}
    public void setScreenOverworld(){setScreen(overworld);}
    public void loadGameStartScreen(){setScreen(gameStartScreen);}
    public void loadLevelStartScreen(){setScreen(levelStartScreen);}
}
