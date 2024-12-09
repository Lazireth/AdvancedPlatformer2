package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.lazireth.advancedPlatformer.Screens.DeathScreen;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameCore extends Game {
    public static float WIDTH=30;
    public static float HEIGHT=14;
    public static OrthographicCamera camera;
    public static float unitsPerPixel = 1/64f;
    public static float pixelsPerUnit = 64f;

    public static FitViewport viewport;



    public static InputHandler inputHandler;
    public static GameScreen gameScreen;
    public static DeathScreen deathScreen;
    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_NONE);
        System.out.println("\nMove camera with W A S D");
        System.out.println("Move player with arrow keys");
        System.out.println("The window in resizeable\n");

        System.out.println("There is no reset");

        camera=new OrthographicCamera();
        camera.setToOrtho(false,WIDTH,HEIGHT);
        viewport=new FitViewport(WIDTH,HEIGHT,camera);



        inputHandler=new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        gameScreen=new GameScreen(this);
        deathScreen=new DeathScreen();
        loadDeathScreen();
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
    public void loadGameScreen(){
        this.setScreen(gameScreen);
    }
    public void loadDeathScreen(){
        this.setScreen(deathScreen);
    }
}
