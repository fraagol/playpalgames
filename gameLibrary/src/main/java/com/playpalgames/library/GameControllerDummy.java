package com.playpalgames.library;

import com.playpalgames.backend.gameEndpoint.model.Match;
import com.playpalgames.backend.gameEndpoint.model.Turn;
import com.playpalgames.backend.gameEndpoint.model.User;

import java.io.IOException;
import java.util.List;

/**
 * Created by javi on 18/08/2014.
 */
public class GameControllerDummy extends GameController {

    @Override
    public boolean isMyTurn() {
        return false;
    }

    @Override
    public void setMyTurn(boolean isMyTurn) {

    }

    @Override
    public boolean isHost() {
        return false;
    }

    @Override
    public void setHost(boolean host) {

    }

    @Override
    public void processCommand(String msg) throws IOException {

    }

    @Override
    public void getTurnsFromServer() throws IOException {

    }

    @Override
    public <T> T getNextTurn() {
        return null;
    }

    @Override
    public Match createMatch() throws IOException {
        return null;
    }

    @Override
    public void joinMatch(Long matchId) throws IOException {

    }

    @Override
    public void sendTurn(Object o) throws IOException {

    }

    @Override
    public List<Turn> listTurns() throws IOException {
        return null;
    }

    @Override
    public List<Turn> listTurnsFromNumber(Long turnNumber) throws IOException {
        return null;
    }

    @Override
    public List<User> listUsers() throws IOException {
        return null;
    }

    @Override
    public void sendInvitation(User userToInvite) throws IOException {

    }

    @Override
    public void acceptChallenge(String matchId) throws IOException {

    }

    @Override
    public Long getMatchId() {
        return null;
    }
}
