package com.testapps.testLibGDX.gameStates.selectRechargeState;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.testLibGDX.buttons.GameButtons;
import com.testapps.testLibGDX.characters.cowboy.CowboysBand;
import com.testapps.testLibGDX.gameGUI.Bullets;
import com.testapps.testLibGDX.gameStates.IGameStates;

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
