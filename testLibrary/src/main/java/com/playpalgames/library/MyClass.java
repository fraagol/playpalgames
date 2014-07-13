package com.playpalgames.library;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.menu.Action;
import com.menu.Menu;
import com.menu.MenuItem;
import com.playpalgames.backend.registration.Registration;
import com.playpalgames.backend.registration.model.User;
import com.playpalgames.game.gameEndpoint.GameEndpoint;
import com.playpalgames.game.gameEndpoint.model.Match;
import com.playpalgames.game.gameEndpoint.model.Turn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyClass {

    public static void main(String[] args){

       final  User user= new User();
        user.setName("consola");
        user.setPhoneNumber("55555");
        user.setRegId("XXXXXXXXX");

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        GameEndpoint.Builder builder= new GameEndpoint.Builder(httpTransport,jsonFactory , null);
        Registration.Builder regBuilder= new Registration.Builder(httpTransport,jsonFactory , null);
       // builder.setRootUrl("http://localhost:8080/_ah/api/");

       final GameEndpoint gameEndpoint=builder.build();
        final Registration regEndpoint=regBuilder.build();
         // Create menus
        final Menu mainMenu = new Menu(5);


        MenuItem[] menuItems= new MenuItem[]{
                new MenuItem("create match",new Action() {
                    @Override
                    public void run() {
                        try{
                            Match match= gameEndpoint.createMatch().execute();
                            log(match);


                        } catch (IOException e) {
                            log(e.getMessage());

                            e.printStackTrace();
                        }
                        mainMenu.run();
                    }
                }),



                new MenuItem("send turn",new Action() {
                    @Override
                    public void run() {
                        try{
                            Turn turn = new Turn();
                            turn.setMatchId(1L);
                            turn.setPlayerId(223l);
                            List list= new ArrayList();
                            list.add("asdf");
                            list.add(2);
                            list.add(new Date());
                            turn.setTurnData(list);

                           Turn t =  gameEndpoint.insertTurn(turn).execute();
                           log(t);


                        } catch (IOException e) {
                            log(e.getMessage());

                            e.printStackTrace();
                        }
                        mainMenu.run();
                    }
                }),

                new MenuItem("list turns",new Action() {
                    @Override
                    public void run() {
                        try{
                            List<Turn> turns=  gameEndpoint.listTurns().execute().getItems();
                            for(Turn turnN:turns){

                                log(turnN);
                            }
                        } catch (IOException e) {
                            log(e.getMessage());

                            e.printStackTrace();
                        }
                        mainMenu.run();
                    }
                }),

                new MenuItem( "Register User", new Action() {
                    @Override
                    public void run() {


                        try{
                            regEndpoint.register(user).execute();
                            log(user);
                        } catch (IOException e) {
                            log(e.getMessage());

                            e.printStackTrace();
                        }
                        mainMenu.run();
                    }
                }),

                new MenuItem( "Exit", new Action() {
                    @Override
                    public void run() {

                    }
                }),

        };


        mainMenu.addItem( menuItems);




        mainMenu.run();


    }

    private static void log(Object s){
        System.out.println(s);
    }
}
