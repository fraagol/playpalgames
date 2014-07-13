package com.testapps.testLibGDX.gameStates;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IGameStates {

    public void init();

    public void render(SpriteBatch batch);

    public void dispose();
}
