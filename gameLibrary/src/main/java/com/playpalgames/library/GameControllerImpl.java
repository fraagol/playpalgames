package com.playpalgames.library;

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.playpalgames.backend.gameEndpoint.GameEndpoint;
import com.playpalgames.backend.gameEndpoint.model.Match;
import com.playpalgames.backend.gameEndpoint.model.Turn;
import com.playpalgames.backend.gameEndpoint.model.User;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


public class GameControllerImpl extends GameController {
    private static final Logger LOG = Logger.getLogger(GameControllerImpl.class.getName());
    Match match;
    User user;
    GameEndpoint gameEndpoint;
    private ChallengesClient challengesClient;
    private long lastProcessedTurnId = -1;
    private int state;

    private GameClient gameClient = null;

    @Override
    public boolean isNewGame() {
        return match.getStatus() <= (STATUS_INVITATION_ACCEPTED);
    }

    @Override
    public boolean isMyTurn() {
        return isMyTurn;
    }

    @Override
    public void setMyTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
    }

    private boolean isMyTurn = false;

    @Override
    public boolean isHost() {
        return host;
    }

    @Override
    public String getOpponentName() {
        return match.getHostUserId().equals(user.getId()) ? match.getGuestName() : match.getHostName();
    }

    @Override
    public void setHost(boolean host) {
        this.host = host;
    }

    private boolean host = false;

    private LinkedList<Turn> availablesTurns = null;


    public GameControllerImpl(HttpTransport httpTransport, JsonFactory jsonFactory, User userP, ChallengesClient challengesClient, boolean localServer, String buildString) throws IOException {
        GameEndpoint.Builder builder = new GameEndpoint.Builder(httpTransport, jsonFactory, new DisableTimeout());
        if (localServer) {
            localServer(builder, buildString);
        }
        LOG.info("creating GameController");
        gameEndpoint = builder.build();
        user = gameEndpoint.register(userP).execute();
        this.challengesClient = challengesClient;

    }

    @Override
    public void initMatch() {
        LOG.info("Init match " + match.getId());
        state = CONTROLLER_STATE_IN_GAME;
        availablesTurns = new LinkedList<Turn>();
    }

    @Override
    public void exitMatch() {
        LOG.info("Exit match " + match.getId());
        state = CONTROLLER_STATE_NOT_IN_GAME;
        availablesTurns.clear();
        availablesTurns = null;
    }

    @Override
    public void processCommand(String msg) throws IOException {
        LOG.info("Processing Command " + msg);
        String[] command = msg.split(" ");

        switch (command[0].charAt(0)) {
            case 'A'://challenge Accepted

                setHost(true);
                setMyTurn(true);
                match.setId(Long.valueOf(command[1]));
                challengesClient.challengeAccepted();

                break;
            case 'C': //Challenge received
                challengesClient.incomingChallenge(command[1], command[2], command[3]);
                break;
            case 'T'://Turns availables
                if (state == CONTROLLER_STATE_IN_GAME) {
                    getTurnsFromServer();
                }
                break;
        }
    }

    @Override
    public List<Match> retrievePendingGames() throws IOException {
        LOG.info("retrievePendingGames");
        List<Match> pendingMatches = gameEndpoint.listGamesByPlayer(user.getId()).execute().getItems();
        return pendingMatches;
    }

    @Override
    public String getStateAndLastTurn() throws IOException {
        LOG.info("getStateAndLastTurn");
        Turn turn = gameEndpoint.getLastTurn(getMatchId()).execute();
        availablesTurns.add(turn);
        lastProcessedTurnId = turn.getTurnNumber();
        return turn.getGameState();
    }

    @Override
    synchronized public boolean getTurnsFromServer() throws IOException {
        LOG.info("getTurnsFromServer");

        List<Turn> turns = gameEndpoint.listTurnsFrom(getMatchId(), lastProcessedTurnId).execute().getItems();
        if (turns != null && turns.size() > 0) {
            LOG.info("  Retrieved " + turns.size() + " turns");

            for (int i = turns.size(); i > 0; i--) {
                availablesTurns.add(turns.get(i - 1));
                lastProcessedTurnId = turns.get(i - 1).getTurnNumber();
            }
        }
        //Notify game client if there are turns availables
        if (gameClient != null && availablesTurns != null && !availablesTurns.isEmpty()) {
            gameClient.turnAvailable();
            return true;
        }
        return false;
    }

    @Override
    public <T extends GameTurn> T getNextTurn(T turnToPopulate) {
        if (availablesTurns == null) {
            return null;
        }
        Turn t = availablesTurns.poll();
        if (t != null) {
            turnToPopulate.populateFromString(t.getTurnData());
            return turnToPopulate;
        } else {
            return null;
        }
    }

    @Override
    public void setMatch(Match m) {
        this.match = m;
        setHost(match.getHostUserId().equals(user.getId()));
    }

    @Override
    public void endMatch() throws IOException {
        gameEndpoint.endMatch(match.getId(), isHost()).execute();
    }

    @Override
    public Match createMatch(User userToInvite, int gameType) throws IOException {
        match = gameEndpoint.createMatch(user.getId(), userToInvite.getId(), userToInvite.getName(), user.getName(), gameType).execute();
        info(match);
        return match;
    }


    @Override
    public void joinMatch(Long matchId) throws IOException {
        match = gameEndpoint.joinMatch(user.getId(), matchId, user.getName()).execute();
        info(match);
    }

    /**
     * @param o              turn to send
     * @param gameState      snapshot of the Game for game reloading
     * @param opponentIsNext same purpose, set next player after reloading
     * @param <T>
     * @throws IOException
     */
    @Override
    public <T extends GameTurn> void sendTurn(T o, String gameState, boolean opponentIsNext) throws IOException {
        Turn turn = new Turn();
        turn.setMatchId(match.getId());
        turn.setPlayerId(user.getId());
        turn.setTurnData(o.dataToString());
        turn.setGameState(gameState);
        turn.setOpponentIsNext(opponentIsNext);


        Turn t = gameEndpoint.insertTurn(turn).execute();
        lastProcessedTurnId = t.getTurnNumber();
        info("Sent turn: " + t);
    }

    @Override
    public List<Turn> listTurns() throws IOException {
        List<Turn> turns = gameEndpoint.listTurns(match.getId()).execute().getItems();
        for (Turn turnN : turns) {

            info(turnN);
        }
        return turns;
    }


    @Override
    public List<User> listUsers() throws IOException {
        List<User> users = gameEndpoint.listOthersDevices(user.getId()).execute().getItems();
        return users;
    }


    @Override
    public void acceptChallenge(String matchId) throws IOException {
        match = gameEndpoint.joinMatch(user.getId(), Long.valueOf(matchId), user.getName()).execute();
        setHost(false);
        setMyTurn(false);
    }


    @Override
    public Long getMatchId() {
        return match != null ? match.getId() : null;
    }

    @Override
    public void addGameClientListener(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    private void localServer(AbstractGoogleClient.Builder builder, String buildString) {
        //  builder.setRootUrl("http://localhost:8080/_ah/api/");
        if (buildString.matches(".*_?sdk_?.*")) {
            builder.setRootUrl("http://10.0.2.2:8080/_ah/api/");
        } else {
            builder.setRootUrl("http://192.168.1.16:8080/_ah/api/");
        }
    }


    private static void info(Object s) {
        LOG.info(s.toString());

    }

    public User getUser() {
        return user;
    }

    class DisableTimeout implements HttpRequestInitializer {
        public void initialize(HttpRequest request) {
            request.setConnectTimeout(0);
            request.setReadTimeout(0);
        }
    }
}