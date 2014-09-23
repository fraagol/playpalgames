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

    private static GameController GAME_CONTROLLER;
    Match match;
    User user;
    GameEndpoint gameEndpoint;
    public String message="";
    private ChallengesClient challengesClient;
    private long lastProcessedTurnId=-1;

    private GameClient gameClient=null;

    @Override
    public boolean isMyTurn() {
        return isMyTurn;
    }

    @Override
    public void setMyTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;
    }

    private boolean isMyTurn=false;

    @Override
    public boolean isHost() {
        return host;
    }

    @Override
    public void setHost(boolean host) {
        this.host = host;
    }

    private boolean host=false;

    private LinkedList<Turn> availablesTurns=null;



    public GameControllerImpl(HttpTransport httpTransport, JsonFactory jsonFactory, UserFinder finder, boolean localServer) throws IOException {
        GameEndpoint.Builder builder= new GameEndpoint.Builder(httpTransport,jsonFactory , new DisableTimeout());
        if (localServer) {
            localServer(builder, "");
        }
        gameEndpoint=builder.build();
        user= finder.getUser();
        user= gameEndpoint.register(user).execute();

    }

    public GameControllerImpl(HttpTransport httpTransport, JsonFactory jsonFactory, User userP, ChallengesClient challengesClient, boolean localServer, String buildString) throws IOException {
        GameEndpoint.Builder builder= new GameEndpoint.Builder(httpTransport,jsonFactory , new DisableTimeout());
        if (localServer) {
            localServer(builder,buildString);
        }
        gameEndpoint=builder.build();
        user= gameEndpoint.register(userP).execute();
        this.challengesClient = challengesClient;

    }

    private void initMatch(){
        availablesTurns= new LinkedList<Turn>();
    }
    @Override
    public void processCommand(String msg) throws IOException {
        String [] command= msg.split(" ");

        switch (command[0].charAt(0)){
            case 'A'://challenge Accepted
                setHost(true);
                setMyTurn(true);
                initMatch();
                challengesClient.challengeAccepted();

                break;
            case 'C': //Challenge received
                challengesClient.incomingChallenge(command[1], command[2]);
                break;
            case 'T'://Turns availables
                getTurnsFromServer();
                break;
        }
        }

    @Override
    public void getTurnsFromServer() throws IOException {
        List<Turn> turns= gameEndpoint.listTurnsFrom(getMatchId(),lastProcessedTurnId).execute().getItems();
        for (int i = turns.size(); i >0; i--) {
            availablesTurns.add(turns.get(i-1));
            lastProcessedTurnId = turns.get(i-1).getTurnNumber();
        }
        if(gameClient!=null){
            gameClient.turnAvailable();
}
    }

    @Override
    public <T extends GameTurn> T getNextTurn(T turnToPopulate){
        if (availablesTurns==null){
            return null;
        }
        Turn t= availablesTurns.poll();
        if(t!=null) {
        turnToPopulate.populateFromString(t.getTurnData());
            return turnToPopulate;
        }else
        {
            return null;
        }
    }

    @Override
    public Match createMatch() throws IOException {
        match= gameEndpoint.createMatch(user.getId()).execute();
        info(match);
        return match;
    }
    @Override
    public void joinMatch(Long matchId) throws IOException {
        match= gameEndpoint.joinMatch(user.getId(),matchId ).execute();
        log(match);
    }

    @Override
    public <T extends GameTurn> void sendTurn(T o) throws IOException {
        Turn turn = new Turn();
        turn.setMatchId(match.getId());
        turn.setPlayerId(user.getId());
        turn.setTurnData(o.dataToString());


        Turn t =  gameEndpoint.insertTurn(turn).execute();
        lastProcessedTurnId=t.getTurnNumber();
        log("Sent turn: "+t);
    }

    @Override
    public List<Turn> listTurns() throws IOException {
        List<Turn> turns=  gameEndpoint.listTurns(match.getId()).execute().getItems();
        for(Turn turnN:turns){

            log(turnN);
        }
        return turns;
    }

//    @Override
//    public List<Turn> listTurnsFromNumber(Long turnNumber) throws IOException {
//        List<Turn> turns=  gameEndpoint.listTurnsFrom(match.getId(),turnNumber).execute().getItems();
//        for(Turn turnN:turns){
//
//            log(turnN);
//        }
//        return turns;
//    }

    @Override
    public List<User> listUsers() throws IOException {
        List<User> users=  gameEndpoint.listDevices().execute().getItems();
        return  users;
    }

    @Override
    public void sendInvitation(User userToInvite) throws IOException{
        log("Sending invitation to match "+match.getId()+" to user "+ userToInvite.getName());
        gameEndpoint.sendMessage("C "+ user.getName()+" "+ getMatchId(),userToInvite.getRegId()).execute();
    }

    @Override
    public void acceptChallenge(String matchId)throws IOException{
        match=gameEndpoint.joinMatch(user.getId(),Long.valueOf(matchId)).execute();
        setHost(false);
        setMyTurn(false);
        initMatch();
    }



    @Override
    public Long getMatchId(){
        return  match!=null? match.getId():null;
    }

    @Override
    public void addGameClientListener(GameClient gameClient) {
        this.gameClient=gameClient;
    }

    private void localServer(AbstractGoogleClient.Builder builder, String buildString) {
        //  builder.setRootUrl("http://localhost:8080/_ah/api/");
        if(buildString.matches(".*_?sdk_?.*")){
            builder.setRootUrl("http://10.0.2.2:8080/_ah/api/");
        }else{
            builder.setRootUrl("http://192.168.1.16:8080/_ah/api/");
        }


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
