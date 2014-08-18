package com.playpalgames.library;

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.menu.Action;
import com.menu.Menu;
import com.menu.MenuItem;

import java.io.IOException;

public class MyClass {

    public static void main(String[] args){
       final boolean LOCAL_SERVER=true;


        final GameControllerImpl gc;
        try {

         gc= new GameControllerImpl( new NetHttpTransport(),new GsonFactory(),new MockUserFinder(),LOCAL_SERVER);



         } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        // Create menus
        final Menu mainMenu = new Menu(5);


        MenuItem[] menuItems= new MenuItem[]{
                new MenuItem("create match",new Action() {
                    @Override
                    public void run() {
                        try{
                           gc.createMatch();
                        } catch (IOException e) {
                            log(e.getMessage());

                            e.printStackTrace();
                        }
                        mainMenu.run();
                    }
                }),

                new MenuItem("join match",new Action() {
                    @Override
                    public void run() {
                        try{
                            gc.joinMatch(5066549580791808l );


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
                           gc.sendTurn("turn data");


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
                          gc.listTurns();
                        } catch (IOException e) {
                            log(e.getMessage());

                            e.printStackTrace();
                        }
                        mainMenu.run();
                    }
                }),

//                new MenuItem( "Register User", new Action() {
//                    @Override
//                    public void run() {
//
//
//                        try{
//                           User user1= gameEndpoint.register(user).execute();
//                            log(user);
//                            log(user1);
//                        } catch (IOException e) {
//                            log(e.getMessage());
//
//                            e.printStackTrace();
//                        }
//                        mainMenu.run();
//                    }
//                }),

                new MenuItem( "Exit", new Action() {
                    @Override
                    public void run() {

                    }
                }),

        };


        mainMenu.addItem( menuItems);




        mainMenu.run();


    }

    private static void localServer(AbstractGoogleClient.Builder builder, AbstractGoogleClient.Builder builderReg) {
        builder.setRootUrl("http://localhost:8080/_ah/api/");
        builderReg.setRootUrl("http://localhost:8080/_ah/api/");

    }

    private static void log(Object s){
        System.out.println(s);
    }
}
