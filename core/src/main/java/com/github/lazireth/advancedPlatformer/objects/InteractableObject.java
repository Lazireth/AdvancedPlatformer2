package com.github.lazireth.advancedPlatformer.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

public abstract class InteractableObject{
    public Body body;
    public abstract void render(TextureMapObjectRenderer renderer);
    public abstract void update();
    public abstract void startInteraction(Player player);
}
