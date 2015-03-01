package com.testapps.wildWistEast.messageHandler;

import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.turn.TurnAction;

public class MssgHandlerReload implements IMessageHandler{
    private CowboysBand cowboysBand;

    public MssgHandlerReload(CowboysBand cowboysBand) {
        this.cowboysBand = cowboysBand;
    }

    @Override
    public void work(TurnAction turnAction) {
        cowboysBand.getCowboy(turnAction.getPlayer()).rechargeGun();
    }
}
