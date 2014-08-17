package com.testapps.testLibGDX;

/**
 * Created by javi on 06/08/2014.
 */
public class TurnAction {


    enum Action {MOVE, SHOOT, RELOAD}
    Action action;
    //player id
    private  int player=-1;
    //Shoot target
    private  int target=-1;

    //Move coords.
    private int origPos=-1;
    private int destPos=-1;

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

    public int getOrigPos() {
        return origPos;
    }

    public void setOrigPos(int origPos) {
        this.origPos = origPos;
    }

    public int getDestPos() {
        return destPos;
    }

    public void setDestPos(int destPos) {
        this.destPos = destPos;
    }
}
