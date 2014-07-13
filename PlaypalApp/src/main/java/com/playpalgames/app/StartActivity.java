package com.playpalgames.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.playpalgames.backend.messaging.Messaging;
import com.playpalgames.backend.registration.Registration;
import com.playpalgames.backend.registration.model.User;
import com.playpalgames.game.gameEndpoint.GameEndpoint;
import com.playpalgames.game.gameEndpoint.model.Turn;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@EActivity(R.layout.activity_start)
public class StartActivity extends ActionBarActivity implements NumberSelectionDialogFragment.NumberDialogListener {

    public static final String EXTRA_MESSAGE = "message";

    public static final String PREFERENCE_LOCAL_SERVER="LOCAL_SERVER";
    public static final String  PREFERENCE_USER_NAME="USER_NAME";
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

    TextView mDisplay;

    AtomicInteger msgId = new AtomicInteger();
    Context context;

    @ViewById(R.id.button1)
    Button button1;

    @ViewById(R.id.serverCheckbox)
    CheckBox serverCheckBox;

    @ViewById(R.id.textView)
    TextView text;

    @ViewById(R.id.logTextView)
    TextView logTextView;
    String auxLog = "";

    SharedPreferences preferences;

    List<User> list;
    String[]numbersStringArray;
    private String userName;

    @UiThread
    void log(String msg) {
        Log.e("###########@", msg);
        auxLog += msg + "\n";
        if (logTextView != null) {
            logTextView.setText(auxLog);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        initPreferences();


        initGcm();

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
        sendTurnToBackend();
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


    @Click
    void button1() {
        unregister();
    }

    @Click
    void pingButton(){

        FragmentManager fm = getSupportFragmentManager();
        NumberSelectionDialogFragment numberSelectionDialogFragment = new NumberSelectionDialogFragment(numbersStringArray);
        numberSelectionDialogFragment.show(fm, "fragment_select_number");
    }


    @Click
    void buttonRegister() {
        if (isRegistered()) {
            log("Already Registered");

        } else {
            log("REGISTERING");
            initGcm();
            }
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
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        Registration.Builder reg = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                       ;

        if (isLocalServer()){
            setBuilderToLocalServer(reg);
        }
        Registration registration = reg.build();
        try {
            log("sending to backend");
            PhoneNumberUtil s;
            User user= new User();
            user.setName(userName);
            user.setRegId(gcmRegistrer.getRegistrationId());
            user.setPhoneNumber(Utils.getPhoneNumber(this));

            registration.register(user).execute();
         list=  registration.listDevices().execute().getItems();
            numbersStringArray= new String[list.size()];
            for (int i=0; i<list.size();i++) {
                log("user " + i + " : " + list.get(i).getName());
                numbersStringArray[i]=list.get(i).getPhoneNumber();
            }
            log("sent to backend");




        } catch (IOException e) {
            log(e.getMessage());

            e.printStackTrace();
        }

    }

    @Background
    public void sendTurnToBackend(){
        GameEndpoint.Builder builder= new GameEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
        if (isLocalServer()){
            setBuilderToLocalServer(builder);
        }
        GameEndpoint turnEndpoint=builder.build();
        log("Sending test turn");
        Turn turn = new Turn();
        turn.setPlayerId(223l);
        turn.setTurnData(new Integer(343434));
       try{
        turnEndpoint.insertTurn(turn).execute();
           log("turn sent");
    } catch (IOException e) {
        log(e.getMessage());

        e.printStackTrace();
    }
    }


    private void sendUnregistrationIdToBackend(String removedId) {
        Registration.Builder reg = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)

                ;
        if (isLocalServer()){
            setBuilderToLocalServer(reg);
        }

        Registration registration = reg.build();
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
    public void onNumberPicked(String number) {
        Messaging.Builder builder = new  Messaging.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver

//                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
//                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                                                       @Override
//                                                       public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                                                           abstractGoogleClientRequest.setDisableGZipContent(true);
//                                                       }
//                                                   }
//                )
//
                ;

  if (isLocalServer()){
      setBuilderToLocalServer(builder);
  }
        Messaging messaging=builder.build();
        try{

        messaging.sendPing(number, "minumero").execute();
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
}
