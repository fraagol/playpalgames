package com.testapps.testLibGDX.gameStates.selectPositionState;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.testapps.testLibGDX.GameBoard;
import com.testapps.testLibGDX.buttons.IButtonsSubscribed;

public class SelectorButtonMovePlayer implements IButtonsSubscribed {
    private Texture texture;
    private Vector2 pos;
    private SelectPositionState state;
    private Integer boardPos;
    private Boolean enabled;


    public SelectorButtonMovePlayer(Texture texture, Integer boardPos, SelectPositionState state) {
        this.texture = texture;
        this.pos = GameBoard.getScreenPos(boardPos);
        this.pos.x -= texture.getWidth() / 2;
        this.pos.y -= texture.getHeight() / 2;
        this.boardPos = boardPos;
        this.state = state;
        enabled = false;
    }

    public void render(SpriteBatch batch) {
        if(this.enabled == false)
            return;
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

    public Integer getBoardPos(){
        return this.boardPos;
    }

    public void dispose() {
        texture.dispose();
    }

    public int getWidth() {
        return this.texture.getWidth();
    }

    public int getHeight() {
        return this.texture.getHeight();
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
