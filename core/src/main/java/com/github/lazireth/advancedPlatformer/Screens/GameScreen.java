package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.lazireth.advancedPlatformer.CollisionListener;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Level;
import com.github.lazireth.advancedPlatformer.Player;


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


        player=new Player(level.playerStartingPos,level.playerTextureTiles);
    }


    @Override
    public void render(float delta) {
        input(delta);
        //Gdx.app.log("GameScreen.render","Player velocity: "+player.getVelocity());
        Vector2 playerPositionInitial=player.getPosition();
        doPhysicsStep(delta);
        updateCamera(playerPositionInitial);

        //update game here

        level.update();
        player.update(delta);


        ScreenUtils.clear(Color.BLACK);
        GameCore.camera.update();

        level.render();
        if(player.checkIfFallingOffMap()){
            player.manageFallingOffMap();
            game.loadDeathScreen();
        }
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
        player.input(delta);
    }
    private void updateCamera(Vector2 playerPositionInitial){
        Vector2 playerChange=player.getPosition().sub(playerPositionInitial);
        Vector3 playerChangeCleaned=new Vector3(playerChange.x,0,0);
        GameCore.cameraPos.add(playerChangeCleaned);
        GameCore.camera.position.set(GameCore.cameraPos);
        GameCore.camera.update();
        GameCore.renderer.setView(GameCore.camera);
    }
    @Override
    public void resize(int width, int height) {
        game.resize(width,height);
    }

    @Override
    public void show() {
        // is now handled by GameCore
//        GameCore.viewport.apply();
//        GameCore.camera.setToOrtho(false,GameCore.WIDTH,GameCore.HEIGHT);
//        level.updateRenderer();
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
