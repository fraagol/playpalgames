package com.testapps.wildWistEast;

import com.playpalgames.library.GameTurn;

/**
 * Created by javi on 06/08/2014.
 */
public class TurnAction implements GameTurn {

    public TurnAction() {
    }

    public TurnAction(Action action, int player, int target) {
        this.action = action;
        this.player = player;
        this.target = target;
    }

    @Override
    /**
     * Creates an string representing the Turn to be sent
     */

    public String dataToString() {
        return action.name() + "|" + player + "|" + target;
    }

    /**
     * Reads the state received and populates the Turn
     *
     * @param data
     */
    @Override
    public void populateFromString(String data) {
        String[] fields = data.split("\\|");
        action = Action.valueOf(fields[0]);
        player = Integer.parseInt(fields[1]);
        target = Integer.parseInt(fields[2]);
    }

    public enum Action {MOVE, SHOOT, RELOAD}

    Action action;
    //player id
    private int player = -1;
    //Shoot/move target
    private int target = -1;

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
