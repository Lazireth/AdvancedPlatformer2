package com.github.lazireth.advancedPlatformer.objects;

public abstract class Enemy extends InteractableObject{
    int contactDamage;
    int health;//if 0 is dead if -1 is immortal
    Enemy(int health,int contactDamage){
        this.contactDamage=contactDamage;
        this.health=health;
    }
    public abstract void killEnemy();
    public abstract void damagePlayer();
}
