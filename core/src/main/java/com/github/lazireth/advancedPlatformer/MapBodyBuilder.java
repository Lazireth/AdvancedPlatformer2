package com.github.lazireth.advancedPlatformer;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.lazireth.advancedPlatformer.objects.FilterCategory;
import com.github.lazireth.advancedPlatformer.objects.Wall;

import java.util.ArrayList;

public class MapBodyBuilder {

    // The pixels per tile. If your tiles are 16x16, this is set to 16f
    static float pixelsPerUnit = GameCore.pixelsPerMeter;

    public static ArrayList<Body> buildShapes(TiledMap map, World world, String layer) {
        MapObjects objects = map.getLayers().get(layer).getObjects();
        ArrayList<Body> bodies = new ArrayList<>();

        for(MapObject object : objects) {
            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;

            switch (object) {
                case RectangleMapObject rectangleMapObject -> shape = getRectangle(rectangleMapObject);
                case PolygonMapObject polygonMapObject -> shape = getPolygon(polygonMapObject);
                case PolylineMapObject polylineMapObject -> shape = getPolyline(polylineMapObject);
                case CircleMapObject circleMapObject -> shape = getCircle(circleMapObject);
                case null, default -> {
                    continue;
                }
            }

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bd);

            FixtureDef fixtureDefRect=new FixtureDef();
            fixtureDefRect.shape=shape;
            FilterCategory.WALL.makeFilter(fixtureDefRect.filter);

            body.createFixture(fixtureDefRect).setUserData(new Wall());

            bodies.add(body);
            shape.dispose();
        }
        return bodies;
    }

    private static PolygonShape getTiledMapTileMapObject(TiledMapTileMapObject tileMapObject) {
        Rectangle rectangle = new Rectangle(tileMapObject.getX(), tileMapObject.getY(),tileMapObject.getTile().getTextureRegion().getRegionWidth(),tileMapObject.getTile().getTextureRegion().getRegionHeight());
        //Rectangle rectangle=new Rectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / pixelsPerUnit,
            (rectangle.y + rectangle.height * 0.5f ) / pixelsPerUnit);
        polygon.setAsBox(rectangle.width * 0.5f / pixelsPerUnit,
            rectangle.height * 0.5f / pixelsPerUnit,
            size,
            0.0f);
        return polygon;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / pixelsPerUnit,
            (rectangle.y + rectangle.height * 0.5f ) / pixelsPerUnit);
        polygon.setAsBox(rectangle.width * 0.5f / pixelsPerUnit,
            rectangle.height * 0.5f / pixelsPerUnit,
            size,
            0.0f);
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / pixelsPerUnit);
        circleShape.setPosition(new Vector2(circle.x / pixelsPerUnit, circle.y / pixelsPerUnit));
        return circleShape;
    }
    /// don't use is kinda broken
    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {

        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / pixelsPerUnit;
            worldVertices[i].y = vertices[i * 2 + 1] / pixelsPerUnit;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}
