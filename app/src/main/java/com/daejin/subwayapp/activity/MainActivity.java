package com.daejin.subwayapp.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.fragment.FragmentChart;
import com.daejin.subwayapp.fragment.FragmentSNSLobby;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.kakao.sdk.common.KakaoSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private Fragment chart, sns;
    private FragmentManager fragmentManager;
    private boolean isChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        bottomNavigationView = findViewById(R.id.bottomNav);
        fragmentManager = getSupportFragmentManager();

        chart = new FragmentChart();
        fragmentManager.beginTransaction().replace(R.id.linearLayout, chart).commit();
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        //getHashKey();
        KakaoSdk.init(this, "050ea31122a99c46deb34d493df1108d");
    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
    }

    NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_sns){
                switchSns();
                return true;
            }
            else if (itemId == R.id.bottom_chart){
                switchChart();
                return true;
            }
            return false;
        }
    };

    private void switchSns(){
        fragmentManager.beginTransaction().hide(chart).commit();
        if (sns == null){
            sns = new FragmentSNSLobby();
            fragmentManager.beginTransaction().add(R.id.linearLayout, sns).commit();
        }
        else {
            fragmentManager.beginTransaction().show(sns).commit();
        }
        isChart = false;
    }

    private void switchChart(){
        fragmentManager.beginTransaction().hide(sns).commit();
        fragmentManager.beginTransaction().show(chart).commit();
        isChart = true;
    }

    public OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (fragmentManager.getBackStackEntryCount() <= 0 || isChart) {
                showDialog(MainActivity.this);
            } else {
                fragmentManager.popBackStack();
            }
        }
    };

    public void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("종료");
        builder.setMessage("종료하시겠습니까?");
        builder.setPositiveButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setNegativeButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }
}