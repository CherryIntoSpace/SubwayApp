package com.daejin.subwayapp.activity;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.adapters.PostAdapter;
import com.daejin.subwayapp.list.PostList;
import com.daejin.subwayapp.utils.ProgressDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileSettings extends AppCompatActivity {

    ProgressDialog customProgressDialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    Toolbar toolbar;

    ImageView iv_pAvatar, iv_pCover;
    TextView tv_pName, tv_pEmail;

    RecyclerView postsRecyclerView;
    ArrayList<PostList> postList = new ArrayList<>();
    PostAdapter postAdapter = new PostAdapter();

    String storagePath = "Users_Profile_Cover_Imgs/";

    String uid;

    FloatingActionButton floatingActionButton;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    String[] cameraPermissions;
    String[] storagePermissions;

    Uri image_uri;
    Uri downloadUri;

    String profileOrCoverPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        initToolbar();
        initFirebase();
        initId();
        setCustomProgressDialog();
        loadMyPosts();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        floatingActionButton.setOnClickListener(onClickListener);
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    tv_pName.setText(name);
                    tv_pEmail.setText(email);
                    try {
                        Picasso.get().load(image).fit().centerCrop().into(iv_pAvatar);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_avatar).into(iv_pAvatar);
                    }

                    try {
                        Picasso.get().load(cover).into(iv_pCover);
                    } catch (Exception e) {

                    }
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
            if (view.getId() == R.id.fbtn_editProfile) {
                showEditProfileDialog();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_profile, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(600);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchMyPosts(query);
                } else {
                    loadMyPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    searchMyPosts(newText);
                } else {
                    loadMyPosts();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        startToast("카메라 권한과 저장소 권한을 허용해주세요.");
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        startToast("저장소 권한을 허용해주세요.");
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void pickFromGallery() {
        Intent galleyIntent = new Intent(Intent.ACTION_PICK);
        galleyIntent.setType("image/*");
        galleryActivityResultLauncher.launch(galleyIntent);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "temp Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        image_uri = data.getData();
                        if (profileOrCoverPhoto.equals("image")) {
                            iv_pAvatar.setImageURI(image_uri);
                            uploadProfileCoverPhoto(image_uri);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            Query query = ref.orderByChild("uid").equalTo(uid);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String child = ds.getKey();
                                        snapshot.getRef().child(child).child("uDp").setValue(image_uri.toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String child = ds.getKey();
                                        if (snapshot.child(child).hasChild("Comments")) {
                                            String child1 = "" + snapshot.child(child).getKey();
                                            Query child2 = FirebaseDatabase.getInstance().getReference("Posts")
                                                    .child(child).child("Comments").orderByChild("uid").equalTo(uid);
                                            child2.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                        String child = ds.getKey();
                                                        snapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else if (profileOrCoverPhoto.equals("cover")) {
                            iv_pCover.setImageURI(image_uri);
                            uploadProfileCoverPhoto(image_uri);
                        }
                        startToast("프로필 설정 완료");
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (profileOrCoverPhoto.equals("image")) {
                            iv_pAvatar.setImageURI(image_uri);
                            uploadProfileCoverPhoto(image_uri);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            Query query = ref.orderByChild("uid").equalTo(uid);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String child = ds.getKey();
                                        snapshot.getRef().child(child).child("uDp").setValue(image_uri.toString());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        String child = ds.getKey();
                                        if (snapshot.child(child).hasChild("Comments")) {
                                            String child1 = "" + snapshot.child(child).getKey();
                                            Query child2 = FirebaseDatabase.getInstance().getReference("Posts")
                                                    .child(child).child("Comments").orderByChild("uid").equalTo(uid);
                                            child2.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                        String child = ds.getKey();
                                                        snapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else if (profileOrCoverPhoto.equals("cover")) {
                            iv_pCover.setImageURI(image_uri);
                            uploadProfileCoverPhoto(image_uri);
                        }
                        startToast("프로필 설정 완료");
                    }
                }
            }
    );

    private void showEditProfileDialog() {
        String options[] = {"프로필 사진 설정", "프로필 배경 설정", "이름 변경"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필 설정");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    profileOrCoverPhoto = "image";
                    showImagePicDialog();
                } else if (i == 1) {
                    profileOrCoverPhoto = "cover";
                    showImagePicDialog();
                } else if (i == 2) {
                    showNameUpdateDialog("name");
                }
            }
        });
        builder.create().show();
    }

    private void showNameUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(key + " 변경");
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        EditText editText = new EditText(this);
        editText.setHint(key + " 입력...");
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    customProgressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    customProgressDialog.dismiss();
                                    startToast("업데이트 완료");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismiss();
                                    startToast("" + e.getMessage());
                                }
                            });
                    if (key.equals("name")) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                        Query query = ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String child = ds.getKey();
                                    snapshot.getRef().child(child).child("uName").setValue(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String child = ds.getKey();
                                    if (snapshot.child(child).hasChild("Comments")) {
                                        String child1 = "" + snapshot.child(child).getKey();
                                        Query child2 = FirebaseDatabase.getInstance().getReference("Posts")
                                                .child(child).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    String child = ds.getKey();
                                                    snapshot.getRef().child(child).child("uName").setValue(value);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    startToast(key + "을(를) 입력해주세요.");
                }
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

    private void showImagePicDialog() {
        String options[] = {"카메라", "갤러리"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("사진 설정");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (i == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void uploadProfileCoverPhoto(Uri imageUri) {
        customProgressDialog.show();
        String filePathAndName = storagePath + "" + profileOrCoverPhoto + "_" + user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                downloadUri = uriTask.getResult();

                if (uriTask.isSuccessful()) {
                    HashMap<String, Object> results = new HashMap<>();
                    results.put(profileOrCoverPhoto, downloadUri.toString());
                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    customProgressDialog.dismiss();
                                    startToast("이미지 업데이트 완료");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    customProgressDialog.dismiss();
                                    startToast("에러 발생");
                                }
                            });
                } else {
                    customProgressDialog.dismiss();
                    startToast("에러 발생");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startToast(e.getMessage());
            }
        });
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        firebaseDatabase = getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.layout_toolBar_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("프로필");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initId() {
        iv_pAvatar = findViewById(R.id.iv_pAvatar);
        iv_pCover = findViewById(R.id.iv_pCover);
        tv_pName = findViewById(R.id.tv_pName);
        tv_pEmail = findViewById(R.id.tv_pEmail);
        floatingActionButton = findViewById(R.id.fbtn_editProfile);
        postsRecyclerView = findViewById(R.id.recyclerview_posts);
    }

    private void setCustomProgressDialog() {
        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void loadMyPosts() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PostList myPosts = ds.getValue(PostList.class);
                    postsRecyclerView.setAdapter(postAdapter);
                    postList.add(myPosts);
                    postAdapter.setpList(postList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchMyPosts(String searchQuery) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postsRecyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PostList myPosts = ds.getValue(PostList.class);
                    postsRecyclerView.setAdapter(postAdapter);
                    if (myPosts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            myPosts.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(myPosts);
                    }
                    postAdapter.setpList(postList);
                }
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