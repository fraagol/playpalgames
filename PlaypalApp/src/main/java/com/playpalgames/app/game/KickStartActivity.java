package com.playpalgames.app.game;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.playpalgames.app.GcmRegistrer;
import com.playpalgames.app.NumberSelectionDialogFragment;
import com.playpalgames.app.R;
import com.playpalgames.app.Utils;
import com.playpalgames.backend.gameEndpoint.GameEndpoint;
import com.playpalgames.backend.gameEndpoint.model.Match;
import com.playpalgames.backend.gameEndpoint.model.User;
import com.playpalgames.library.ChallengesClient;
import com.playpalgames.library.GameController;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


@EActivity(R.layout.kick_activity_start)
public class KickStartActivity extends ActionBarActivity implements ChallengesClient, NumberSelectionDialogFragment.NumberDialogListener {

    public static final String EXTRA_MESSAGE = "message";
    public static final String DISPLAY_MESSAGE_ACTION = "com.playpalgames.app.DISPLAY_MESSAGE_ACTION";
    public static final String TURN_ACTION = "com.playpalgames.app.TURN_ACTION";

    private static boolean foreground = false;

    public static boolean isForeground() {
        return foreground;
    }

    public static final String PREFERENCE_LOCAL_SERVER = "LOCAL_SERVER";
    public static final String PREFERENCE_PENALTY_KICKS = "PENALTY_KICKS";
    public static final String PREFERENCE_USER_NAME = "USER_NAME";
    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "292303650809";


    /**
     * Tag used on log messages.
     */
    static final String TAG = "PLAYPAL";

    private GcmRegistrer gcmRegistrer;

    Context context;

    GameController gameController;

    String pendingCommand = null;

    List<Match>pendingGames;


    @ViewById(R.id.logTextView)
    TextView logTextView;
    String auxLog = "";

    @ViewById(R.id.createGameButton)
    ImageButton createGameButton;

    @ViewById(R.id.pendingGamesListView)
    ListView pendingGamesListView;

    SharedPreferences preferences;

    private String userName;
    private List<User> users;

    @UiThread
    void log(String msg) {

        Date now = new Date();
        String strDate = sdfDate.format(now);
        msg = strDate + " " + msg;
        Log.e("###########@", msg);
        auxLog += msg + "\n";
        if (logTextView != null) {
            logTextView.setText(auxLog);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foreground = true;
        context = getApplicationContext();
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));

        initPreferences();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String command = extras.getString("COMMAND");
            log(command);
            if (command != null) {
                pendingCommand = command;
                log("Keeping pending Command");


            }
        }
        initGcm();

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        foreground = true;
        Bundle extras = intent.getExtras();
        log("on new intent");
        if (extras != null) {
            String command = extras.getString("COMMAND");
            log(command);
            if (command != null) {
                log("processing from onNewIntent");
                try {
                    gameController.processCommand(command);
                } catch (IOException e) {
                    log(e.getMessage());
                    e.printStackTrace();
                }

            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        foreground = false;
        unregisterReceiver(mHandleMessageReceiver);
        //unregisterReceiver(mHandleTurnReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        foreground = false;
    }


    private void initPreferences() {
        preferences = getSharedPreferences(KickStartActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        userName = getPreferenceString(PREFERENCE_USER_NAME);
        if (userName == null) {
            userName = Utils.getEmail(this);
            if (userName == null) {
                userName = "Guest" + new Random().nextInt(1000);
            } else {
                userName = userName.substring(0, userName.indexOf("@"));
            }
        }
    }

    @AfterViews
    void afterViews() {

        populateFieldsFromPreferences();
        logTextView.setMovementMethod(new ScrollingMovementMethod());


    }

    void populateFieldsFromPreferences() {


    }

    private void editPreference(String key, Boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    private boolean getPreferenceBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    private String getPreferenceString(String key) {
        return preferences.getString(key, null);
    }

    private boolean isLocalServer() {
        return getPreferenceBoolean(PREFERENCE_LOCAL_SERVER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;

    }

    @Background
    void initGcm() {
        gcmRegistrer = GcmRegistrer.instance(context,
                preferences,
                SENDER_ID);
        try {
            if (gcmRegistrer.checkAndRegisterGCM()) {
                sendRegistrationIdToBackend();
            } else {
                finish();

            }
        } catch (IOException e) {
            //TODO: how to handle background exceptions
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_registerGCM:
                initGcm();
                return true;
            case R.id.action_unregisterGCM:
                unregister();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private boolean isRegistered() {
        return gcmRegistrer == null || gcmRegistrer.isRegistered();
    }


    @Background
    void unregister() {
        if (gcmRegistrer.isRegistered()) {

            try {
                String removedId = gcmRegistrer.unregister();
                log("UNREGISTERED");

                sendUnregistrationIdToBackend(removedId);

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                logUnregister("ERROR UNREGISTERING");
                e.printStackTrace();
            }
        } else {
            log("Not Registered");
        }

    }

    @UiThread
    void logUnregister(String s) {
        log(s);
    }


    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app.
     */
    private void sendRegistrationIdToBackend() {
        try {
            log("Connecting to backend");
            User user = new User();
            user.setName(userName);
            user.setRegId(gcmRegistrer.getRegistrationId());
            user.setPhoneNumber(Utils.getPhoneNumber(this));
            gameController = GameController.createGameController(AndroidHttp.newCompatibleTransport(), new JacksonFactory(), user, this, isLocalServer(), Build.PRODUCT);
            log("Connected");
            retrievePendingGames();

            afterInitBackgroundProcess();

        } catch (IOException e) {
            log(e.getMessage());

            e.printStackTrace();
        }

    }

    private void retrievePendingGames() throws IOException {
        pendingGames=gameController.retrievePendingGames();

    }

    @UiThread
    public void afterInitBackgroundProcess() {
        if (pendingCommand != null) {
            log("Processing pending command");
            try {
                gameController.processCommand(pendingCommand);
            } catch (IOException e) {
                log(e.getMessage());
                e.printStackTrace();
            }
            pendingCommand = null;
        }

        if (pendingGames!=null){
            List<String> pendingAux=new ArrayList<String>(pendingGames.size());
            for (int i = 0; i < pendingGames.size(); i++) {
                pendingAux.add(pendingGames.get(i).getHostName());
            }
            pendingGamesListView.setAdapter(new PendingGamesAdapter(this,R.layout.pendinggameitemlayout,  pendingGames) );

pendingGamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Match match=((Match) adapterView.getItemAtPosition(i));
        Toast.makeText(getApplicationContext(), match.getHostName(),Toast.LENGTH_LONG).show();
        ((PendingGamesAdapter) adapterView.getAdapter()).remove(match);
        ((PendingGamesAdapter) adapterView.getAdapter()).notifyDataSetChanged();
    }
});
            log("Pending games= "+pendingGames.size());
        }

        createGameButton.setVisibility(View.VISIBLE);



    }

    private void sendUnregistrationIdToBackend(String removedId) {
        GameEndpoint.Builder reg = new GameEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
        if (isLocalServer()) {
            setBuilderToLocalServer(reg);
        }

        GameEndpoint registration = reg.build();
        try {
            log("sending unregistration to backend");
            registration.unregister(removedId).execute();
            log("sent unregistration to backend");
        } catch (IOException e) {
            log(e.getMessage());

            e.printStackTrace();
        }

    }


    @Override
    @Background
    public void onNumberPicked(int index) {

        try {
            log("Sending invitation to match " + gameController.getMatchId() + " to user " + users.get(index).getName());
            gameController.createMatch(users.get(index));
        } catch (IOException e) {
            log(e.getMessage());

            e.printStackTrace();
        }
    }

    private void setBuilderToLocalServer(com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder builder
    ) {
        builder.setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                                       @Override
                                                       public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                                           abstractGoogleClientRequest.setDisableGZipContent(true);
                                                       }
                                                   }
                );
    }

    @Background
    @Click
    void createGameButton() {
        try {

            users = gameController.listUsers();
            log("users retrieved");
            String[] userNames = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                log("user " + i + " : " + users.get(i).getName());
                userNames[i] = users.get(i).getName();
                //numbersStringArray[i]=list.get(i).getPhoneNumber();
            }

            FragmentManager fm = getSupportFragmentManager();
            NumberSelectionDialogFragment numberSelectionDialogFragment = new NumberSelectionDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArray("names", userNames);
            numberSelectionDialogFragment.setArguments(bundle);
            numberSelectionDialogFragment.show(fm, "fragment_select_number");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void startGameActivity() {
        Intent myIntent = new Intent(KickStartActivity.this, GameActivity_.class);
        startActivity(myIntent);

    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    sendCommandToGameController(intent.getExtras().getString("COMMAND"));

                }
            };


    @Background
    void sendCommandToGameController(String command) {
        try {
            gameController.processCommand(command);
        } catch (IOException e) {
            log(e.getMessage());
        }

    }


    @Background
    void acceptChallenge(String idMatch, String challengerName) {
        log("Has aceptado el desafío!");
        try {
            gameController.acceptChallenge(idMatch);
            gameCountdown();

        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    @Background
    public void challengeAccepted() {
        log("Desafío aceptado!");
        try {
            gameCountdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    void gameCountdown() throws InterruptedException {

        log("el juego comenzará en... ");
        for (int i = 1; i >= 0; i--) {
            Thread.sleep(1000);
            log(i + "");
        }
        startGameActivity();
    }


    @UiThread
    public void incomingChallenge(final String challengerName, final String matchId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(KickStartActivity.this);

        builder.setTitle("Nuevo desafío");
        builder.setMessage(challengerName + " te desafía a un duelo ¿aceptas?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                acceptChallenge(matchId, challengerName);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
