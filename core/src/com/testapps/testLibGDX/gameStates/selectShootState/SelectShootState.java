package com.testapps.testLibGDX.gameStates.selectShootState;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.testapps.testLibGDX.buttons.ButtonController;
import com.testapps.testLibGDX.characters.cowboy.Cowboy;
import com.testapps.testLibGDX.characters.cowboy.CowboysBand;
import com.testapps.testLibGDX.gameStates.IGameStates;

import java.util.HashMap;

public class SelectShootState implements IGameStates{
    ButtonController buttonController;
    HashMap<Integer, SelectorButtonShoot> selectorsButtons;
    Array<SelectorButtonShoot> nextPossibleShoots;
    CowboysBand band;

    public SelectShootState(ButtonController buttonController, CowboysBand band) {
        this.buttonController = buttonController;
        createSelectorButtons();
        this.band = band;
    }

    private void createSelectorButtons() {
        Texture selector = new Texture(Gdx.files.internal("bullseye.jpg"));

        selectorsButtons = new HashMap<Integer, SelectorButtonShoot>();
        for(Integer i = 1; i <= 6; i++)
        {
            SelectorButtonShoot bttn = new SelectorButtonShoot(selector, i, this);
            selectorsButtons.put(i, bttn);
            this.buttonController.subscribeButton(bttn);
        }
    }

    @Override
    public void init() {
        this.buttonController.hideMenuButtons();
        this.nextPossibleShoots = calculateNextShootingPositions();
        for(SelectorButtonShoot bttn : nextPossibleShoots)
        {
            bttn.enable();
        }
    }

    private Array<SelectorButtonShoot> calculateNextShootingPositions() {
        Array<SelectorButtonShoot> availablePositions = new Array<SelectorButtonShoot>();
        Array<Cowboy> enemies = this.band.getEnemies();
        for(Cowboy enemy : enemies)
        {
            availablePositions.add(this.selectorsButtons.get(enemy.getBoardPos()));
        }
        return availablePositions;
    }

    @Override
    public void render(SpriteBatch batch) {
        for(SelectorButtonShoot bttn : nextPossibleShoots)
        {
            bttn.render(batch);
        }
    }

    @Override
    public void dispose() {
        for(SelectorButtonShoot bttn : this.selectorsButtons.values())
        {
            bttn.dispose();
        }
        nextPossibleShoots = null;
    }


    public void selectorPushed(SelectorButtonShoot selectorButtonShoot) {
        this.band.getMyCowboy().shootTo(selectorButtonShoot.getBoardPos());

        //TODO: more than one enemy
        this.band.getEnemies().get(0).die();
        //battleFieldController.buttonPressed(selector);
        for(SelectorButtonShoot bttn : nextPossibleShoots)
        {
            bttn.disable();
        }
    }
}
