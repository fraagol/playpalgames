package com.testapps.wildWistEast.gameStates;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.wildWistEast.GameBoard;
import com.testapps.wildWistEast.characters.cowboy.Cowboy;
import com.testapps.wildWistEast.characters.cowboy.CowboyOrientation;
import com.testapps.wildWistEast.characters.cowboy.CowboysBand;


/*
Game first screen creation. Displays the first disposition of the players and
game elements in the Battlefield.
 */

public class InitGameState implements IGameStates{
    CowboysBand cowboys;

    public InitGameState(CowboysBand cowboysBand) {
        cowboys = cowboysBand;
    }

    @Override
    public void render(SpriteBatch batch) {

    }



    @Override
    public void init() {
        //Host is places south and other player is placed north.
        Cowboy cowboy1 = cowboys.getCowboy(1);
        Cowboy cowboy2 = cowboys.getCowboy(2);

        cowboy2.setPos(1);
        cowboy2.stop(new CowboyOrientation(CowboyOrientation.STOP_S));
        cowboy1.setPos(4);
        GameBoard.setCowboyPosition(cowboy1, 4);
        GameBoard.setCowboyPosition(cowboy2, 1);
    }

    @Override
    public void dispose() {

    }
}
