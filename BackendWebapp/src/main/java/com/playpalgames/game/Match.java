package com.playpalgames.game;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by javi on 08/07/2014.
 */
@Entity
public class Match {

    public static int STATUS_CREATED=0;

    @Id
    Long id;

    Integer status;

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

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}
