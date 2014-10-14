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
    public boolean isNewGame() {
        return false;
    }

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
    public String getOpponentName() {
        return null;
    }

    @Override
    public void setHost(boolean host) {

    }

    @Override
    public void initMatch() {

    }

    @Override
    public void exitMatch() {

    }

    @Override
    public void processCommand(String msg) throws IOException {

    }

    @Override
    public List<Match> retrievePendingGames() throws IOException {
        return null;
    }

    @Override
    public String getStateAndLastTurn() throws IOException {
        return null;
    }

    @Override
    public boolean getTurnsFromServer() throws IOException {
        return false;
    }

    @Override
    public <T extends GameTurn> T getNextTurn(T turn) {
        return null;
    }

    @Override
    public void setMatch(Match m) {

    }

    @Override
    public void endMatch() throws IOException {

    }

    @Override
    public Match createMatch(User userToInvite, int gameType) throws IOException {
        return null;
    }


    @Override
    public void joinMatch(Long matchId) throws IOException {

    }

    @Override
    public <T extends GameTurn> void sendTurn(T o, String gameState, boolean opponentIsNext) throws IOException {

    }

    @Override
    public List<Turn> listTurns() throws IOException {
        return null;
    }

    @Override
    public User getUser() {
        return null;
    }

//    @Override
//    public List<Turn> listTurnsFromNumber(Long turnNumber) throws IOException {
//        return null;
//    }

    @Override
    public List<User> listUsers() throws IOException {
        return null;
    }


    @Override
    public void acceptChallenge(String matchId) throws IOException {

    }

    @Override
    public Long getMatchId() {
        return null;
    }

    @Override
    public void addGameClientListener(GameClient gameClient) {

    }
}
