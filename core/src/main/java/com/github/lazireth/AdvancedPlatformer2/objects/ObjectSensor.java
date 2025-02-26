package com.github.lazireth.AdvancedPlatformer2.objects;

public class ObjectSensor {
    public String sensorName;
    public Object relatedObject;
    public ObjectSensor(String sensorName, Object relatedObject){
        this.sensorName=sensorName;
        this.relatedObject=relatedObject;
    }
}
