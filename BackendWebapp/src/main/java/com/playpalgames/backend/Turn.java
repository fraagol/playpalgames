package com.playpalgames.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by javi on 08/07/2014.
 */

@Entity
public class Turn {
    @Id
    Long id;

    Long playerId;

    @Index
    Long matchId;
    @Index
    Long turnNumber;

    String turnData;

    String gameState;

    boolean opponentIsNext;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(Long turnNumber) {
        this.turnNumber = turnNumber;
    }

    public boolean isOpponentIsNext() {
        return opponentIsNext;
    }

    public void setOpponentIsNext(boolean opponentIsNext) {
        this.opponentIsNext = opponentIsNext;
    }

    public String getTurnData() {
        return turnData;
    }

    public void setTurnData(String turnData) {
        this.turnData = turnData;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public String toString() {
        return "M " + matchId + " N " + this.getTurnNumber() + " Id " + id + "Data: " + turnData;
    }
}
