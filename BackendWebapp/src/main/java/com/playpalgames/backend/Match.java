package com.playpalgames.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by javi on 08/07/2014.
 */
@Entity
public class Match {


    //DONT MODIFY WITHOUT MODIFYING GameController!!!!!!!!!!!
    public static final int STATUS_CREATED = 0;
    public static final int STATUS_INVITATION_SENT = 1;
    public static final int STATUS_INVITATION_ACCEPTED = 2;
    public static final int STATUS_IN_GAME = 3;
    public static final int STATUS_CANCELED = 4;
    public static final int STATUS_HOST_FINISHED = 5;
    public static final int STATUS_GUEST_FINISHED = 6;
    public static final int STATUS_FINISHED = 7;


    @Id
    Long id;

    @Index
    Long hostUserId;

    @Index
    Long guestUserId;

    @Index
    Integer status;

    int gameType;

    String hostName;

    String guestName;

    long nextTurnPlayerId;


    public Match() {
        status = STATUS_CREATED;
    }

    public long getNextTurnPlayerId() {
        return nextTurnPlayerId;
    }

    public void setNextTurnPlayerId(long nextTurnPlayerId) {
        this.nextTurnPlayerId = nextTurnPlayerId;
    }

    public Match(Integer status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(Long hostUserId) {
        this.hostUserId = hostUserId;
    }

    public Long getGuestUserId() {
        return guestUserId;
    }

    public void setGuestUserId(Long guestUserId) {
        this.guestUserId = guestUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }


    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", status=" + status +
                ", hostUserId =" + hostUserId +
                ",guestUserId =" + guestUserId +
                ",hostName =" + hostName +
                ",guestName =" + guestName +
                ",nextTurnPlayerId =" + nextTurnPlayerId +
                '}';
    }
}
