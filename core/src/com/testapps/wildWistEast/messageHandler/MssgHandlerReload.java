package com.testapps.wildWistEast.messageHandler;

import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Bullets;
import com.testapps.wildWistEast.turn.TurnAction;

public class MssgHandlerReload implements IMessageHandler{
    private CowboysBand cowboysBand;
    private Bullets bullets;

    public MssgHandlerReload(CowboysBand cowboysBand, Bullets bullets) {
        this.cowboysBand = cowboysBand;
        this.bullets = bullets;
    }

    @Override
    public void work(TurnAction turnAction) {
        cowboysBand.getCowboy(turnAction.getPlayer()).rechargeGun();
        bullets.recharge();
    }
}
