package com.github.lazireth.AdvancedPlatformer2.objects.timedMovement;

import com.badlogic.gdx.math.Vector2;

public class MovementStep{
    Vector2 velocity;
    float startTime;// unit of seconds
    CollisionFlag flag;
    public MovementStep(Vector2 velocity, float startTime, CollisionFlag flag){
        this.velocity=velocity;
        this.startTime=startTime;
        this.flag=flag;
    }
    public MovementStep(float x, float y, float startTime, CollisionFlag flag){
        this.velocity=new Vector2(x,y);
        this.startTime=startTime;
        this.flag=flag;
    }
}

