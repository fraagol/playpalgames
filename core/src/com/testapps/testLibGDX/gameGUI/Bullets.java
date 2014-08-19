package com.testapps.testLibGDX.gameGUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.testapps.testLibGDX.GameParams;

import java.util.ArrayList;
import java.util.List;

public class Bullets {
    private List<Vector3> positions;
    private final Texture texture;

    public Bullets() {
        texture = new Texture(Gdx.files.internal("bullet.png"));
        setupPositions();
    }

    private void setupPositions() {
        positions = new ArrayList<Vector3>();
        for (int numBullets = 1; numBullets <= GameParams.numBullets; numBullets++) {
            positions.add(new Vector3((Gdx.graphics.getWidth() -  Gdx.graphics.getWidth()/ 40 - texture.getWidth() * numBullets),
                    Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 10) - texture.getHeight(), 0));
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < positions.size(); i++) {
            batch.draw(this.texture, positions.get(i).x, positions.get(i).y);
        }

    }

    public void shoot() {
        positions.remove(positions.size() - 1);
    }


    public void recharge() {
        setupPositions();
    }
}
