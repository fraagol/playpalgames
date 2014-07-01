package com.playpalgames.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.playpalgames.backend.registration.Registration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@EActivity(R.layout.activity_start)
public class StartActivity extends ActionBarActivity {

    public static final String EXTRA_MESSAGE = "message";


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

    @ViewById(R.id.textView)
    TextView text;

    @ViewById(R.id.logTextView)
    TextView logTextView;
    String auxLog = "";





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
        log("starting" + Utils.getPhoneNumber(this));
        initGcm();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Background
     void  initGcm(){
        gcmRegistrer = GcmRegistrer.instance(context,
                                            getSharedPreferences(StartActivity.class.getSimpleName(), Context.MODE_PRIVATE),
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

//    /**
//     * Registers the application with GCM servers asynchronously.
//     * <p/>
//     * Stores the registration ID and the app versionCode in the application's
//     * shared preferences.
//     */
//    private void registerInBackground() {
//        log("registering in background");
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                String msg = "";
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(context);
//                    }
//                    regid = gcm.register(SENDER_ID);
//                    msg = "Device registered, " + regid.substring(0, 10) + "...";
//
//                    // You should send the registration ID to your server over HTTP, so it
//                    // can use GCM/HTTP or CCS to send messages to your app.
//                    sendRegistrationIdToBackend();
//
//                    // For this demo: we don't need to send it because the device will send
//                    // upstream messages to a server that echo back the message using the
//                    // 'from' address in the message.
//
//                    } catch (IOException ex) {
//                    msg = "Error :" + ex.getMessage();
//                    // If there is an error, don't just keep trying to register.
//                    // Require the user to click a button again, or perform
//                    // exponential back-off.
//                }
//                return msg;
//            }
//
//            @Override
//            protected void onPostExecute(String msg) {
//                log("registered " + msg);
//                Log.i(TAG, msg + "\n");
////                mDisplay.append(msg + "\n");
//            }
//        }.execute(null, null, null);
//    }




    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        Registration.Builder reg = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver

                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                                       @Override
                                                       public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                                           abstractGoogleClientRequest.setDisableGZipContent(true);
                                                       }
                                                   }
                );

        Registration registration = reg.build();
        try {
            log("sending to backend");
            PhoneNumberUtil s;

            registration.register(gcmRegistrer.getRegistrationId(), Utils.getPhoneNumber(this)).execute();
            log("sent to backend");
        } catch (IOException e) {
            log(e.getMessage());

            e.printStackTrace();
        }

    }


    private void sendUnregistrationIdToBackend(String removedId) {
        Registration.Builder reg = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver

                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                                       @Override
                                                       public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                                           abstractGoogleClientRequest.setDisableGZipContent(true);
                                                       }
                                                   }
                );


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


    @Click
    void notifButton() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification").setAutoCancel(true)
                        .setContentText("Hello World!");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //  Intent resultIntent = new Intent(this, StartActivity_.class);
        Intent resultIntent = StartActivity_.intent(context).get();
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent p = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(p);
// mId allows you to update the notification later on.
        mNotificationManager.notify(7, mBuilder.build());
    }


}
