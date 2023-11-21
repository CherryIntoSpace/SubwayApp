package com.daejin.subwayapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceManager {
    private static final String PREFERENCES = "Subway_Preferences";
    private static final String TOPIC_EMERGENCY_NOFICATION = "EMERGENCY";
    private static final String TOPIC_DELAY_NOFICATION = "DELAY";

    public static SharedPreferences getPreferences(Context mContext) {
        return mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void setuId(Context context, String uId) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Current_USERID", uId);
        editor.apply();
    }

    public static String getuId(Context context) {
        SharedPreferences prefs = getPreferences(context);
        String uId = prefs.getString("Current_USERID", "None");
        return uId;
    }

    public static void setLoginInfo(Context context, String email, String password) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public static Map<String, String> getLoginInfo(Context context) {
        SharedPreferences prefs = getPreferences(context);
        Map<String, String> LoginInfo = new HashMap<>();
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");

        LoginInfo.put("email", email);
        LoginInfo.put("password", password);

        return LoginInfo;
    }

    public static void setEmergencyNofication(Context context, boolean isChecked) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("" + TOPIC_EMERGENCY_NOFICATION, isChecked);
        editor.apply();
    }

    public static boolean getEmergencyNofication(Context context) {
        SharedPreferences prefs = getPreferences(context);
        boolean isCheck = prefs.getBoolean("" + TOPIC_EMERGENCY_NOFICATION, false);
        return isCheck;
    }

    public static void setDelayNofication(Context context, boolean isChecked) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("" + TOPIC_DELAY_NOFICATION, isChecked);
        editor.apply();
    }

    public static boolean getDelayNofication(Context context) {
        SharedPreferences prefs = getPreferences(context);
        boolean isCheck = prefs.getBoolean("" + TOPIC_DELAY_NOFICATION, false);
        return isCheck;
    }
}
