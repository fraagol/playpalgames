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

    private static GameController GAME_CONTROLLER;

    public static GameController createGameController(HttpTransport httpTransport, JsonFactory jsonFactory, User userP, ChallengesClient challengesClient, boolean localServer) throws IOException{
        GAME_CONTROLLER=new GameControllerImpl(httpTransport,jsonFactory,userP, challengesClient, localServer);
        return GAME_CONTROLLER;
    }

    public static GameController getInstance(){
        //If game_controller hasn't been initialized, a dummy gameController is created
        if (GAME_CONTROLLER==null){
            GAME_CONTROLLER= new GameControllerDummy();
        }
        return GAME_CONTROLLER;
    }

    abstract public boolean isMyTurn();

    abstract public void setMyTurn(boolean isMyTurn);

    abstract public boolean isHost();

    abstract public void setHost(boolean host);

    abstract public void processCommand(String msg) throws IOException;

    abstract public void getTurnsFromServer() throws IOException;

    abstract public  <T> T getNextTurn();

    abstract public Match createMatch() throws IOException;

    abstract public void joinMatch(Long matchId) throws IOException;

    abstract public void sendTurn(Object o) throws IOException;

    abstract public List<Turn> listTurns() throws IOException;

    abstract public List<Turn> listTurnsFromNumber(Long turnNumber) throws IOException;

    abstract public List<User> listUsers() throws IOException;

    abstract public void sendInvitation(User userToInvite) throws IOException;

    abstract public void acceptChallenge(String matchId)throws IOException;

    abstract public Long getMatchId();
}
