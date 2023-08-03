package com.daejin.subwayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
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
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        fragmentManager = getSupportFragmentManager();
        user = FirebaseAuth.getInstance().getCurrentUser();

        chart = new FragmentChart();
        fragmentManager.beginTransaction().replace(R.id.linearLayout, chart).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
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
                    fragmentManager.beginTransaction().hide(chart).commit();
                    if (sns == null){
                        sns = new FragmentSNSLobby();
                        fragmentManager.beginTransaction().add(R.id.linearLayout, sns).commit();
                        return  true;
                    }
                    else {
                        fragmentManager.beginTransaction().show(sns).commit();
                        return true;
                    }
                }
                else if (itemId == R.id.bottom_sns && user != null){
                    skipLobby();
                }
                return false;
            }
        });
    }

    private void skipLobby(){
        FragmentSNS fragmentSNS = new FragmentSNS();
        fragmentManager.beginTransaction().replace(R.id.sns_main_layout, fragmentSNS).commit();
    }
}