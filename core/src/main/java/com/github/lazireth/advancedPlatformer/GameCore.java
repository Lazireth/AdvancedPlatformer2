package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.lazireth.advancedPlatformer.Screens.DeathScreen;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;
import com.github.lazireth.advancedPlatformer.Screens.StartScreen;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameCore extends Game {
    public static boolean isDebuggingEnabled=true;

    public static TextureMapObjectRenderer renderer;
    public static float WIDTH=30;
    public static float HEIGHT=14;
    public static OrthographicCamera camera;
    public static float unitsPerPixel = 1/64f;
    public static float pixelsPerUnit = 64f;
    public static Vector3 cameraPos;

    public static FitViewport viewport;


    public static InputHandler inputHandler;

    public static GameScreen gameScreen;
    public static DeathScreen deathScreen;
    public static StartScreen startScreen;
    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_NONE);

        camera=new OrthographicCamera();
        camera.setToOrtho(false,WIDTH,HEIGHT);
        viewport=new FitViewport(WIDTH,HEIGHT,camera);


        inputHandler=new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        gameScreen=new GameScreen(this);
        cameraPos=new Vector3(GameScreen.player.getXPosition(),camera.position.y,camera.position.z);
        renderer.getBatch().setProjectionMatrix(viewport.getCamera().combined);

        deathScreen=new DeathScreen(this);
        startScreen=new StartScreen(this);

        loadStartScreen();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        renderer.setView(GameCore.camera);
        Gdx.app.debug("GameCore.resize","New screen size"+viewport.getScreenWidth()+","+viewport.getScreenHeight());
    }

    @Override
    public void render() {
        super.render();
        // Draw your application here.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
    public void loadStartScreen(){
        this.setScreen(startScreen);
    }
    public void loadGameScreen(){
        this.setScreen(gameScreen);
    }
    public void loadDeathScreen(){
        this.setScreen(deathScreen);
    }
}
