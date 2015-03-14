package com.testapps.wildWistEast.gameStates.selectPositionState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.testapps.wildWistEast.BattleFieldController;
import com.testapps.wildWistEast.GameBoard;
import com.testapps.wildWistEast.buttons.GameButtons;
import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameStates.ActionMessage;
import com.testapps.wildWistEast.gameStates.IGameStates;
import com.testapps.wildWistEast.turn.TurnAction;

import java.util.HashMap;

public class SelectPositionState implements IGameStates {
    GameButtons gameButtons;
    BattleFieldController battleFieldController;
    HashMap<Integer, SelectorButtonMovePlayer> selectorsButtons;
    Array<SelectorButtonMovePlayer> nextPossibleMovements;
    CowboysBand band;

    public SelectPositionState(BattleFieldController battleFieldController, GameButtons gameButtons, CowboysBand band) {
        this.battleFieldController = battleFieldController;
        this.gameButtons = gameButtons;
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
            this.gameButtons.subscribeButton(bttn);
        }
    }

    @Override
    public void init() {
        //this.gameButtons.hideMenuButtons();
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
        Integer myPlayerPos = GameBoard.getBoardPos(this.band.getMyCowboy());
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
        battleFieldController.buttonPressed(new ActionMessage(TurnAction.Action.MOVE, selector.getBoardPos()));
        for(SelectorButtonMovePlayer bttn : nextPossibleMovements)
        {
            bttn.disable();
        }
    }
}