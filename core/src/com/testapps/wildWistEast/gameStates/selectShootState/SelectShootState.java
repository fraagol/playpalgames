package com.testapps.wildWistEast.gameStates.selectShootState;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.testapps.wildWistEast.GameBoard;
import com.testapps.wildWistEast.buttons.GameButtons;
import com.testapps.wildWistEast.characters.cowboy.Cowboy;
import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Bullets;
import com.testapps.wildWistEast.gameStates.IGameStates;

import java.util.HashMap;

public class SelectShootState implements IGameStates{
    GameButtons gameButtons;
    HashMap<Integer, SelectorButtonShoot> selectorsButtons;
    Array<SelectorButtonShoot> nextPossibleShoots;
    CowboysBand band;
    Bullets bullets;

    public SelectShootState(GameButtons gameButtons, CowboysBand band, Bullets bullets) {
        this.gameButtons = gameButtons;
        this.band = band;
        this.bullets = bullets;
        createSelectorButtons();
    }

    private void createSelectorButtons() {
        Texture selector = new Texture(Gdx.files.internal("bullseye.jpg"));

        selectorsButtons = new HashMap<Integer, SelectorButtonShoot>();
        for(Integer gameBoardPosition = 1; gameBoardPosition <= GameBoard.numPositions(); gameBoardPosition++)
        {
            SelectorButtonShoot bttn = new SelectorButtonShoot(selector, gameBoardPosition, this);
            selectorsButtons.put(gameBoardPosition, bttn);
            this.gameButtons.subscribeButton(bttn);
        }
    }

    private Boolean itsMyPosition(int gameBoardPosition) {
        Cowboy cowboy = GameBoard.getCowboyAt(gameBoardPosition);
        if(cowboy == null) {
            return Boolean.FALSE;
        }
        return (this.band.getMyCowboy() == cowboy);
    }

    @Override
    public void init() {
        //this.gameButtons.hideMenuButtons();
        this.nextPossibleShoots = calculateNextShootingPositions();
        for(SelectorButtonShoot bttn : nextPossibleShoots)
        {
            bttn.enable();
        }
    }

    private Array<SelectorButtonShoot> calculateNextShootingPositions() {
        Array<SelectorButtonShoot> availablePositions = new Array<SelectorButtonShoot>();
        Array<Cowboy> enemies = this.band.getEnemies();
        for (int gameBoardPositions = 1; gameBoardPositions <= GameBoard.numPositions(); gameBoardPositions++) {
            if(itsMyPosition(gameBoardPositions)) {
                continue;
            }
            availablePositions.add(this.selectorsButtons.get(gameBoardPositions));
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
        if(this.band.getMyCowboy().canShoot()) {
            this.band.getMyCowboy().shootTo(selectorButtonShoot.getBoardPos());
            this.bullets.shoot();
            Cowboy objective = GameBoard.getCowboyAt(selectorButtonShoot.getBoardPos());
            if(objective != null) {
                objective.shooted();
            }

            //battleFieldController.buttonPressed(selector);
            for (SelectorButtonShoot bttn : nextPossibleShoots) {
                bttn.disable();
            }
        }
    }
}
