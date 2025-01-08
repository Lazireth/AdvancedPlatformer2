package com.github.lazireth.advancedPlatformer.objects.enemies;

import com.github.lazireth.advancedPlatformer.objects.InteractableObject;

public abstract class Enemy extends InteractableObject {
    public abstract void death();
    public abstract void takeDamage();
    public abstract void damagePlayer();
}
