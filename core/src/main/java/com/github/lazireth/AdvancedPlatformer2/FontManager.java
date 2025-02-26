package com.github.lazireth.AdvancedPlatformer2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import java.util.HashMap;
import java.util.Map;

public class FontManager {
    static Map<String, BitmapFont> fontMap=new HashMap<>();
    static public BitmapFont getFont(String fontName, int fontSize){
        if(fontSize<1){
            throw new RuntimeException("fontSize of "+fontSize+" is less than 1");
        }
        if(fontMap.containsKey(fontName+fontSize)){
            return fontMap.get(fontName+fontSize);
        }
        FreeTypeFontGenerator generator=new FreeTypeFontGenerator(Gdx.files.internal("fonts/"+fontName+".ttf"));
        FreeTypeFontParameter parameter=new FreeTypeFontParameter();
        parameter.size=fontSize;
        BitmapFont font=generator.generateFont(parameter);
        generator.dispose();
        fontMap.put(fontName+fontSize,font);
        return font;
    }
}
