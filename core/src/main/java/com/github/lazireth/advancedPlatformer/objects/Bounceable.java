package com.github.lazireth.advancedPlatformer.objects;


public abstract class Bounceable extends InteractableObject{
    // todo
    // does not get called when it should
    public void bounce(){
        System.out.println("bounce");

        if(body.getLinearVelocity().x<0){
            body.setLinearVelocity(2,0);
        }else{
            body.setLinearVelocity(-2,0);
        }
    }
}
