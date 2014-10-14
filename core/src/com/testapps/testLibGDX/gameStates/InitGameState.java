package com.testapps.testLibGDX.gameStates;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.testLibGDX.GameBoard;
import com.testapps.testLibGDX.characters.cowboy.Cowboy;
import com.testapps.testLibGDX.characters.cowboy.CowboyOrientation;
import com.testapps.testLibGDX.characters.cowboy.CowboysBand;


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
        Cowboy cowboy1 = cowboys.getMyCowboy();
        Cowboy cowboy2 = cowboys.getEnemies().get(0);

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
