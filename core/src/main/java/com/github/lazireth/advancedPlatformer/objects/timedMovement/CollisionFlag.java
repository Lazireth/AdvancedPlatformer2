package com.github.lazireth.advancedPlatformer.objects.timedMovement;

public enum CollisionFlag{
    ON,     //turns collision on
    OFF,    //turns collision on
    TOGGLE, //if collision off turn on, if on turn off
    RESET,  //set collision to initial state
    NONE    //don't change collision
}
