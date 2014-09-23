package com.playpalgames.app.game;

import com.playpalgames.library.GameTurn;

/**
 * Created by javi on 20/08/2014.
 */
public class PenaltyTurn implements GameTurn {

    public static int LEFT=0;
    public static int RIGHT=1;
    int look;
    int shoot;

    int save;


    @Override
    public String dataToString() {
        return look+"|"+shoot+"|"+save;
    }

    @Override
    public void populateFromString(String data) {
        String[] fields=data.split("\\|");
        look=Integer.parseInt(fields[0]);
        shoot=Integer.parseInt(fields[1]);
        save=Integer.parseInt(fields[2]);

    }

    public int getSave() {
        return save;
    }

    public void setSave(int save) {
        this.save = save;
    }

    public int getLook() {
        return look;
    }

    public void setLook(int look) {
        this.look = look;
    }

    public int getShoot() {
        return shoot;
    }

    public void setShoot(int shoot) {
        this.shoot = shoot;
    }

    public boolean isLookLeft(){
        return look==LEFT;
    }

    public boolean isShootLeft(){
        return shoot==LEFT;
    }

}
