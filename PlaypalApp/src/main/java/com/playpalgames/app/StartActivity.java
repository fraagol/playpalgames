package com.playpalgames.app;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.playpalgames.backend.gameEndpoint.GameEndpoint;
import com.playpalgames.backend.gameEndpoint.model.User;
import com.playpalgames.library.GameController;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;


@EActivity(R.layout.activity_start)
public class StartActivity extends ActionBarActivity implements NumberSelectionDialogFragment.NumberDialogListener {

    public static final String EXTRA_MESSAGE = "message";
    public static final String DISPLAY_MESSAGE_ACTION ="com.playpalgames.app.DISPLAY_MESSAGE_ACTION";
    public static final String TURN_ACTION ="com.playpalgames.app.TURN_ACTION";

    private static boolean foreground=false;

    public static boolean isForeground(){
        return foreground;
    }

    public static final String PREFERENCE_LOCAL_SERVER="LOCAL_SERVER";
    public static final String  PREFERENCE_USER_NAME="USER_NAME";
    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "292303650809";


    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Demo";

    private GcmRegistrer gcmRegistrer;



    Context context;

    GameController gameController;


    @ViewById(R.id.serverCheckbox)
    CheckBox serverCheckBox;

    @ViewById(R.id.logTextView)
    TextView logTextView;
    String auxLog = "";

    SharedPreferences preferences;


    String[]numbersStringArray;
    private String userName;
    private List<User> users;

    @UiThread
    void log(String msg) {

        Date now = new Date();
        String strDate = sdfDate.format(now);
        msg= strDate+" "+msg;
        Log.e("###########@", msg);
        auxLog += msg + "\n";
        if (logTextView != null) {
            logTextView.setText(auxLog);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foreground=true;
        context = getApplicationContext();
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        registerReceiver(mHandleTurnReceiver,
                new IntentFilter(TURN_ACTION));

        initPreferences();
        initGcm();
       Bundle extras= getIntent().getExtras();
        if(extras!=null) {
           String[] command = extras.getStringArray("COMMAND");
            log(Arrays.deepToString(command));
            if (command != null) {
                log("processing from onCreate");
                processReceivedCommand(command);
            }
        }

    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        foreground=true;
        Bundle extras = intent.getExtras();
        log("on new intent");
        if(extras != null){
            String[] command = extras.getStringArray("COMMAND");
            log(Arrays.deepToString(command));
            if (command != null) {
                log("processing from onNewIntent");
                processReceivedCommand(command);
            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        foreground=false;
        unregisterReceiver(mHandleMessageReceiver);
        unregisterReceiver(mHandleTurnReceiver);
    }
    @Override
    protected void onPause(){
        super.onPause();
        foreground=false;
    }



    private void initPreferences() {
        preferences= getSharedPreferences(StartActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            userName= getPreferenceString(PREFERENCE_USER_NAME);
         if(userName==null){
             userName=Utils.getEmail(this);
             if(userName==null){
                 userName="Guest"+ new Random().nextInt(1000);
             }else{
                 userName=userName.substring(0,userName.indexOf("@"));
             }
         }
    }

    @AfterViews
    void afterViews(){
       populateFieldsFromPreferences();
        logTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    void populateFieldsFromPreferences(){

        serverCheckBox.setChecked(preferences.getBoolean(PREFERENCE_LOCAL_SERVER,false));
    }

    @Click
    void serverCheckbox(){

        editPreference(PREFERENCE_LOCAL_SERVER,serverCheckBox.isChecked());
    }

    private void editPreference(String key,Boolean value)
    {
        preferences.edit().putBoolean(key, value).commit();
    }

    private boolean getPreferenceBoolean(String key)
    {
        return preferences.getBoolean(key,false);
    }

    private String getPreferenceString(String key ){return preferences.getString(key,null);}

    private boolean isLocalServer(){
        return getPreferenceBoolean(PREFERENCE_LOCAL_SERVER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        foreground=true;

    }

    @Background
     void  initGcm(){
        gcmRegistrer = GcmRegistrer.instance(context,
                                            preferences,
                                            SENDER_ID);
        try {
           if (gcmRegistrer.checkAndRegisterGCM()) {
               sendRegistrationIdToBackend();
           } else{
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
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }




private boolean isRegistered(){
    return gcmRegistrer==null || gcmRegistrer.isRegistered();
}



    @Background
    void unregister() {
        if(gcmRegistrer.isRegistered()) {

            try {
                String removedId = gcmRegistrer.unregister();
                log("UNREGISTERED");

                sendUnregistrationIdToBackend(removedId);

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                logUnregister("ERROR UNREGISTERING");
                e.printStackTrace();
            }
        }
        else{
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
            PhoneNumberUtil s;
            User user= new User();
            user.setName(userName);
            user.setRegId(gcmRegistrer.getRegistrationId());
            user.setPhoneNumber(Utils.getPhoneNumber(this));
            gameController= GameController.createGameController(AndroidHttp.newCompatibleTransport(), new JacksonFactory(), user, isLocalServer());
            log("Connected");



        } catch (IOException e) {
            log(e.getMessage());

            e.printStackTrace();
        }

    }



    private void sendUnregistrationIdToBackend(String removedId) {
        GameEndpoint.Builder reg = new GameEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null)

                ;
        if (isLocalServer()){
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

        try{
            log("Sending invitation to match "+gameController.getMatchId()+" to user "+ users.get(index).getName());
        gameController.sendInvitation(users.get(index));
        } catch (IOException e) {
            log(e.getMessage());

            e.printStackTrace();
        }
    }

private void setBuilderToLocalServer(com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder builder
){
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
    void createGameButton(){
        try {
            log("creating match");
            gameController.createMatch();
            log("created match");
        users = gameController.listUsers();
            log("users retrieved");
       String[] userNames= new String[users.size()];
        for (int i=0; i< users.size();i++) {
            log("user " + i + " : " + users.get(i).getName());
            userNames[i]= users.get(i).getName();
            //numbersStringArray[i]=list.get(i).getPhoneNumber();
        }

        FragmentManager fm = getSupportFragmentManager();
        NumberSelectionDialogFragment numberSelectionDialogFragment = new NumberSelectionDialogFragment(userNames);
        numberSelectionDialogFragment.show(fm, "fragment_select_number");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.launchButton)
    void startGameActivity(){
        Intent myIntent = new Intent(StartActivity.this, AndroidLauncher.class);

        startActivity(myIntent);

    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    processReceivedCommand( intent.getExtras().getStringArray("COMMAND"));

                }
            };

    private final BroadcastReceiver mHandleTurnReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    gameController.addTurn(intent.getExtras().getStringArray("COMMAND"));

                }
            };

    private void processReceivedCommand(String[] command){
        switch (command[0].charAt(0)){
            case 'A'://challenge Accepted
                startGame();
                break;
            case 'C': //Challenge received
                showChallengeDialog(command[1],command[2]);
                break;
        }
        //Toast.makeText(context,newMessage, Toast.LENGTH_LONG).show();

    }

    @Background
 void acceptChallenge(String idMatch, String challengerName){
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
    void startGame(){
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
        for (int i=5;i>=0;i--){
            Thread.sleep(1000);
            log(i+"");
        }
        startGameActivity();
    }




    private void showChallengeDialog(final String challengerName, final String matchId){
        AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);

        builder.setTitle("Nuevo desafío");
        builder.setMessage(challengerName+ " te desafía a un duelo ¿aceptas?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                acceptChallenge(matchId,challengerName);
            }
        });

        builder.setNegativeButton("poo-poo-pooo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
