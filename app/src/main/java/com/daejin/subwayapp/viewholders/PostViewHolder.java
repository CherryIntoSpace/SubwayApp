package com.daejin.subwayapp.viewholders;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.list.PostList;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class PostViewHolder extends RecyclerView.ViewHolder {

    ImageView iv_uAvatar, iv_pImage;
    TextView tv_uName, tv_pTime, tv_pTitle, tv_pDescription, tv_pLikes;
    ImageButton ibtn_pMore;
    Button btn_pLikes, btn_pComment, btn_pShare;
    Context mContext;

    public String uid;
    public LinearLayout layout_profile;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        iv_uAvatar = itemView.findViewById(R.id.iv_uAvatar);
        iv_pImage = itemView.findViewById(R.id.iv_pImage);
        tv_uName = itemView.findViewById(R.id.tv_uName);
        tv_pTime = itemView.findViewById(R.id.tv_pTime);
        tv_pTitle = itemView.findViewById(R.id.tv_pTitle);
        tv_pDescription = itemView.findViewById(R.id.tv_pDescription);
        tv_pLikes = itemView.findViewById(R.id.tv_pLikes);
        ibtn_pMore = itemView.findViewById(R.id.ibtn_pMore);
        btn_pLikes = itemView.findViewById(R.id.btn_pLikes);
        btn_pComment = itemView.findViewById(R.id.btn_pComment);
        btn_pShare = itemView.findViewById(R.id.btn_pShare);
        layout_profile = itemView.findViewById(R.id.layout_profile);
    }

    public void onBind(PostList postList) {
        uid = postList.getUid();
        String uEmail = postList.getuEmail();
        String uName = postList.getuName();
        String uDp = postList.getuDp();
        String pId = postList.getpId();
        String pTitle = postList.getpTitle();
        String pDescr = postList.getpDescr();
        String pImage = postList.getpImage();
        String pTimeStamp = postList.getpTime();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("yyyy/MM/dd hh: mm aa", calendar).toString();

        tv_uName.setText(uName);
        tv_pTime.setText(pTime);
        tv_pTitle.setText(pTitle);
        tv_pDescription.setText(pDescr);


        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_avatargreen).into(iv_uAvatar);
        } catch (Exception e) {

        }

        if (pImage.equals("noImage")) {
            iv_pImage.setVisibility(View.GONE);
        } else {
            try {
                Picasso.get().load(pImage).into(iv_pImage);
            } catch (Exception e) {

            }
        }
    }
}
