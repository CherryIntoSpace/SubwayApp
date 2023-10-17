package com.daejin.subwayapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.adapters.PostAdapter;
import com.daejin.subwayapp.list.PostList;
import com.daejin.subwayapp.utils.ProgressDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OtherUserProfile extends AppCompatActivity {

    Toolbar toolbar;
    ProgressDialog customProgressDialog;

    RecyclerView postsRecyclerView;
    ArrayList<PostList> postList = new ArrayList<>();
    PostAdapter postAdapter = new PostAdapter();

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String uid;

    ImageView iv_pAvatar, iv_pCover;
    TextView tv_pName, tv_pEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        initId();
        initFirebase();
        initToolbar();
        setCustomProgressDialog();
        loadProfile();
        loadOtherPosts();

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    tv_pName.setText(name);
                    tv_pEmail.setText(email);
                    try {
                        Picasso.get().load(image).fit().centerCrop().into(iv_pAvatar);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_avatar).into(iv_pAvatar);
                    }

                    try {
                        Picasso.get().load(cover).into(iv_pCover);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_profile, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(600);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchOtherPosts(query);
                } else {
                    loadOtherPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    searchOtherPosts(newText);
                } else {
                    loadOtherPosts();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initId() {
        postsRecyclerView = findViewById(R.id.recyclerview_posts);
        iv_pAvatar = findViewById(R.id.iv_pAvatar);
        iv_pCover = findViewById(R.id.iv_pCover);
        tv_pName = findViewById(R.id.tv_pName);
        tv_pEmail = findViewById(R.id.tv_pEmail);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.layout_toolBar_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("프로필");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadProfile() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
    }

    private void setCustomProgressDialog() {
        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void loadOtherPosts() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PostList otherPosts = ds.getValue(PostList.class);
                    postsRecyclerView.setAdapter(postAdapter);
                    postList.add(otherPosts);
                    postAdapter.setpList(postList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchOtherPosts(String searchQuery) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PostList otherPosts = ds.getValue(PostList.class);
                    postsRecyclerView.setAdapter(postAdapter);
                    if (otherPosts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            otherPosts.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(otherPosts);
                    }
                    postAdapter.setpList(postList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}