package com.daejin.subwayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    LinearLayout linearLayout;

    private Fragment chart, sns;
    private FragmentManager fragmentManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        fragmentManager = getSupportFragmentManager();

        chart = new FragmentChart();
        fragmentManager.beginTransaction().replace(R.id.linearLayout, chart).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.bottom_chart){
                    if(sns != null) fragmentManager.beginTransaction().hide(sns).commit();
                    if(chart != null) {
                        fragmentManager.beginTransaction().show(chart).commit();
                        return true;
                    }
                }
                else if (itemId == R.id.bottom_sns){
                    if(chart != null) {
                        fragmentManager.beginTransaction().hide(chart).commit();
                    }
                    if (sns == null){
                        sns = new FragmentSNSLobby();
                        fragmentManager.beginTransaction().add(R.id.linearLayout, sns).commit();
                        return  true;
                    }
                    if(sns != null) {
                        fragmentManager.beginTransaction().show(sns).commit();
                        return true;
                    }
                }
                return false;
            }
        });
    }

}