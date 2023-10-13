package com.daejin.subwayapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.activity.AddPostActivity;
import com.daejin.subwayapp.activity.ProfileSettings;
import com.daejin.subwayapp.adapters.PostAdapter;
import com.daejin.subwayapp.list.PostList;
import com.daejin.subwayapp.utils.SharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentSNS extends Fragment {
    FragmentManager fragmentManager;
    AppCompatActivity activity;
    FirebaseAuth firebaseAuth;
    Toolbar toolbar;

    RecyclerView recyclerView;
    private ArrayList<PostList> list = new ArrayList<>();
    private PostAdapter postAdapter = new PostAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) requireActivity();
        fragmentManager = requireActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_sns, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.tool_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            userLogout();
            return true;
        } else if (item.getItemId() == R.id.action_addPost) {
            startActivity(new Intent(requireActivity(), AddPostActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_profileSetting) {
            startActivity(new Intent(requireActivity(), ProfileSettings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPosts() {
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
                startToast("" + error.getMessage());
            }
        });
    }

    public void downKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    private void userLogout() {
        firebaseAuth.signOut();
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

    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}