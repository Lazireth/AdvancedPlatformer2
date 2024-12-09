package com.github.lazireth.advancedPlatformer.objects;

import com.github.lazireth.advancedPlatformer.Player;
import com.github.lazireth.advancedPlatformer.render.TextureMapObjectRenderer;

public abstract class InteractableObject{
    public abstract void render(TextureMapObjectRenderer renderer);
    public abstract void update();
    public abstract void startInteraction(Player player);
}
