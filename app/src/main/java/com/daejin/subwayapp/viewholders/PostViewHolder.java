package com.daejin.subwayapp.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.activity.AddPostActivity;
import com.daejin.subwayapp.activity.OtherUserProfile;
import com.daejin.subwayapp.list.PostList;
import com.daejin.subwayapp.utils.ProgressDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class PostViewHolder extends RecyclerView.ViewHolder {

    ProgressDialog customProgressDialog;

    CardView cardView;
    ImageView iv_uAvatar, iv_pImage;
    TextView tv_uName, tv_pTime, tv_pTitle, tv_pDescription, tv_pLikes;
    ImageButton ibtn_pMore;
    Button btn_pLikes, btn_pComment, btn_pShare;

    Context context;
    String uid;
    String myUid;
    String pId;
    String pImage;
    String pLikes;
    LinearLayout layout_profile;

    private DatabaseReference likesRef;
    private DatabaseReference postsRef;

    boolean mProcessLike = false;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.cardView);
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
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        context = itemView.getContext();
        setCustomProgressDialog();
    }

    public void onBind(PostList postList) {
        uid = postList.getUid();
        String uEmail = postList.getuEmail();
        String uName = postList.getuName();
        String uDp = postList.getuDp();
        pId = postList.getpId();
        String pTitle = postList.getpTitle();
        String pDescr = postList.getpDescr();
        pImage = postList.getpImage();
        String pTimeStamp = postList.getpTime();
        pLikes = postList.getpLikes();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("yyyy/MM/dd hh: mm aa", calendar).toString();

        tv_uName.setText(uName);
        tv_pTime.setText(pTime);
        tv_pTitle.setText(pTitle);
        tv_pDescription.setText(pDescr);
        tv_pLikes.setText(pLikes + "명이 좋아요");

        setLikes(pId);

        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_avatargreen).into(iv_uAvatar);
        } catch (Exception e) {

        }

        if (pImage.equals("noImage")) {
            iv_pImage.setVisibility(View.GONE);
        } else {
            iv_pImage.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(pImage).into(iv_pImage);
            } catch (Exception e) {

            }
        }

        layout_profile.setOnClickListener(onClickListener);
        ibtn_pMore.setOnClickListener(onClickListener);
        btn_pLikes.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.layout_profile) {
                Context context = view.getContext();
                Intent intent = new Intent(context, OtherUserProfile.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            } else if (view.getId() == R.id.ibtn_pMore) {
                showMoreOptions(ibtn_pMore, uid, myUid, pId, pImage);
            } else if (view.getId() == R.id.btn_pLikes) {
                int int_pLikes = Integer.parseInt(pLikes);
                mProcessLike = true;
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mProcessLike) {
                            if (snapshot.child(pId).hasChild(myUid)) {
                                postsRef.child(pId).child("pLikes").setValue("" + (int_pLikes - 1));
                                likesRef.child(pId).child(myUid).removeValue();
                                mProcessLike = false;
                            } else {
                                postsRef.child(pId).child("pLikes").setValue("" + (int_pLikes + 1));
                                likesRef.child(pId).child(myUid).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    };

    private void setLikes(String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(myUid)) {
                    btn_pLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                    btn_pLikes.setText("완료!");
                } else {
                    btn_pLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_black, 0, 0, 0);
                    btn_pLikes.setText("좋아요");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMoreOptions(ImageButton ibtnPMore, String uid, String myUid, String pId, String pImage) {
        PopupMenu popupMenu = new PopupMenu(context, ibtnPMore, Gravity.END);
        if (uid.equals(myUid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "삭제");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "수정");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0) {
                    beginDelete(pId, pImage);
                } else if (id == 1) {
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        if (pImage.equals("noImage")) {
            customProgressDialog.show();
            deleteWithoutImage(pId);
            cardView.setVisibility(View.GONE);
        } else {
            customProgressDialog.show();
            deleteWithImage(pId, pImage);
            cardView.setVisibility(View.GONE);
        }
    }

    private void deleteWithImage(String pId, String pImage) {
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        startToast("삭제 완료");
                        customProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customProgressDialog.dismiss();
                startToast(e.getMessage());
            }
        });
    }

    private void deleteWithoutImage(String pId) {
        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                startToast("삭제 완료");
                customProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setCustomProgressDialog() {
        customProgressDialog = new ProgressDialog(context);
        customProgressDialog.setCancelable(false);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void startToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
