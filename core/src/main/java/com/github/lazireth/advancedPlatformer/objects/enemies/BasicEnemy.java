package com.github.lazireth.advancedPlatformer.objects.enemies;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.github.lazireth.advancedPlatformer.Level;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.Iterator;
import java.util.Map;

public class BasicEnemy extends Enemy {
    TiledMapTileMapObject basicEnemy;
    String enemyType;
    int state;
    public BasicEnemy (TiledMapTileMapObject enemy, Level level) {
        basicEnemy =enemy;

        enemyType= basicEnemy.getTile().getProperties().get("EnemyType",String.class);
        state= basicEnemy.getTile().getProperties().get("State",int.class);

        getTilesFor("BasicEnemy");
    }
    private void loadEnemyData(){

    }
    @Override
    public void death() {

    }

    @Override
    public void takeDamage() {

    }

    @Override
    public void damagePlayer() {

    }

    @Override
    public void render(TextureMapObjectRenderer renderer) {

    }

    @Override
    public void update() {

    }

    @Override
    public void startInteraction(Player player) {

    }
}
