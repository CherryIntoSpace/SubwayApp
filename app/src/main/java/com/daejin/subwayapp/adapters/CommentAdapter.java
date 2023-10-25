package com.daejin.subwayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.list.CommentList;
import com.daejin.subwayapp.viewholders.CommentViewHolder;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter {

    private ArrayList<CommentList> cList = new ArrayList<>();
    private Context context;
    String myUid, postId;

    public CommentAdapter(String myUid, String postId) {
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.comment_list, parent, false);
        CommentViewHolder commentViewHolder = new CommentViewHolder(view);

        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        commentViewHolder.onBind(cList.get(position), myUid, postId);
    }

    public void setcList(ArrayList<CommentList> list) {
        this.cList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }
}
