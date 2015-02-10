package com.playpalgames.app;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.playpalgames.app.game.KickGameActivity_;
import com.playpalgames.backend.gameEndpoint.GameEndpoint;
import com.playpalgames.backend.gameEndpoint.model.Match;
import com.playpalgames.backend.gameEndpoint.model.User;
import com.playpalgames.library.ChallengesClient;
import com.playpalgames.library.GameController;

import org.acra.ACRA;
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

@EActivity(R.layout.activity_start)
public class StartActivity extends ActionBarActivity implements ChallengesClient, NumberSelectionDialogFragment.NumberDialogListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String DISPLAY_MESSAGE_ACTION = "com.playpalgames.app.DISPLAY_MESSAGE_ACTION";

    private static boolean foreground = false;

    public static boolean isForeground() {
        return foreground;
    }

    public static final String PREFERENCE_LOCAL_SERVER = "LOCAL_SERVER";
    public static final String PREFERENCE_PENALTY_KICKS = "PENALTY_KICKS";
    public static final String PREFERENCE_USER_NAME = "USER_NAME";
    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy

    public static final int GAME_KICKS = 0;
    public static final int GAME_BANG = 1;
    public static final String[] GAME_NAMES = {"Kicks", "Bang"};
    public static final int[] GAME_IMAGES_ID = {R.drawable.ball, R.drawable.revolver_render};

    public int selectedGame = 0;


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

    List<Match> pendingGames = new ArrayList<Match>();

    @ViewById(R.id.serverCheckbox)
    CheckBox serverCheckBox;

    @ViewById(R.id.pkCheckbox)
    CheckBox pkCheckbox;


    @ViewById(R.id.logTextView)
    TextView logTextView;
    String auxLog = "";

    @ViewById(R.id.createKicksGameButton)
    ImageButton createKicksGameButton;

    @ViewById(R.id.createBangGameButton)
    ImageButton createBangGameButton;


    @ViewById(R.id.pendingGamesListView)
    ListView pendingGamesListView;

    @ViewById(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;

    SharedPreferences preferences;

    private String userName;
    private List<User> users;

    @UiThread
    void log(String msg) {

        Date now = new Date();
        String strDate = sdfDate.format(now);
        msg = strDate + " " + msg;
        Log.i("###########@", msg);
        auxLog += msg + "\n";
        if (logTextView != null) {
            logTextView.setText(auxLog);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ACRA.init(this.getApplication());
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
                    E.manage(e);
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

    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
        if (gameController != null) {
            swipeLayout.setRefreshing(true);
            reloadPendingGames();
        }
    }


    private void initPreferences() {
        preferences = getSharedPreferences(StartActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        userName = getPreferenceString(PREFERENCE_USER_NAME);
        if (userName == null) {
            userName = Utils.getEmail(this);
            if (userName == null) {
                userName = "Guest" + new Random().nextInt(1000);
            } else {
                userName = userName.substring(0, userName.indexOf("@"));
            }
            editPreference(PREFERENCE_USER_NAME, userName);
        }
    }

    @AfterViews
    void afterViews() {
        populateFieldsFromPreferences();
        logTextView.setMovementMethod(new ScrollingMovementMethod());


        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    void populateFieldsFromPreferences() {

        //   serverCheckBox.setChecked(preferences.getBoolean(PREFERENCE_LOCAL_SERVER, false));
        //   pkCheckbox.setChecked(preferences.getBoolean(PREFERENCE_PENALTY_KICKS, false));
    }

    @Click
    void serverCheckbox() {

        editPreference(PREFERENCE_LOCAL_SERVER, serverCheckBox.isChecked());
    }

    @Click
    void pkCheckbox() {

        editPreference(PREFERENCE_PENALTY_KICKS, pkCheckbox.isChecked());
    }

    private void editPreference(String key, Boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    private void editPreference(String key, String value) {
        preferences.edit().putString(key, value).commit();
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

    private boolean isPk() {
        return getPreferenceBoolean(PREFERENCE_PENALTY_KICKS);
    }




    @Background
    void reloadPendingGames(){

        retrievePendingGames();
        notifyPendingGamesAdapter();
    }

    @UiThread
    void notifyPendingGamesAdapter(){
        processPendingGames();
        ((PendingGamesAdapter)pendingGamesListView.getAdapter()).notifyDataSetChanged();
        swipeLayout.setRefreshing(false);
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
            E.manage(e);
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
                E.manage(e);
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
            E.manage(e);
            log(e.getMessage());

            e.printStackTrace();
        }

    }

    private void retrievePendingGames() {
        try {
            pendingGames.clear();
           List<Match> matchList = gameController.retrievePendingGames();
            if (matchList!=null) {
                pendingGames.addAll(matchList);
            }

        } catch (IOException e) {
            E.manage(e);
            log(e.getMessage());
            e.printStackTrace();
        }

    }

    @UiThread
    void processPendingGames() {
        if (pendingGames != null) {

            for (Match pendingGame : pendingGames) {
            log(pendingGame.toString());
            }
            pendingGamesListView.setAdapter(new PendingGamesAdapter(this, R.layout.pendinggameitemlayout, pendingGames, gameController.getUser().getId()));

            pendingGamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Match match = ((Match) adapterView.getItemAtPosition(i));
                    //((PendingGamesAdapter) adapterView.getAdapter()).remove(match);
                    //((PendingGamesAdapter) adapterView.getAdapter()).notifyDataSetChanged();
                    gameController.setMatch(match);
                    selectedGame = match.getGameType();
                    switch (match.getStatus()) {
                        case GameController.STATUS_IN_GAME:
                            startGame();
                            break;
                        case GameController.STATUS_INVITATION_SENT:
                            if (match.getHostUserId().equals(gameController.getUser().getId())) {
                                toast("Esperando respuesta del rival");
                            } else {
                                incomingChallenge(match.getHostName(), String.valueOf(match.getId()), String.valueOf(match.getGameType()));
                            }

                            break;
                        case GameController.STATUS_INVITATION_ACCEPTED:
                            if (match.getHostUserId().equals(gameController.getUser().getId())) {
                                startGame();
                            } else {
                                toast("Esperando inicio del juego");
                            }

                            break;

                        case GameController.STATUS_HOST_FINISHED:
                        case GameController.STATUS_GUEST_FINISHED:
                            startGame();
                    }
                }
            });
            log("Pending games= " + pendingGames.size());
        }
    }


    @UiThread
    public void afterInitBackgroundProcess() {
        if (pendingCommand != null) {
            log("Processing pending command");
            try {
                gameController.processCommand(pendingCommand);
            } catch (IOException e) {
                E.manage(e);
                log(e.getMessage());
                e.printStackTrace();
            }
            pendingCommand = null;
        }

        processPendingGames();


        createBangGameButton.setVisibility(View.VISIBLE);
        createKicksGameButton.setVisibility(View.VISIBLE);

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
            E.manage(e);
            log(e.getMessage());

            e.printStackTrace();
        }

    }


    @Override
    @Background
    public void onNumberPicked(int index) {

        try {
            log("Sending invitation to match " + gameController.getMatchId() + " to user " + users.get(index).getName());
            gameController.createMatch(users.get(index), selectedGame);
        } catch (IOException e) {
            E.manage(e);
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


    @Click
    void createBangGameButton() {
        selectedGame = GAME_BANG;
        selectOpponent();
    }

    @Click
    void createKicksGameButton() {
        selectedGame = GAME_KICKS;
        selectOpponent();
    }


    @Background
    void selectOpponent() {
        try {

            users = gameController.listUsers();
            log("users retrieved");
            String[] userNames = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                log("user " + i + " : " + users.get(i).getName());
                userNames[i] = users.get(i).getName();

            }

            FragmentManager fm = getSupportFragmentManager();
            NumberSelectionDialogFragment numberSelectionDialogFragment = new NumberSelectionDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArray("names", userNames);
            numberSelectionDialogFragment.setArguments(bundle);
            numberSelectionDialogFragment.show(fm, "fragment_select_number");

        } catch (IOException e) {
            E.manage(e);
            e.printStackTrace();
        }
    }

    void startGame() {
        Intent myIntent = null;
        switch (selectedGame) {
            case GAME_BANG:
                myIntent = new Intent(StartActivity.this, AndroidLauncher.class);
                break;
            case GAME_KICKS:
                myIntent = new Intent(StartActivity.this, KickGameActivity_.class);
                break;

            default:
        }

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
            E.manage(e);
            log(e.getMessage());
        }

    }


    @Background
    void acceptChallenge(String idMatch, String challengerName, int gameType) {
        log("Has aceptado el desafío!");
        selectedGame = gameType;
        try {
            gameController.acceptChallenge(idMatch);
            gameCountdown();

        } catch (Exception e) {
            E.manage(e);
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
            E.manage(e);
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    @Override
    public void notifyNewTurn() {
        if (gameController != null) {
            swipeLayout.setRefreshing(true);
            reloadPendingGames();
        }
    }


    void gameCountdown() throws InterruptedException {

        log("el juego comenzará en... ");
        for (int i = 1; i >= 0; i--) {
            Thread.sleep(1000);
            log(i + "");
        }
        startGame();
    }


    @UiThread
    public void incomingChallenge(final String challengerName, final String matchId, final String gameType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
        final int gameTypeInt = Integer.parseInt(gameType);
        builder.setTitle("Nuevo desafío");
        builder.setMessage(challengerName + " te reta a " + GAME_NAMES[gameTypeInt] + " ¿aceptas?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                acceptChallenge(matchId, challengerName, gameTypeInt);
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



    @UiThread
    void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRefresh() {
        toast("refreshing...");
        reloadPendingGames();
    }
}
