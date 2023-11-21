package com.daejin.subwayapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.activity.AddPostActivity;
import com.daejin.subwayapp.activity.NoficationActivity;
import com.daejin.subwayapp.activity.ProfileSettings;
import com.daejin.subwayapp.adapters.PostAdapter;
import com.daejin.subwayapp.list.PostList;
import com.daejin.subwayapp.utils.ProgressDialog;
import com.daejin.subwayapp.utils.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentSNS extends Fragment {
    FragmentManager fragmentManager;
    AppCompatActivity activity;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Toolbar toolbar;

    ProgressDialog customProgressDialog;
    RecyclerView recyclerView;
    private ArrayList<PostList> list = new ArrayList<>();
    private PostAdapter postAdapter = new PostAdapter();

    String uName = "";
    String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) requireActivity();
        fragmentManager = requireActivity().getSupportFragmentManager();
        setCustomProgressDialog();

        View view = inflater.inflate(R.layout.fragment_sns, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = firebaseAuth.getUid();
        SharedPreferenceManager.setuId(requireActivity(), uid);
        uName = checkUserName();

        toolbar = view.findViewById(R.id.layout_toolBar);
        activity.setSupportActionBar(toolbar);
        activity.setTitle("커뮤니티");

        recyclerView = view.findViewById(R.id.layout_recyclerView_SNS);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadPosts();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        downKeyboard();
    }

    @Override
    public void onResume() {
        super.onResume();
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.tool_bar, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(600);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchPosts(query);
                } else {
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    searchPosts(newText);
                } else {
                    loadPosts();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            userLogout();
            return true;
        } else if (item.getItemId() == R.id.action_addPost) {
            if (uName.length() > 0) {
                startActivity(new Intent(requireActivity(), AddPostActivity.class));
                return true;
            } else {
                startToast("닉네임 설정을 해주세요.");
                return false;
            }
        } else if (item.getItemId() == R.id.action_profileSetting) {
            startActivity(new Intent(requireActivity(), ProfileSettings.class));
            return true;
        } else if (item.getItemId() == R.id.action_nofication) {
            startActivity(new Intent(requireActivity(), NoficationActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String checkUserName() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference userRef = ref.child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uName = snapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return uName;
    }

    private void loadPosts() {
        customProgressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PostList postList = ds.getValue(PostList.class);
                    recyclerView.setAdapter(postAdapter);
                    list.add(postList);
                    postAdapter.setpList(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        customProgressDialog.dismiss();
    }

    private void searchPosts(String searchQuery) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PostList postList = ds.getValue(PostList.class);
                    recyclerView.setAdapter(postAdapter);

                    if (postList.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())
                            || postList.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {
                        list.add(postList);
                    }
                    postAdapter.setpList(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void downKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    private void userLogout() {
        recyclerView.setVisibility(View.GONE);
        firebaseAuth.signOut();
        list.clear();
        clearPreferences(requireActivity());
        startToast("로그아웃 되었습니다.");
        FragmentLogin fragmentLogin = new FragmentLogin();
        fragmentManager.beginTransaction().replace(R.id.sns_layout, fragmentLogin).commit();
        activity.getSupportActionBar().hide();
    }

    public static void clearPreferences(Context context) {
        SharedPreferences prefs = SharedPreferenceManager.getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private void setCustomProgressDialog() {
        customProgressDialog = new ProgressDialog(requireContext());
        customProgressDialog.setCancelable(false);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}