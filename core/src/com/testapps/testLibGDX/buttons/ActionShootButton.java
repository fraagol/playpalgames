package com.testapps.testLibGDX.buttons;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ActionShootButton implements IActionButton{
    private Texture texture;
    private Vector3 pos;
    private boolean visible = true;


    public ActionShootButton(Texture texture, Vector3 pos) {
        this.texture = texture;
        this.pos = pos;
    }

    @Override
    public void render(SpriteBatch batch) {
        if(this.visible)
            batch.draw(this.texture, pos.x, pos.y);
    }

    @Override
    public Vector3 getPos() {
        return pos;
    }

    @Override
    public Boolean touchEvent(int screenX, int screenY) {
        Boolean result = false;
        if(screenX >= this.pos.x && screenX <= this.pos.x + this.texture.getWidth() &&
                screenY >= this.pos.y && screenY <= this.pos.y + this.texture.getHeight())
        {
            result = true;
        }

        return result;
    }

    @Override
    public void show() {
        this.visible = true;
    }

    @Override
    public void hide() {
        this.visible = false;
    }

    @Override
    public void dispose() {
        this.texture.dispose();
    }
}
