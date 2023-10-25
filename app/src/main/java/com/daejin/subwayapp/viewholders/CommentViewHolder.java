package com.daejin.subwayapp.viewholders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.list.CommentList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    Context context;
    CardView cardView_comment;
    ImageView iv_commentlistAv;
    TextView tv_commentlistName, tv_comment, tv_ctime;

    String uid, postId, cid;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        cardView_comment = itemView.findViewById(R.id.cardView_comment);
        iv_commentlistAv = itemView.findViewById(R.id.iv_commentlistAv);
        tv_commentlistName = itemView.findViewById(R.id.tv_commentlistName);
        tv_comment = itemView.findViewById(R.id.tv_comment);
        tv_ctime = itemView.findViewById(R.id.tv_ctime);
    }

    public void onBind(CommentList commentList, String myUid, String postId) {
        uid = commentList.getUid();
        this.postId = postId;
        String name = commentList.getuName();
        String email = commentList.getuEmail();
        String image = commentList.getuDp();
        cid = commentList.getcId();
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

        cardView_comment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (myUid.equals(uid)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setTitle("댓글 삭제");
                    builder.setMessage("정말 지우시겠습니까?");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteComment(cid);
                        }
                    });
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }
                else {
                    startToast("자신이 쓴 댓글만 삭제할 수 있습니다.");
                }
                return true;
            }
        });
    }

    private void deleteComment(String cid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.child("Comments").child(cid).removeValue();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String comments = "" + snapshot.child("pComments").getValue();
                int newCommentVal = Integer.parseInt(comments) - 1;
                ref.child("pComments").setValue("" + newCommentVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
