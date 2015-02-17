package com.testapps.wildWistEast.characters.cowboy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class CowboysBand {
    HashMap<Integer, Cowboy> cowboysControllers;

    public CowboysBand() {
        cowboysControllers = new HashMap<Integer, Cowboy>();
    }

    public void addCowboyToBand(Cowboy cowboy){
        cowboysControllers.put(cowboy.getID(), cowboy);
    }

    public Cowboy getMyCowboy(){
        return this.cowboysControllers.get(1);
    }

    public Cowboy getCowboy(Integer id){
        return this.cowboysControllers.get(id);
    }

    public void render(SpriteBatch batch){
        for (Cowboy cb : this.cowboysControllers.values()) {
            cb.render(batch);
        }

    }

    public Array<Cowboy> getEnemies(){
        Array<Cowboy> enemies = new Array<Cowboy>();
        for(Cowboy cowboy : this.cowboysControllers.values())
        {
            if(cowboy.getID() != 1)
            {
                enemies.add(cowboy);
            }
        }
        return enemies;
    }

    public void dispose(){
        for(Cowboy cowboy : this.cowboysControllers.values())
            cowboy.dispose();
    }
}
