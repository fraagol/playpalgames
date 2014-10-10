package com.playpalgames.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by javi on 08/07/2014.
 */
@Entity
public class Match {

    public static int STATUS_CREATED=0;
    public static int STATUS_INVITATION_SENT=1;
    public static int STATUS_INVITATION_ACCEPTED=2;
    public static int STATUS_IN_GAME=3;
    public static int STATUS_CANCELED=4;
    public static int STATUS_FINISHED=5;


    @Id
    Long id;

    @Index
    Long hostUserId;

    @Index
    Long guestUserId;

    @Index
    Integer status;


    String hostName;

    String guestName;




    public Match() {
        status=STATUS_CREATED;
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

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}
