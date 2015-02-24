package com.testapps.wildWistEast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BackGround {
    Texture textureTest;

    public BackGround() {
        FileHandle backGroundImage = Gdx.files.internal("pruebatonta.jpg");
        if(backGroundImage.exists()) {
            this.textureTest = new Texture(backGroundImage);
        }
    }

    public void render(SpriteBatch batch) {
        if(textureTest != null) {
            batch.draw(this.textureTest, 0, 0);
        }
    }
}
