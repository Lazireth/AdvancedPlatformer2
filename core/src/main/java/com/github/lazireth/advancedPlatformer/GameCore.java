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
    public static TextureMapObjectRenderer renderer;
    public static final float WIDTH=30;
    public static final float HEIGHT=15;
    public static OrthographicCamera camera;
    public static final float metersPerPixel = 1/64f;
    public static final float pixelsPerMeter = 64f;
    public static Vector3 cameraPos;

    public static FitViewport viewport;


    public static final InputHandler inputHandler=new InputHandler();

    public static GameScreen gameScreen;
    public static DeathScreen deathScreen;
    public static StartScreen startScreen;
    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_NONE);

        camera=new OrthographicCamera();
        camera.setToOrtho(false,WIDTH,HEIGHT);
        viewport=new FitViewport(WIDTH,HEIGHT,camera);


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
