package com.testapps.wildWistEast.messageHandler;

import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.turn.TurnAction;

public class MssgHandlerMove implements IMessageHandler{
    private CowboysBand cowboysBand;

    public MssgHandlerMove(CowboysBand cowboysBand) {
        this.cowboysBand = cowboysBand;
    }

    @Override
    public void work(TurnAction turnAction) {
        cowboysBand.getCowboy(turnAction.getPlayer()).moveTo(turnAction.getTarget());
    }
}
