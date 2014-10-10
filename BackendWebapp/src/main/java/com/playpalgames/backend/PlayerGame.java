package com.playpalgames.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by javi on 03/10/2014.
 */
@Entity
public class PlayerGame {

    @Id

    Long id;

    @Index
    Long playerId;

    Long matchId;

    int state;

    public PlayerGame(Long playerId, Long matchId) {
        this.playerId = playerId;
        this.matchId = matchId;
        this.state = 0;
    }

    public PlayerGame() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
}
