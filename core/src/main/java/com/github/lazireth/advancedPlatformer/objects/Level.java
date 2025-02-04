package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.github.lazireth.advancedPlatformer.Area;
import com.github.lazireth.advancedPlatformer.GameCore;
import com.github.lazireth.advancedPlatformer.Screens.GameScreen;

import java.util.ArrayList;

public class Level {
    public static String[] levelNames={"1-1","1-2"};
    public static String[][] levelAreas={{"1-1 0","1-1 1"},{"1-2 0"}};//levelAreas[level][area]
    public final int levelNumber;

    public ArrayList<Area> areas=new ArrayList<>();
    public int currentArea;
    public boolean doAreaTransition;
    public Level(int levelNumber){
        if(levelNumber==0){
            InteractableObject.loadTiles(new TmxMapLoader().load("Map/"+levelAreas[0][0]+".tmx"));
        }

        this.levelNumber=levelNumber;
        for(int area=0;area<levelAreas[levelNumber].length;area++){
            TiledMap tiledMap=new TmxMapLoader().load("Map/"+levelAreas[levelNumber][area]+".tmx");
            areas.add(new Area(tiledMap,this));
            GameCore.renderer=areas.getLast().renderer;
        }
        System.out.println("Level "+levelNames[levelNumber]+" loaded \t Has "+areas.size()+" areas");
    }
    public void loadSprites(){

    }

    public void show(){
        areas.get(levelNumber).show();
    }
    public void render(float delta){
        System.out.println("Level render start "+GameScreen.timeSinceLastCheck());
        if(doAreaTransition){
            currentArea++;
            areas.get(currentArea).show();
        }
        areas.get(currentArea).render(delta);
    }
    public Area getCurrentArea(){
        return areas.get(currentArea);
    }
    public String toString(){
        return levelNames[levelNumber];
    }
}
