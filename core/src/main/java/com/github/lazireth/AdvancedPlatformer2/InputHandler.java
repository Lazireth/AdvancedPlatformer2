package com.github.lazireth.AdvancedPlatformer2;

import com.badlogic.gdx.InputAdapter;


public class InputHandler extends InputAdapter {
    public static boolean[] keys;
    public static boolean[] buttons;
    public InputHandler(){
        super();
        keys=new boolean[255];
        buttons=new boolean[5];
    }

    @Override
    public boolean keyDown(int keycode) {
        keys[keycode]=true;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys[keycode]=false;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        buttons[button]=false;
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        buttons[button]=true;
        return true;
    }
}
