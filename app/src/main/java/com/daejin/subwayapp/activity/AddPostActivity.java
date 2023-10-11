package com.daejin.subwayapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.fragment.FragmentLogin;
import com.daejin.subwayapp.utils.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;

public class AddPostActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        toolbar = findViewById(R.id.layout_toolBar);
        firebaseAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        setTitle("게시물 올리기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}