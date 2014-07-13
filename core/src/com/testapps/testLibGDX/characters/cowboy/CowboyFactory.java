package com.testapps.testLibGDX.characters.cowboy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.testapps.testLibGDX.characters.cowboy.views.CowboyView;
import com.testapps.testLibGDX.characters.cowboy.views.CowboyViewMainPlayer;

public class CowboyFactory {

    private int currentID;
    private Texture cowboyTexture;

    public CowboyFactory() {
        currentID = 2; //ID 1 is reserved for player's cowboy
        this.cowboyTexture = new Texture(Gdx.files.internal("cowboyAndando.png"));
    }

    public void dispose(){
        cowboyTexture.dispose();
    }

    public Cowboy createMyPlayer() {
        CowboyView view = new CowboyViewMainPlayer(this.cowboyTexture);
        Cowboy cowboy = createCowboy(view, 1);
        return cowboy;
    }

    public Cowboy createEnemy() {
        CowboyView view = new CowboyView(this.cowboyTexture);
        Cowboy cowboy = createCowboy(view, currentID);
        currentID++;
        return cowboy;
    }

    private Cowboy createCowboy(CowboyView view, int id){
        return new Cowboy(view, id);
    }
}
