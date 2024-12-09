package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.lazireth.advancedPlatformer.CollisionListener;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Level;
import com.github.lazireth.advancedPlatformer.Player;

import static com.github.lazireth.advancedPlatformer.InputHandler.keys;


public class GameScreen extends ScreenAdapter {
    private static final float TIME_STEP=1/60f;
    private static final int VELOCITY_ITERATIONS=4;
    private static final int POSITION_ITERATIONS=6;
    private static final int MAX_VELOCITY=5;
    private static final float MIN_VELOCITY=0.2f;
    private float accumulator=0;

    public Level level;
    public static Player player;
    GameCore game;



    public static World world;

    Box2DDebugRenderer debugRenderer;

    public GameScreen(final GameCore game){
        this.game=game;
        world=new World(new Vector2(0,-20),true);
        world.setContactListener(new CollisionListener());
        level=new Level(-2);
        debugRenderer=new Box2DDebugRenderer();


        player=new Player(new Vector2(10,5),level.playerTextureTiles);
    }


    @Override
    public void render(float delta) {
        input(delta);
        //Gdx.app.log("GameScreen.render","Player velocity: "+player.getVelocity());
        doPhysicsStep(delta);

        //update game here

        level.update();
        player.update(delta);


        ScreenUtils.clear(Color.BLACK);
        GameCore.camera.update();

        level.render();
        if(player.checkFallenOffMap()){
            game.loadDeathScreen();
        }
        //renders player inside level

        //debugRenderer.render(world,GameCore.camera.combined);
    }
    private void doPhysicsStep(float deltaTime){
        float frameTime=Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while(accumulator >= TIME_STEP){
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }
    private void input(float delta){
        float cameraMoveAmount=0.25f;
        //Camera movement start
        boolean updateCamera=false;
        float cameraX = GameCore.camera.position.x;
        float cameraY = GameCore.camera.position.y;
        if(keys[Keys.W]){
            cameraY+=cameraMoveAmount;
            updateCamera=true;
        }
        if(keys[Keys.S]){
            cameraY-=cameraMoveAmount;
            updateCamera=true;
        }
        if(keys[Keys.A]){
            cameraX-=cameraMoveAmount;
            updateCamera=true;
        }
        if(keys[Keys.D]){
            cameraX+=cameraMoveAmount;
            updateCamera=true;
        }
        if(updateCamera){
            GameCore.camera.position.set(cameraX,cameraY,0);
            GameCore.camera.update();
            level.updateRenderer();
        }
        //Camera movement end
        player.input(delta);
    }

    @Override
    public void resize(int width, int height) {
        GameCore.viewport.update(width, height, true);
        GameCore.camera.setToOrtho(false,GameCore.WIDTH,GameCore.HEIGHT);
        level.updateRenderer();
        Gdx.app.debug("GameScreen.resize","New screen size"+GameCore.viewport.getScreenWidth()+","+ GameCore.viewport.getScreenHeight());
    }

    @Override
    public void show() {
        GameCore.camera.setToOrtho(false,GameCore.WIDTH,GameCore.HEIGHT);
        level.updateRenderer();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        level.dispose();
        world.dispose();
    }
}
