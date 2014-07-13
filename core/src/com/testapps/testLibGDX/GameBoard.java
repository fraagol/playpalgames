package com.testapps.testLibGDX;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.testapps.testLibGDX.characters.cowboy.Cowboy;
import com.testapps.testLibGDX.characters.cowboy.CowboysBand;

import java.util.HashMap;

public class GameBoard {

    private static HashMap<Integer, Vector2> coordenades;
    private static HashMap<Integer, Cowboy> cowboys;

    public static void initBoard(CowboysBand band){
        createCoordenades();
        createCowboyPositions(band);
    }



    public static Vector2 getScreenPos(Integer pos){
        return coordenades.get(pos);
    }

    public static Cowboy getCowboyAt(Integer pos){
        return cowboys.get(pos);
    }

    private static void createCowboyPositions(CowboysBand band) {
        cowboys = new HashMap<Integer, Cowboy>();
        for(int i = 1; i <= 6; i++)
        {
            cowboys.put(i, band.getCowboy(i));
        }
    }

    private static void createCoordenades() {
        coordenades = new HashMap<Integer, Vector2>();
        int height20Percent = Gdx.graphics.getHeight() / 5;
        int width25Percent = Gdx.graphics.getWidth() / 4;

        coordenades.put(1, new Vector2(width25Percent * 2, Gdx.graphics.getHeight() - height20Percent));
        coordenades.put(2, new Vector2(width25Percent * 3, Gdx.graphics.getHeight() - height20Percent * 2));
        coordenades.put(3, new Vector2(width25Percent * 3, Gdx.graphics.getHeight() - height20Percent * 3));
        coordenades.put(4, new Vector2(width25Percent * 2, height20Percent));
        coordenades.put(5, new Vector2(width25Percent, Gdx.graphics.getHeight() - height20Percent * 3));
        coordenades.put(6, new Vector2(width25Percent, Gdx.graphics.getHeight() - height20Percent * 2));
    }
}
