package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.lazireth.advancedPlatformer.Screens.*;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameCore extends Game {
    public static final boolean testing=false;

    public static final float WIDTH=32;
    public static final float HEIGHT=20;
    public static final float metersPerPixel = 1/64f;
    public static final float pixelsPerMeter = 64f;

    public static TextureMapObjectRenderer renderer;
    public static OrthographicCamera camera;
    public static Vector3 cameraPos;
    public static FitViewport viewport;


    public static final InputHandler inputHandler=new InputHandler();

    private static GameScreen gameScreen;
    private static GameStartScreen gameStartScreen;
    private static LevelStartScreen levelStartScreen;
    private static GameOverScreen gameOverScreen;
    private static VictoryScreen victoryScreen;
    @Override
    public void create() {restartGame();}
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        renderer.setView(GameCore.camera);
        Gdx.app.debug("GameCore.resize","New screen size"+viewport.getScreenWidth()+","+viewport.getScreenHeight());
    }
    public void restartGame(){
        Gdx.app.setLogLevel(Application.LOG_NONE);
        Gdx.input.setInputProcessor(inputHandler);

        camera=new OrthographicCamera();
        camera.setToOrtho(false,WIDTH,HEIGHT);
        viewport=new FitViewport(WIDTH,HEIGHT,camera);

        renderer=new TextureMapObjectRenderer(new TmxMapLoader().load("Map/1-1 0.tmx"),GameCore.metersPerPixel);
        gameScreen=new GameScreen();GameScreen.gameCore=this;Player.game=this;
        cameraPos=new Vector3(5,camera.position.y,camera.position.z);
        renderer.getBatch().setProjectionMatrix(viewport.getCamera().combined);

        gameStartScreen =new GameStartScreen(this);
        levelStartScreen=new LevelStartScreen(this);
        gameOverScreen=new GameOverScreen(this);
        victoryScreen=new VictoryScreen(this);

        loadGameStartScreen();
    }
    public void render(){super.render();}
    public void dispose(){}
    public void loadGameScreen(){setScreen(gameScreen);}
    public void loadGameStartScreen(){setScreen(gameStartScreen);}
    public void loadLevelStartScreen(){setScreen(levelStartScreen);}
    public void loadGameOverScreen(){setScreen(gameOverScreen);}
    public void loadVictoryScreen(){setScreen(victoryScreen);}
}
