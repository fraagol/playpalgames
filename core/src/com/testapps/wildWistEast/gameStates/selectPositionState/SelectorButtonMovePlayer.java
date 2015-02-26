package com.testapps.wildWistEast.gameStates.selectPositionState;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.testapps.wildWistEast.GameBoard;
import com.testapps.wildWistEast.gameStates.ActionMessage;
import com.testapps.wildWistEast.turn.TurnAction;
import com.testapps.wildWistEast.buttons.IButtonsSubscribed;

public class SelectorButtonMovePlayer implements IButtonsSubscribed {
    private Texture texture;
    private Vector2 pos;
    private SelectPositionState state;
    private Boolean enabled;
    private Integer boardPos;

    public SelectorButtonMovePlayer(Texture texture, Integer boardPos, SelectPositionState state) {
        this.texture = texture;
        this.pos = GameBoard.getScreenPos(boardPos);
        this.pos.x -= texture.getWidth() / 2;
        this.pos.y -= texture.getHeight() / 2;
        this.state = state;
        this.boardPos = boardPos;
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
    public Integer getBoardPos() {
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
