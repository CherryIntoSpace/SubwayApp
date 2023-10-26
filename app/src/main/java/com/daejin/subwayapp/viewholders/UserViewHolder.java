package com.daejin.subwayapp.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.list.UserList;
import com.squareup.picasso.Picasso;

public class UserViewHolder extends RecyclerView.ViewHolder {

    ImageView iv_uAvatar;
    TextView tv_uName, tv_uEmail;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        iv_uAvatar = itemView.findViewById(R.id.iv_uAvatar);
        tv_uName = itemView.findViewById(R.id.tv_uName);
        tv_uEmail = itemView.findViewById(R.id.tv_uEmail);
    }

    public void onBind(UserList userList) {
        String userImage = userList.getImage();
        String userName = userList.getName();
        String userEmail = userList.getEmail();

        tv_uName.setText(userName);
        tv_uEmail.setText(userEmail);
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_default_avatargreen)
                    .into(iv_uAvatar);
        } catch (Exception e){

        }
    }
}
