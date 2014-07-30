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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class GameController {
    private static final Logger LOG = Logger.getLogger(GameController.class.getName());

    private static  GameController GAME_CONTROLLER;
    Match match;
    User user;
    GameEndpoint gameEndpoint;
    public String message="";

    public static GameController createGameController(HttpTransport httpTransport, JsonFactory jsonFactory, User userP, boolean localServer) throws IOException{
        GAME_CONTROLLER=new GameController(httpTransport,jsonFactory,userP,localServer);
        return GAME_CONTROLLER;
    }

    public static GameController getInstance(){
        return GAME_CONTROLLER;
    }

    public GameController(HttpTransport httpTransport, JsonFactory jsonFactory, UserFinder finder, boolean localServer) throws IOException {
        GameEndpoint.Builder builder= new GameEndpoint.Builder(httpTransport,jsonFactory , new DisableTimeout());
        if (localServer) {
            localServer(builder);
        }
        gameEndpoint=builder.build();
        user= finder.getUser();
        user= gameEndpoint.register(user).execute();

    }

    public GameController(HttpTransport httpTransport, JsonFactory jsonFactory, User userP, boolean localServer) throws IOException {
        GameEndpoint.Builder builder= new GameEndpoint.Builder(httpTransport,jsonFactory , new DisableTimeout());
        if (localServer) {
            localServer(builder);
        }
        gameEndpoint=builder.build();
        user= gameEndpoint.register(userP).execute();

    }

    public void addTurn(String[] commands){
        System.out.println("Add Turn called");
        message= Arrays.deepToString(commands);
    }

    public Match createMatch() throws IOException {
        match= gameEndpoint.createMatch(user.getId()).execute();
        info(match);
        return match;
    }
    public void joinMatch(Long matchId) throws IOException {
        match= gameEndpoint.joinMatch(user.getId(),matchId ).execute();
        log(match);
    }

    public void sendTurn(Object o) throws IOException {
        Turn turn = new Turn();
        turn.setMatchId(match.getId());
        turn.setPlayerId(user.getId());
        List list= new ArrayList();
        list.add(o);
        list.add(new Date());
        turn.setTurnData(list);

        Turn t =  gameEndpoint.insertTurn(turn).execute();
        log(t);
    }

    public List<Turn> listTurns() throws IOException {
        List<Turn> turns=  gameEndpoint.listTurns(match.getId()).execute().getItems();
        for(Turn turnN:turns){

            log(turnN);
        }
        return turns;
    }

    public List<Turn> listTurnsFromNumber(Long turnNumber) throws IOException {
        List<Turn> turns=  gameEndpoint.listTurnsFrom(match.getId(),turnNumber).execute().getItems();
        for(Turn turnN:turns){

            log(turnN);
        }
        return turns;
    }

    public List<User> listUsers() throws IOException {
        List<User> users=  gameEndpoint.listDevices().execute().getItems();
        return  users;
    }

    public void sendInvitation(User userToInvite) throws IOException{
        log("Sending invitation to match "+match.getId()+" to user "+ userToInvite.getName());
        gameEndpoint.sendMessage("C "+ user.getName()+" "+ getMatchId(),userToInvite.getRegId()).execute();
    }

    public void acceptChallenge(String matchId)throws IOException{
        match=gameEndpoint.joinMatch(user.getId(),Long.valueOf(matchId)).execute();
    }



    public Long getMatchId(){
        return  match!=null? match.getId():null;
    }

    private void localServer(AbstractGoogleClient.Builder builder) {
        //  builder.setRootUrl("http://localhost:8080/_ah/api/");
        builder.setRootUrl("http://10.0.2.2:8080/_ah/api/");

    }

    private static void log(Object s){
        System.out.println(s);
    }

    private static void info(Object s){
        LOG.info(s.toString());

    }

    class DisableTimeout implements HttpRequestInitializer {
        public void initialize(HttpRequest request) {
            request.setConnectTimeout(0);
            request.setReadTimeout(0);
        }
    }


}
