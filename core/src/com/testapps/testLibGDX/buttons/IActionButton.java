package com.testapps.testLibGDX.buttons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public interface IActionButton {

    void render(SpriteBatch batch);

    Vector3 getPos();

    //Return: pressed
    Boolean touchEvent(int screenX, int screenY);

    void show();

    void hide();

    public void dispose();
}
