package com.daejin.subwayapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.adapters.CommentAdapter;
import com.daejin.subwayapp.list.CommentList;
import com.daejin.subwayapp.utils.ProgressDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressDialog customProgressDialog;

    ArrayList<CommentList> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ImageView iv_uAvatar, iv_pImage;
    TextView tv_uName, tv_pTime, tv_pTitle, tv_pDescription, tv_pLikes, tv_pComment;
    ImageButton ibtn_pMore;
    Button btn_pLikes;
    LinearLayout layout_profile;
    RecyclerView recyclerview_commentlist;

    ImageView iv_commentAv;
    EditText et_comment;
    ImageButton ibtn_sendComment;

    String myUid, myEmail, myName, myAv;
    String pImage, pLikes, postId, hisAv, hisName, hisUid;

    boolean mProcessComment = false;
    boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initId();
        initToolbar();
        setCustomProgressDialog();
        initFirebase();
        getPostId();
        loadPostInfo();
        loadUserInfo();
        setLikes();
        loadComments();

        btn_pLikes.setOnClickListener(onClickListener);
        ibtn_sendComment.setOnClickListener(onClickListener);
        ibtn_pMore.setOnClickListener(onClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_common, menu);

        return super.onCreateOptionsMenu(menu);
    }


    private void initId() {
        iv_uAvatar = findViewById(R.id.iv_uAvatar);
        iv_pImage = findViewById(R.id.iv_pImage);
        tv_uName = findViewById(R.id.tv_uName);
        tv_pTime = findViewById(R.id.tv_pTime);
        tv_pTitle = findViewById(R.id.tv_pTitle);
        tv_pDescription = findViewById(R.id.tv_pDescription);
        tv_pLikes = findViewById(R.id.tv_pLikes);
        tv_pComment = findViewById(R.id.tv_pComment);
        ibtn_pMore = findViewById(R.id.ibtn_pMore);
        btn_pLikes = findViewById(R.id.btn_pLikes);
        layout_profile = findViewById(R.id.layout_profile);
        recyclerview_commentlist = findViewById(R.id.recyclerview_commentlist);

        iv_commentAv = findViewById(R.id.iv_commentAv);
        et_comment = findViewById(R.id.et_comment);
        ibtn_sendComment = findViewById(R.id.ibtn_sendComment);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.layout_toolBar_common);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setCustomProgressDialog() {
        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        myEmail = user.getEmail();
        myUid = user.getUid();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void getPostId() {
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String pTitle = "" + ds.child("pTitle").getValue();
                    String pDescr = "" + ds.child("pDescr").getValue();
                    pLikes = "" + ds.child("pLikes").getValue();
                    String pTimeStamp = "" + ds.child("pTime").getValue();
                    pImage = "" + ds.child("pImage").getValue();
                    hisAv = "" + ds.child("uDp").getValue();
                    hisUid = "" + ds.child("uid").getValue();
                    String uEmail = "" + ds.child("uEmail").getValue();
                    hisName = "" + ds.child("uName").getValue();
                    getSupportActionBar().setTitle(hisName + " 님의 게시물");
                    String commentCount = "" + ds.child("pComments").getValue();

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = DateFormat.format("yyyy/MM/dd hh: mm aa", calendar).toString();

                    tv_pTitle.setText(pTitle);
                    tv_pDescription.setText(pDescr);
                    tv_pLikes.setText(pLikes + " 명이 좋아요");
                    tv_pTime.setText(pTime);
                    tv_pComment.setText(commentCount + " 댓글 수");
                    tv_uName.setText(hisName);

                    if (pImage.equals("noImage")) {
                        iv_pImage.setVisibility(View.GONE);
                    } else {
                        iv_pImage.setVisibility(View.VISIBLE);
                        try {
                            Picasso.get().load(pImage).into(iv_pImage);
                        } catch (Exception e) {

                        }
                    }

                    try {
                        Picasso.get().load(hisAv).placeholder(R.drawable.ic_default_avatargreen).into(iv_uAvatar);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_avatargreen).into(iv_uAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserInfo() {
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    myName = "" + ds.child("name").getValue();
                    myAv = "" + ds.child("image").getValue();

                    try {
                        Picasso.get().load(myAv).placeholder(R.drawable.ic_default_avatargreen).into(iv_commentAv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_avatargreen).into(iv_commentAv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes() {
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).hasChild(myUid)) {
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

    private void loadComments() {
        commentAdapter = new CommentAdapter(myUid, postId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerview_commentlist.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CommentList list = ds.getValue(CommentList.class);
                    recyclerview_commentlist.setAdapter(commentAdapter);
                    commentList.add(list);
                    commentAdapter.setcList(commentList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.ibtn_sendComment) {
                postComment();
            } else if (view.getId() == R.id.btn_pLikes) {
                likePost();
            } else if (view.getId() == R.id.ibtn_pMore) {
                showMoreOptions();
            }
        }
    };

    private void postComment() {
        customProgressDialog.show();
        String comment = et_comment.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            startToast("한 글자 이상의 댓글을 입력해주세요.");
        } else {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
                    .child("Comments");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cId", timeStamp);
            hashMap.put("comment", comment);
            hashMap.put("timeStamp", timeStamp);
            hashMap.put("uid", myUid);
            hashMap.put("uEmail", myEmail);
            hashMap.put("uDp", myAv);
            hashMap.put("uName", myName);

            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            customProgressDialog.dismiss();
                            startToast("댓글 추가 완료");
                            updateCommentCount();
                            et_comment.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            customProgressDialog.dismiss();
                            startToast(e.getMessage());
                        }
                    });
        }
    }

    private void likePost() {
        int int_pLikes = Integer.parseInt(pLikes);
        mProcessLike = true;
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessLike) {
                    if (snapshot.child(postId).hasChild(myUid)) {
                        postsRef.child(postId).child("pLikes").setValue("" + (int_pLikes - 1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;
                    } else {
                        postsRef.child(postId).child("pLikes").setValue("" + (int_pLikes + 1));
                        likesRef.child(postId).child(myUid).setValue("Liked");
                        mProcessLike = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCommentCount() {
        mProcessComment = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessComment) {
                    String comments = "" + snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue("" + newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(this, ibtn_pMore, Gravity.END);
        if (hisUid.equals(myUid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "삭제");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "수정");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0) {
                    beginDelete();
                } else if (id == 1) {
                    Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", postId);
                    startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete() {
        if (pImage.equals("noImage")) {
            customProgressDialog.show();
            deleteWithoutImage();
            onBackPressed();
        } else {
            customProgressDialog.show();
            deleteWithImage();
            onBackPressed();
        }
    }

    private void deleteWithImage() {
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
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

    private void deleteWithoutImage() {
        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
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

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}