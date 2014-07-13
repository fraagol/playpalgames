package com.testapps.testLibGDX.gameStates;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        Cowboy cowboy1 = cowboys.getCowboy(1);
        Cowboy cowboy2 = cowboys.getCowboy(2);

        cowboy2.positionInBoard(1);
        cowboy2.stop(new CowboyOrientation(CowboyOrientation.STOP_S));
        cowboy1.positionInBoard(4);
    }

    @Override
    public void dispose() {

    }
}
