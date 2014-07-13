package com.testapps.testLibGDX.characters.cowboy.views;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CowboyViewMainPlayer extends CowboyView {
    Texture circle;


    public CowboyViewMainPlayer(Texture texture) {
        super(texture);
        circle = new Texture(Gdx.files.internal("selector.png"));

    }

    @Override
    protected void renderBehindMainPlayer(SpriteBatch batch, float elapsedTime)
    {
        batch.draw(circle, this.pos.x, this.pos.y);
    }

}
