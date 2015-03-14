package com.testapps.wildWistEast.gameStates.selectReloadState;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.wildWistEast.BattleFieldController;
import com.testapps.wildWistEast.GameBoard;
import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Bullets;
import com.testapps.wildWistEast.gameStates.ActionMessage;
import com.testapps.wildWistEast.gameStates.IGameStates;
import com.testapps.wildWistEast.turn.TurnAction;

public class SelectReloadState implements IGameStates {
    private final CowboysBand band;
    private final Bullets bullets;
    BattleFieldController battleFieldController;

    public SelectReloadState(BattleFieldController battleFieldController, CowboysBand cowboysBand, Bullets bullets) {
        this.battleFieldController = battleFieldController;
        this.band = cowboysBand;
        this.bullets = bullets;
    }

    @Override
    public void init() {
        battleFieldController.buttonPressed(new ActionMessage(TurnAction.Action.RELOAD, GameBoard.getBoardPos(band.getMyCowboy())));
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void dispose() {

    }
}
