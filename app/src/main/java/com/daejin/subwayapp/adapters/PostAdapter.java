package com.daejin.subwayapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.activity.OtherUserProfile;
import com.daejin.subwayapp.list.PostList;
import com.daejin.subwayapp.viewholders.PostViewHolder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter {

    private ArrayList<PostList> pList = new ArrayList<>();
    private Context context;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_postitem, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(view);

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostViewHolder postViewHolder = (PostViewHolder) holder;
        postViewHolder.onBind(pList.get(position));
    }


    @Override
    public int getItemCount() {
        return pList.size();
    }

    public void setpList(ArrayList<PostList> list) {
        this.pList = list;
        notifyDataSetChanged();
    }

}
