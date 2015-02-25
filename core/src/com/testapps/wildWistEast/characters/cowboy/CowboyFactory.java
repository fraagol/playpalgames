package com.testapps.wildWistEast.characters.cowboy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.testapps.wildWistEast.characters.cowboy.views.CowboyView;
import com.testapps.wildWistEast.characters.cowboy.views.CowboyViewMainPlayer;

public class CowboyFactory {

    private Texture cowboyTexture;
    private int myPlayerID;
    private int enemyID;

    public CowboyFactory(boolean amIHost) {
        this.cowboyTexture = new Texture(Gdx.files.internal("cowboyAndando.png"));
        if(amIHost) {
            myPlayerID = 1;
            enemyID = 2;
        } else {
            myPlayerID = 2;
            enemyID = 1;
        }
    }

    public Cowboy createMyPlayer() {
        CowboyView view = new CowboyViewMainPlayer(this.cowboyTexture);
        Cowboy cowboy = createCowboy(view, myPlayerID);
        return cowboy;
    }

    public Cowboy createEnemy() {
        CowboyView view = new CowboyView(this.cowboyTexture);
        Cowboy cowboy = createCowboy(view, enemyID);
        return cowboy;
    }

    private Cowboy createCowboy(CowboyView view, int id){
        return new Cowboy(view, id);
    }

    public void dispose(){
        cowboyTexture.dispose();
    }
}
