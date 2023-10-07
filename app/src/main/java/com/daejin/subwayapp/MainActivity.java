package com.daejin.subwayapp;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private Fragment chart, sns;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNav);
        fragmentManager = getSupportFragmentManager();

        chart = new FragmentChart();
        fragmentManager.beginTransaction().replace(R.id.linearLayout, chart).commit();
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
    }

    private void switchChart(){
        fragmentManager.beginTransaction().hide(sns).commit();
        fragmentManager.beginTransaction().show(chart).commit();
    }

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

    public AlertDialog showProgressBar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progressbar);
        AlertDialog dialog = builder.create();

        return  dialog;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}