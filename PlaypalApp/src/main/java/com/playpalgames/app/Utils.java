package com.playpalgames.app;


        import java.util.regex.Pattern;

        import android.accounts.Account;
        import android.accounts.AccountManager;
        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.telephony.TelephonyManager;
        import android.util.Patterns;

public class Utils {

    public static void openApplication(String name, Activity activity) {
        Intent i = new Intent(Intent.ACTION_MAIN);

        i = activity.getPackageManager().getLaunchIntentForPackage(name);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        activity.startActivity(i);
    }

    public static String getPhoneNumber(Context context) {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return tm.getLine1Number();
    }

    public static String getEmail(Context context) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                return account.name;

            }
        }
        return null;
    }
}
