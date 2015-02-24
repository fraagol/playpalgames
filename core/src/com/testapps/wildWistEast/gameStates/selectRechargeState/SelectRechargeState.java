package com.testapps.wildWistEast.gameStates.selectRechargeState;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Bullets;
import com.testapps.wildWistEast.gameStates.IGameStates;

public class SelectRechargeState implements IGameStates {
    private final CowboysBand band;
    private final Bullets bullets;

    public SelectRechargeState(CowboysBand cowboysBand, Bullets bullets) {
        this.band = cowboysBand;
        this.bullets = bullets;
    }

    @Override
    public void init() {
        this.band.getMyCowboy().rechargeGun();
        this.bullets.recharge();
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void dispose() {

    }
}
