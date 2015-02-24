package com.testapps.wildWistEast.gameGUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.testapps.wildWistEast.GameParams;

import java.util.ArrayList;
import java.util.List;

public class Lives {
    private List<Vector3> positions;
    private final Texture texture;

    public Lives() {
        texture = new Texture(Gdx.files.internal("heart.png"));
        setupPositions();
    }

    private void setupPositions() {
        positions = new ArrayList<Vector3>();
        for (int numLives = 1; numLives <= GameParams.numLives; numLives++) {
            positions.add(new Vector3((Gdx.graphics.getWidth() -  Gdx.graphics.getWidth()/ 40 - texture.getWidth() * numLives),
                    Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 40) - texture.getHeight(), 0));
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < positions.size(); i++) {
            batch.draw(this.texture, positions.get(i).x, positions.get(i).y);
        }

    }

    //@todo not called yet
    public void looseLife() {
        positions.remove(positions.size() - 1);
    }
}
