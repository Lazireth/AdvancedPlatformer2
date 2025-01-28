package com.github.lazireth.advancedPlatformer.objects;


import com.badlogic.gdx.physics.box2d.Filter;

public enum FilterCategory {
    /**
     * for reference <a href="https://www.iforce2d.net/b2dtut/collision-filtering">...</a>
     */
    SENSOR  (E.SENSOR.val,-1),
    WALL    (E.WALL.val,  -1),//Will collide with all
    PLAYER  (E.PLAYER.val,-1),
    ITEM    (E.ITEM.val,  E.SENSOR.val | E.WALL.val | E.PLAYER.val),
    ENEMY   (E.ENEMY.val, E.SENSOR.val | E.WALL.val | E.PLAYER.val | E.ENEMY.val);
    public final short categoryBits;//What the fixture is
    public final short maskBits;    //What the fixture collides With

    FilterCategory(int categoryBits, int maskBits){
        this.categoryBits=(short)categoryBits;
        this.maskBits=(short)maskBits;
    }
    public void makeFilter(Filter filter){
        filter.categoryBits=categoryBits;
        filter.maskBits=maskBits;
    }
    public void makeSensorFilter(Filter filter, FilterCategory checkFor){
        filter.categoryBits=categoryBits;
        filter.maskBits=checkFor.categoryBits;
    }
    public void makeSensorFilter(Filter filter, short checkFor){
        filter.categoryBits=categoryBits;
        filter.maskBits=checkFor;
    }
    protected enum E{
        SENSOR  (0x0001),
        WALL    (0x0002),
        PLAYER  (0x0004),
        ITEM    (0x0008),
        ENEMY   (0x0010);
        public final short val;//What the fixture is

        E(int categoryBits){
            this.val=(short)categoryBits;
        }
    }
}
