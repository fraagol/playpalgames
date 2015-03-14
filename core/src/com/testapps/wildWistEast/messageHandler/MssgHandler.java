package com.testapps.wildWistEast.messageHandler;

import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Bullets;
import com.testapps.wildWistEast.gameGUI.Lives;
import com.testapps.wildWistEast.turn.TurnAction;

public class MssgHandler {
    MssgHandlerMove mssgHandlerMove;
    MssgHandlerShoot mssgHandlerShoot;
    MssgHandlerReload mssgHandlerReload;

    public MssgHandler(CowboysBand cowboysBand, Lives lives, Bullets bullets) {
        this.mssgHandlerMove = new MssgHandlerMove(cowboysBand);
        this.mssgHandlerShoot = new MssgHandlerShoot(cowboysBand, lives, bullets);
        this.mssgHandlerReload = new MssgHandlerReload(cowboysBand, bullets);
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
