package com.testapps.wildWistEast.buttons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.wildWistEast.turn.TurnAction;

public interface IButtonsSubscribed {

    public void screenTouched(int screenX, int screenY);

    public void dispose();

    public void render(SpriteBatch batch);

    public void enable();

    public void disable();

    public Integer getBoardPos();

    public TurnAction.Action getAction();
}
