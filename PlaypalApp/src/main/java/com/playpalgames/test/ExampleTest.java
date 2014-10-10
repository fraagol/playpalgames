package com.playpalgames.test;

import android.test.InstrumentationTestCase;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.playpalgames.backend.gameEndpoint.GameEndpoint;
import com.playpalgames.backend.gameEndpoint.model.Match;
import com.playpalgames.backend.gameEndpoint.model.User;

import junit.framework.Assert;

import java.util.List;

/**
 * Created by javi on 25/09/2014.
 */
public class ExampleTest extends InstrumentationTestCase {


    public void test2() throws Exception {

        GameEndpoint.Builder builder = new GameEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(), new DisableTimeout());
        localServer(builder, "");
        GameEndpoint gameEndpoint = builder.build();
        User user = new User();
        user.setName("Test");
        user.setRegId("TestRegID");

        User user3 = new User();
        user3.setName("Test3");
        user3.setRegId("Tes3tRegID");
        user = gameEndpoint.register(user).execute();
        user3 = gameEndpoint.register(user3).execute();
        Match m = null;

//        mgameEndpoint.createMatch(user.getId()).execute();
//        p(user3.getId());
//        p(m.getId());
//        m = gameEndpoint.joinMatch(user3.getId(), m.getId()).execute();


        List<Match> matches= gameEndpoint.listGamesByPlayer(user.getId()).execute().getItems();
        Assert.assertNotNull("MATCHES IS NULL",matches);
        Assert.assertTrue(matches.size()>0);


    }

    private void p(Object o) {
        System.out.println(o.toString());
    }

    private void localServer(AbstractGoogleClient.Builder builder, String buildString) {
        //  builder.setRootUrl("http://localhost:8080/_ah/api/");
        if (buildString.matches(".*_?sdk_?.*")) {
            builder.setRootUrl("http://10.0.2.2:8080/_ah/api/");
        } else {
            builder.setRootUrl("http://192.168.251.1:8080/_ah/api/");
        }


    }
}

class DisableTimeout implements HttpRequestInitializer {
    public void initialize(HttpRequest request) {
        request.setConnectTimeout(0);
        request.setReadTimeout(0);
    }
}