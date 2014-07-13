package com.testapps.testLibGDX.gameStates.selectShootState;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.testapps.testLibGDX.GameBoard;
import com.testapps.testLibGDX.buttons.IButtonsSubscribed;

public class SelectorButtonShoot implements IButtonsSubscribed {
    private Texture texture;
    private Vector2 pos;
    private SelectShootState state;
    private Integer boardPos;
    private Boolean enabled;

    public SelectorButtonShoot(Texture texture, Integer i, SelectShootState selectShootState) {
        this.texture = texture;
        this.boardPos = i;
        this.pos = GameBoard.getScreenPos(boardPos);
        this.pos.x -= texture.getWidth() / 2;
        this.pos.y -= texture.getHeight() / 2;
        this.state = selectShootState;
        this.enabled = false;
    }

    @Override
    public void render(SpriteBatch batch) {
        if(this.enabled)
            batch.draw(this.texture, pos.x, pos.y);
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    public int getWidth() {
        return this.texture.getWidth();
    }

    public int getHeight() {
        return this.texture.getHeight();
    }

    public Integer getBoardPos(){
        return this.boardPos;
    }

    @Override
    public void screenTouched(int screenX, int screenY) {
        if(this.enabled == false)
            return;

        if (screenX >= this.pos.x && screenX <= this.pos.x + this.texture.getWidth() &&
                screenY >= this.pos.y && screenY <= this.pos.y + this.texture.getHeight()) {
            state.selectorPushed(this);
        }
    }
}
