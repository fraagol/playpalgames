package com.playpalgames.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by javi on 30/05/2014.
 */
public class GcmRegistrer {

    //TODO: how to check this?
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String TAG = "GcmRegistrer";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static GcmRegistrer gcmRegistrer;

    private SharedPreferences sharedPreferences;

    private Context context;

    private GoogleCloudMessaging gcm;

    private String regid;

    private String senderId;


    private GcmRegistrer(Context context,SharedPreferences sharedPreferences, String senderId){
        this.sharedPreferences=sharedPreferences;
        this.context=context;
        this.senderId=senderId;
        gcm = GoogleCloudMessaging.getInstance(context);

    }

    
    public static GcmRegistrer instance(Context context,SharedPreferences sharedPreferences, String senderId)
    {
        if(gcmRegistrer==null){
        gcmRegistrer=new GcmRegistrer(context,sharedPreferences,senderId);
             }
        return gcmRegistrer;

    }

    public boolean checkAndRegisterGCM() throws IOException {
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId();

            if (regid.isEmpty()) {

                register();
            }
            return true;
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        return false;
    }

    private void register() throws IOException{
        regid = gcm.register(senderId);
        // Persist the regID - no need to register again.
        storeRegistrationId(context, regid);


    }


    public String unregister() throws IOException {
        gcm.unregister();
       String removedId= removeRegistrationId(context);
        return removedId;


    }



    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {

        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.

     */
    private String removeRegistrationId(Context context) {

       String registrationId=  sharedPreferences.getString(PROPERTY_REG_ID, "");
        Log.i(TAG, "Removing regId ");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.commit();
        return registrationId;
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //TODO: check how to implement this
//                GooglePlayServicesUtil.getErrorDialog(resultCode, context,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");

            }
            return false;
        }
        return true;
    }

    private String getRegistrationIdFromPreferences(){
      return  sharedPreferences.getString(PROPERTY_REG_ID, "");
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public String getRegistrationId() {
        
        String registrationId = getRegistrationIdFromPreferences();
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = sharedPreferences.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

public boolean isRegistered(){
    return !getRegistrationId().isEmpty();
}

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
