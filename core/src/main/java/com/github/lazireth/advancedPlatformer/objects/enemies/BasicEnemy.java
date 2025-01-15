package com.github.lazireth.advancedPlatformer.objects.enemies;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.ArrayList;

public class BasicEnemy extends Enemy {
    //BasicEnemies move until they hit a wall then turn around
    //and can only deal damage on contact
    TiledMapTileMapObject myMapObject;
    ArrayList<TiledMapTile> myTiles;
    String enemyType;
    int state;

    float health;
    float damage;
    float moveSpeed;
    public BasicEnemy (TiledMapTileMapObject enemy) {
        myMapObject =enemy;

        enemyType= myMapObject.getTile().getProperties().get("EnemyType",String.class);
        state= myMapObject.getTile().getProperties().get("State",int.class);

        myTiles = getTilesFor("BasicEnemy");
        loadStats();
    }
    private void loadStats(){

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
    public void update(float delta) {

    }

    @Override
    public void startInteractionWithPlayer(Player player) {

    }
}
