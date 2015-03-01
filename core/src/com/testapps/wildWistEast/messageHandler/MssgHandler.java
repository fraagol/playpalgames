package com.testapps.wildWistEast.messageHandler;

import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Lives;
import com.testapps.wildWistEast.turn.TurnAction;

public class MssgHandler {
    MssgHandlerMove mssgHandlerMove;
    MssgHandlerShoot mssgHandlerShoot;
    MssgHandlerReload mssgHandlerReload;

    public MssgHandler(CowboysBand cowboysBand, Lives lives) {
        this.mssgHandlerMove = new MssgHandlerMove(cowboysBand);
        this.mssgHandlerShoot = new MssgHandlerShoot(cowboysBand, lives);
        this.mssgHandlerReload = new MssgHandlerReload(cowboysBand);
    }

    public void handle(TurnAction turnAction) {
        if(turnAction.getAction() == TurnAction.Action.MOVE) {
            mssgHandlerMove.work(turnAction);
        }

        if(turnAction.getAction() == TurnAction.Action.SHOOT) {
            mssgHandlerShoot.work(turnAction);
        }

        if(turnAction.getAction() == TurnAction.Action.RELOAD) {
            mssgHandlerReload.work(turnAction);
        }
    }
}
