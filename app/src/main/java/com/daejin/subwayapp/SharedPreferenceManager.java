package com.daejin.subwayapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceManager {
    private static final String PREFERENCES = "Subway_Preferences";

    public static SharedPreferences getPreferences(Context mContext){
        return mContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void setLoginInfo(Context context, String email, String password){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public static Map<String, String> getLoginInfo(Context context){
        SharedPreferences prefs = getPreferences(context);
        Map<String, String> LoginInfo = new HashMap<>();
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");

        LoginInfo.put("email", email);
        LoginInfo.put("password", password);

        return LoginInfo;
    }
}
