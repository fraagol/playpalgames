package com.testapps.wildWistEast.messageHandler;

import com.testapps.wildWistEast.GameBoard;
import com.testapps.wildWistEast.characters.cowboy.Cowboy;
import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Lives;
import com.testapps.wildWistEast.turn.TurnAction;

public class MssgHandlerShoot implements IMessageHandler {

    private CowboysBand cowboysBand;
    private Lives lives;

    public MssgHandlerShoot(CowboysBand cowboysBand, Lives lives) {
        this.cowboysBand = cowboysBand;
        this.lives = lives;
    }

    @Override
    public void work(TurnAction turnAction) {
        cowboyShoots(turnAction);
        cowboyReceivesShoot(turnAction);
    }

    private void cowboyShoots(TurnAction turnAction) {
        cowboysBand.getCowboy(turnAction.getPlayer()).shootTo(turnAction.getTarget());
    }

    private void cowboyReceivesShoot(TurnAction turnAction) {
        Cowboy shootedCowboy = GameBoard.getCowboyAt(turnAction.getTarget());
        if (shootedCowboy == null) return;
        shootedCowboy.shooted();
        if (shootedCowboy == cowboysBand.getMyCowboy()) {
            lives.looseLife();
        }
    }
}
