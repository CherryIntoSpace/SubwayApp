package com.daejin.subwayapp.viewholders;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.list.CommentList;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    Context context;
    ImageView iv_commentlistAv;
    TextView tv_commentlistName, tv_comment, tv_ctime;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        iv_commentlistAv = itemView.findViewById(R.id.iv_commentlistAv);
        tv_commentlistName = itemView.findViewById(R.id.tv_commentlistName);
        tv_comment = itemView.findViewById(R.id.tv_comment);
        tv_ctime = itemView.findViewById(R.id.tv_ctime);
    }

    public void onBind(CommentList commentList) {
        String uid = commentList.getUid();
        String name = commentList.getuName();
        String email = commentList.getuEmail();
        String image = commentList.getuDp();
        String cid = commentList.getcId();
        String comment = commentList.getComment();
        String timestamp = commentList.getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String cTime = DateFormat.format("yyyy/MM/dd hh: mm aa", calendar).toString();

        tv_commentlistName.setText(name);
        tv_comment.setText(comment);
        tv_ctime.setText(cTime);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_default_avatargreen).into(iv_commentlistAv);
        } catch (Exception e) {

        }
    }
}
