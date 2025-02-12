package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.ScreenAdapter;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.objects.Level;


public class GameScreen extends ScreenAdapter {
    static public GameCore gameCore;

    public static Level[] levels=new Level[2];//Must be changed when more levels are added

    public static int currentLevel=0;
    public static boolean doLevelTransition=false;

    private static long lastTimeUpdate;
    public static long timeSinceLastCheck(){
        long hold=(System.nanoTime()-lastTimeUpdate);
        lastTimeUpdate=System.nanoTime();
        return hold;
    }
    public GameScreen(){
        levels[0]=new Level(0);
        levels[1]=new Level(1);
    }
    public Level getCurrentLevel(){
        return levels[currentLevel];
    }
    @Override
    public void render(float delta) {
        if(doLevelTransition){
            System.out.println("doLevelTransition "+currentLevel);
            currentLevel++;
            if((currentLevel==levels.length)){
                //if the new current level is out of range
                gameCore.loadVictoryScreen();
                return;
            }
            if(levels[currentLevel]==null){
                levels[currentLevel]=new Level(currentLevel);
            }
            levels[currentLevel].show();
            doLevelTransition=false;
        }
        levels[currentLevel].render(delta);
    }
    @Override
    public void resize(int width, int height) {
        gameCore.resize(width,height);
    }

    @Override
    public void show() {
        levels[currentLevel].show();
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {

    }
}
