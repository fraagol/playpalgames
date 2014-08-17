package com.playpalgames.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javi on 08/07/2014.
 */
@Entity
public class Match {

    public static int STATUS_CREATED=0;

    @Id
    Long id;

    Long userId;

    Integer status;

    List<User> players = new ArrayList<User>();



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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<User> getPlayers() {

        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}
