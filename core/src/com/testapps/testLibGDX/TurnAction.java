package com.testapps.testLibGDX;

import com.playpalgames.library.GameTurn;

/**
 * Created by javi on 06/08/2014.
 */
public class TurnAction implements GameTurn {

    public TurnAction(){}

    public TurnAction(Action action, int player, int target) {
        this.action = action;
        this.player = player;
        this.target = target;
    }

    @Override
    public String dataToString() {
        return null;
    }

    @Override
    public void populateFromString(String data) {

    }

    public enum Action {MOVE, SHOOT, RELOAD}
    Action action;
    //player id
    private  int player=-1;
    //Shoot/move target
    private  int target=-1;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
