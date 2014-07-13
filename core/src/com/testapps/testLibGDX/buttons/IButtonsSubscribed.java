package com.testapps.testLibGDX.buttons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IButtonsSubscribed {

    public void screenTouched(int screenX, int screenY);

    public void dispose();

    public void render(SpriteBatch batch);

    public void enable();

    public void disable();
}
