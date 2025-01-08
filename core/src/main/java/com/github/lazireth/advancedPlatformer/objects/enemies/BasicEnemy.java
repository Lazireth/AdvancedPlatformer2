package com.github.lazireth.advancedPlatformer.objects.enemies;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

import java.util.Iterator;
import java.util.Map;

public class BasicEnemy extends Enemy {
    TiledMapTileMapObject walkingEnemy;
    String enemyType;
    int state;
    public BasicEnemy  (TiledMapTileMapObject enemy, Map<String,TiledMapTile[]> npcTileset) {
        walkingEnemy=enemy;
        enemyType=walkingEnemy.getProperties().get("Enemy Type",String.class);
        state=walkingEnemy.getProperties().get("State",int.class);

        for (Iterator<Object> it = walkingEnemy.getProperties().getValues(); it.hasNext(); ) {
            System.out.println(it.next().toString());

        }
        npcTileset.get(enemyType);
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
