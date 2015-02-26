package com.testapps.wildWistEast.gameStates;

import com.testapps.wildWistEast.turn.TurnAction;

public class ActionMessage {
    private TurnAction.Action turnAction;
    private Integer boardPos;

    public ActionMessage(TurnAction.Action turnAction, Integer boardPos) {
        this.turnAction = turnAction;
        this.boardPos = boardPos;
    }

    public TurnAction.Action getTurnAction() {
        return turnAction;
    }

    public Integer getBoardPos() {
        return boardPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionMessage that = (ActionMessage) o;

        if (boardPos != null ? !boardPos.equals(that.boardPos) : that.boardPos != null)
            return false;
        if (turnAction != that.turnAction) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = turnAction != null ? turnAction.hashCode() : 0;
        result = 31 * result + (boardPos != null ? boardPos.hashCode() : 0);
        return result;
    }
}
