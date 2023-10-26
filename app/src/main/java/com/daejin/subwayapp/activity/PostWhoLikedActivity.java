package com.daejin.subwayapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.adapters.UserAdapter;
import com.daejin.subwayapp.list.UserList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostWhoLikedActivity extends AppCompatActivity {

    Toolbar toolbar;
    String postId;

    private RecyclerView recyclerView;
    private ArrayList<UserList> userLists;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_who_liked);

        getPostId();
        recyclerView = findViewById(R.id.recyclerview_userlist);
        initToolbar();
        userLists = new ArrayList<>();
        loadUser();
    }

    private void getPostId() {
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.layout_toolBar_common);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("좋아요를 누른 사람들");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void loadUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Likes");
        ref.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userLists.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String hisUid = "" + ds.getRef().getKey();
                    getUsers(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsers(String hisUid) {
        userAdapter = new UserAdapter();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserList userList = ds.getValue(UserList.class);
                            recyclerView.setAdapter(userAdapter);
                            userLists.add(userList);
                            userAdapter.setuLists(userLists);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}