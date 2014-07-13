package com.testapps.testLibGDX.gameStates.selectPositionState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.testapps.testLibGDX.BattleFieldController;
import com.testapps.testLibGDX.buttons.ButtonController;
import com.testapps.testLibGDX.characters.cowboy.CowboysBand;
import com.testapps.testLibGDX.gameStates.IGameStates;

import java.util.HashMap;

public class SelectPositionState implements IGameStates {
    ButtonController buttonController;
    BattleFieldController battleFieldController;
    HashMap<Integer, SelectorButtonMovePlayer> selectorsButtons;
    Array<SelectorButtonMovePlayer> nextPossibleMovements;
    CowboysBand band;

    public SelectPositionState(BattleFieldController battleFieldController, ButtonController buttonController, CowboysBand band) {
        this.battleFieldController = battleFieldController;
        this.buttonController = buttonController;
        this.band = band;
        createSelectorButtons();
    }

    private void createSelectorButtons() {
        Texture selector = new Texture(Gdx.files.internal("moveToSelector.png"));

        selectorsButtons = new HashMap<Integer, SelectorButtonMovePlayer>();
        for(Integer i = 1; i <= 6; i++)
        {
            SelectorButtonMovePlayer bttn = new SelectorButtonMovePlayer(selector, i, this);
            selectorsButtons.put(i, bttn);
            this.buttonController.subscribeButton(bttn);
        }
    }

    @Override
    public void init() {
        this.buttonController.hideMenuButtons();
        this.nextPossibleMovements = calculateNextMovements();
        for(SelectorButtonMovePlayer bttn : nextPossibleMovements)
        {
            bttn.enable();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        for(SelectorButtonMovePlayer bttn : nextPossibleMovements)
        {
            bttn.render(batch);
        }

    }

    @Override
    public void dispose() {
        for(SelectorButtonMovePlayer bttn : this.selectorsButtons.values())
        {
            bttn.dispose();
        }
        nextPossibleMovements = null;
    }

    private Array<SelectorButtonMovePlayer> calculateNextMovements(){
        Array<SelectorButtonMovePlayer> availablePositions = new Array<SelectorButtonMovePlayer>();
        Integer myPlayerPos = this.band.getMyCowboy().getBoardPos();
        if(myPlayerPos == 1)
        {
            availablePositions.add(this.selectorsButtons.get(2));
            availablePositions.add(this.selectorsButtons.get(6));
        }
        else if(myPlayerPos == 6)
        {
            availablePositions.add(this.selectorsButtons.get(1));
            availablePositions.add(this.selectorsButtons.get(5));
        }
        else
        {
            availablePositions.add(this.selectorsButtons.get(myPlayerPos - 1));
            availablePositions.add(this.selectorsButtons.get(myPlayerPos + 1));
        }

        return availablePositions;
    }

    public void selectorPushed (SelectorButtonMovePlayer selector)
    {
        this.band.getMyCowboy().moveTo(selector.getBoardPos());
        battleFieldController.buttonPressed(selector);
        for(SelectorButtonMovePlayer bttn : nextPossibleMovements)
        {
            bttn.disable();
        }
    }
}