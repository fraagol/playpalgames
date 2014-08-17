package com.testapps.testLibGDX.gameStates;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.testLibGDX.buttons.GameButtons;

/*
Main Game State. Shows the basic buttons and functionality.
 */
public class MainState implements IGameStates{

    private final GameButtons gameButtons;

    public MainState(GameButtons gameButtons) {
        this.gameButtons = gameButtons;
    }

    @Override
    public void init() {
        gameButtons.showMenuButtons();
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void dispose() {

    }
}
