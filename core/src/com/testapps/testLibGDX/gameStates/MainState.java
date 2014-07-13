package com.testapps.testLibGDX.gameStates;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.testLibGDX.buttons.ButtonController;

/*
Main Game State. Shows the basic buttons and functionality.
 */
public class MainState implements IGameStates{

    private final ButtonController buttonController;

    public MainState(ButtonController buttonController) {
        this.buttonController = buttonController;
    }

    @Override
    public void init() {
        buttonController.showMenuButtons();
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void dispose() {

    }
}
