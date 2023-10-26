package com.daejin.subwayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.list.UserList;
import com.daejin.subwayapp.viewholders.UserViewHolder;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<UserList> userLists;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.row_users, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(view);

        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserViewHolder userViewHolder = (UserViewHolder) holder;
        userViewHolder.onBind(userLists.get(position));
    }

    public void setuLists(ArrayList<UserList> userLists) {
        this.userLists = userLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userLists.size();
    }
}
