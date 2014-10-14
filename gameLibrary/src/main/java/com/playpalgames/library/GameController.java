package com.playpalgames.library;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.playpalgames.backend.gameEndpoint.model.Match;
import com.playpalgames.backend.gameEndpoint.model.Turn;
import com.playpalgames.backend.gameEndpoint.model.User;

import java.io.IOException;
import java.util.List;

/**
 * Created by javi on 18/08/2014.
 */
public abstract class GameController {

    public static final int STATUS_CREATED = 0;
    public static final int STATUS_INVITATION_SENT = 1;
    public static final int STATUS_INVITATION_ACCEPTED = 2;
    public static final int STATUS_IN_GAME = 3;
    public static final int STATUS_CANCELED = 4;
    public static final int STATUS_FINISHED = 5;
    static final int CONTROLLER_STATE_NOT_IN_GAME = 0;
    static final int CONTROLLER_STATE_IN_GAME = 1;

    private static GameController GAME_CONTROLLER;

    public static GameController createGameController(HttpTransport httpTransport, JsonFactory jsonFactory, User userP, ChallengesClient challengesClient, boolean localServer, String build) throws IOException {
        GAME_CONTROLLER = new GameControllerImpl(httpTransport, jsonFactory, userP, challengesClient, localServer, build);
        return GAME_CONTROLLER;
    }

    public static GameController getInstance() {
        //If game_controller hasn't been initialized, a dummy gameController is created
        if (GAME_CONTROLLER == null) {
            GAME_CONTROLLER = new GameControllerDummy();
        }
        return GAME_CONTROLLER;
    }

    public abstract boolean isNewGame();

    abstract public boolean isMyTurn();

    abstract public void setMyTurn(boolean isMyTurn);

    abstract public boolean isHost();

    abstract public String getOpponentName();

    abstract public void setHost(boolean host);

    public abstract void initMatch();

    public abstract void exitMatch();

    abstract public void processCommand(String msg) throws IOException;

    public abstract List<Match> retrievePendingGames() throws IOException;

    public abstract String getStateAndLastTurn() throws IOException;

    abstract public boolean getTurnsFromServer() throws IOException;

    abstract public <T extends GameTurn> T getNextTurn(T turn);

    public abstract void setMatch(Match m);

    abstract public void endMatch() throws IOException;

    public abstract Match createMatch(User userToInvite, int gameType) throws IOException;

    abstract public void joinMatch(Long matchId) throws IOException;

    abstract public <T extends GameTurn> void sendTurn(T o, String gameState, boolean opponentIsNext) throws IOException;

    abstract public List<Turn> listTurns() throws IOException;

    abstract public User getUser();

    //abstract public List<Turn> listTurnsFromNumber(Long turnNumber) throws IOException;

    abstract public List<User> listUsers() throws IOException;

    abstract public void acceptChallenge(String matchId) throws IOException;

    abstract public Long getMatchId();

    abstract public void addGameClientListener(GameClient gameClient);
}
