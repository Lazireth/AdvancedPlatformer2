package com.github.lazireth.AdvancedPlatformer2.objects.enemies;

import com.github.lazireth.AdvancedPlatformer2.Player;
import com.github.lazireth.AdvancedPlatformer2.objects.InteractableObject;

public abstract class Enemy extends InteractableObject {
    public abstract void death();
    public abstract void takeDamage(int damageTaken);
    public abstract void damagePlayer(Player player);
}
