package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.github.lazireth.advancedPlatformer.Area;
import com.github.lazireth.advancedPlatformer.GameCore;

import java.util.ArrayList;

public class Level {
    public static String[] levelNames={"1-1","1-2"};
    public static String[][] levelAreas={{"1-1 0","1-1 1"},{"1-2 0","1-2 1","1-2 2","1-2 3"}};//levelAreas[level][area]
    public final int levelNumber;

    public ArrayList<Area> areas=new ArrayList<>();
    public int currentArea;
    public boolean doAreaTransition;

    public int targetLoadArea=-1;
    public int targetLoadPipeID=-1;
    public boolean targetLoadResetArea=false;
    public Level(int levelNumber){
        if(levelNumber==0){
            InteractableObject.loadTiles(new TmxMapLoader().load("Map/"+levelAreas[0][0]+".tmx"));
        }

        this.levelNumber=levelNumber;
        for(int areaNumber=0;areaNumber<levelAreas[levelNumber].length;areaNumber++){

            areas.add(new Area(levelAreas[levelNumber][areaNumber],this,areaNumber));
            GameCore.renderer=areas.getLast().renderer;
        }
        System.out.println("Level "+levelNames[levelNumber]+" loaded \t Has "+areas.size()+" areas");
    }
    public void loadArea(int targetLoadArea,int targetLoadPipeID,boolean targetLoadResetArea){
        System.out.println("loadArea");
        System.out.println(targetLoadArea);
        System.out.println(targetLoadPipeID);
        System.out.println(targetLoadResetArea);
        this.targetLoadArea=targetLoadArea;
        this.targetLoadPipeID=targetLoadPipeID;
        this.targetLoadResetArea=targetLoadResetArea;
        doAreaTransition=true;
    }
    public void reset(){
        areas=new ArrayList<>();
        currentArea=0;
        doAreaTransition=false;

        for(int areaNumber=0;areaNumber<levelAreas[levelNumber].length;areaNumber++){
            areas.add(new Area(levelAreas[levelNumber][areaNumber],this,areaNumber));
            GameCore.renderer=areas.getLast().renderer;
        }
        System.out.println("Level "+levelNames[levelNumber]+" loaded \t Has "+areas.size()+" areas");
    }
    public void show(){
        System.out.println("show");
        areas.get(currentArea).show();
    }
    public void hide(){
        areas.get(currentArea).hide();
    }
    public void render(float delta){
        if(doAreaTransition){
            doAreaTransition=false;
            areas.get(currentArea).hide();
            if(targetLoadArea==-1){
                currentArea++;
            }else{
                System.out.println("targetLoadArea "+targetLoadArea);
                if(targetLoadResetArea){
                    reset();
                }
                currentArea=targetLoadArea;
                targetLoadArea=-1;
                targetLoadResetArea=false;
            }
            if(targetLoadPipeID==-1){
                areas.get(currentArea).show();
            }else{
                System.out.println("diffload currentArea   "+currentArea);
                areas.get(currentArea).show(targetLoadPipeID);
                targetLoadPipeID=-1;
            }


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
