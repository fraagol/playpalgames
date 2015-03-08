package com.testapps.wildWistEast;

import com.playpalgames.library.GameController;
import com.testapps.wildWistEast.turn.TurnAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageController {
    private GameController gameController;
    private TurnAction myTurn;
    private TurnAction hisTurn;

    public MessageController(GameController gameController) {
        this.gameController = gameController;
    }

    public void iDoAction(TurnAction turnAction) {
        this.myTurn = turnAction;
        try {
            gameController.sendTurn(myTurn, null, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean everybodyReady() {
        return myTurn != null && hisTurn != null;
    }

    public TurnAction getMyTurn() {
        return this.myTurn;
    }

    public TurnAction getEnemyTurn() {
        return hisTurn;
    }

    public void setEnemyTurn(TurnAction turn) {
        this.hisTurn = turn;
    }

    public void reset() {
        myTurn = null;
        hisTurn = null;
    }
}