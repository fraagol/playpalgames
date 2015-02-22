package com.testapps.wildWistEast.turn;

import com.playpalgames.library.GameTurn;

import java.util.List;

/**
 * Message sent by the host to other players.
 * It sends the position where each player is.
 * For more than two players a different structure will be needed.
 * Maybe send also configuration values like number of bullets, lifes...?
 *
 * If I receive the message, I'm the "other" because the sender is the "host".
 */
public class ConfigurationMessage implements GameTurn{

    Integer hostID;
    Integer hostPos;
    Integer otherID;
    Integer otherPos;

    public ConfigurationMessage(Integer hostID, Integer hostPos, Integer otherID, Integer otherPos) {
        this.hostID = hostID;
        this.hostPos = hostPos;
        this.otherID = otherID;
        this.otherPos = otherPos;
    }

    @Override
    public String dataToString() {
        return hostID.toString() + "|" + hostPos.toString() + "|" + otherID.toString()  + "|" + otherPos.toString();
    }

    @Override
    public void populateFromString(String data) {
        String[] fields = data.split("\\|");
        hostID = Integer.valueOf(fields[0]);
        hostPos = Integer.parseInt(fields[1]);
        otherID = Integer.parseInt(fields[2]);
        otherPos = Integer.parseInt(fields[3]);
    }
}
