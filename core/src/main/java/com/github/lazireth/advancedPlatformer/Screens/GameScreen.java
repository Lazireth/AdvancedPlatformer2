package com.github.lazireth.advancedPlatformer.Screens;

import com.badlogic.gdx.ScreenAdapter;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.objects.Level;


public class GameScreen extends ScreenAdapter {
    static public GameCore gameCore;

    public static Level[] levels=new Level[Level.levelNames.length];//Must be changed when more levels are added

    public static int currentLevel=0;
    public static boolean doLevelTransition=false;

    private static long lastTimeUpdate;
    public GameScreen(){
        for(int i=0;i<levels.length;i++){
            levels[i]=new Level(i);
        }
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
