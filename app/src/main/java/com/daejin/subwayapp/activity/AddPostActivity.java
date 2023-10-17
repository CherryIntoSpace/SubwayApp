package com.daejin.subwayapp.activity;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.utils.ProgressDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    /*Firebase 관련*/
    FirebaseAuth firebaseAuth;
    DatabaseReference userDbReference;
    FirebaseUser user;

    /*Toolbar 지정*/
    Toolbar toolbar;

    /*로딩 다이얼로그*/
    ProgressDialog customProgressDialog;

    /*카메라와 저장소 권한 관련*/
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    String[] cameraPermissions;
    String[] storagePermissions;

    /*레이아웃 구성 요소*/
    EditText et_inputTitle, et_inputDescription;
    ImageView iv_inputPhoto;
    Button btn_upload;

    /*사용자 */
    String name, email, image;

    Uri image_uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        initFirebase();
        initToolbar();
        initId();
        setCustomProgressDialog();

        btn_upload.setOnClickListener(onClickListener);
        iv_inputPhoto.setOnClickListener(onClickListener);

        Query query = userDbReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    image = "" + ds.child("image").getValue();
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
            if (view.getId() == R.id.btn_upload) {
                String title = et_inputTitle.getText().toString().trim();
                String description = et_inputDescription.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    startToast("제목을 입력해주세요.");
                }
                else if (TextUtils.isEmpty(description)) {
                    startToast("내용을 입력해주세요.");
                }
                else if (image_uri == null) {
                    uploadData(title, description, "noImage");
                }
                else {
                    uploadData(title, description, String.valueOf(image_uri));
                }
            }
            if (view.getId() == R.id.iv_inputPhoto) {
                showImagePickDialog();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_upload, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initId() {
        et_inputTitle = findViewById(R.id.et_inputTitle);
        et_inputDescription = findViewById(R.id.et_inputDescription);
        iv_inputPhoto = findViewById(R.id.iv_inputPhoto);
        btn_upload = findViewById(R.id.btn_upload);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.layout_toolBar_upload);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("게시물 올리기");
        getSupportActionBar().setSubtitle(email);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        userDbReference = FirebaseDatabase.getInstance().getReference("Users");
        user = firebaseAuth.getCurrentUser();
        email = user.getEmail();
    }

    private void showImagePickDialog() {
        String[] options = {"카메라", "갤러리"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지 불러오기");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
                if (i == 1) {
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

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
        //startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraActivityResultLauncher.launch(intent);
        //startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        image_uri = data.getData();
                        iv_inputPhoto.setImageURI(image_uri);
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
                        iv_inputPhoto.setImageURI(image_uri);
                    }
                }
            }
    );

    private void uploadData(String title, String description, String uri) {
        customProgressDialog.show();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timeStamp;

        if (!uri.equals("noImage")) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid", user.getUid());
                                hashMap.put("uName", name);
                                hashMap.put("uEmail", user.getEmail());
                                hashMap.put("uDp", image);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pTitle", title);
                                hashMap.put("pDescr", description);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pTime", timeStamp);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                customProgressDialog.dismiss();
                                                startToast("게시물 업로드 완료");
                                                et_inputTitle.setText("");
                                                et_inputDescription.setText("");
                                                iv_inputPhoto.setImageURI(null);
                                                image_uri = null;
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                customProgressDialog.dismiss();
                                                startToast("게시물 업로드 실패");
                                            }
                                        });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            customProgressDialog.dismiss();
                            startToast(e.getMessage());
                        }
                    });
        } else {
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid", user.getUid());
            hashMap.put("uName", name);
            hashMap.put("uEmail", user.getEmail());
            hashMap.put("uDp", image);
            hashMap.put("pId", timeStamp);
            hashMap.put("pTitle", title);
            hashMap.put("pDescr", description);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timeStamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            customProgressDialog.dismiss();
                            startToast("게시물 업로드 완료");
                            et_inputTitle.setText("");
                            et_inputDescription.setText("");
                            iv_inputPhoto.setImageURI(null);
                            image_uri = null;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            customProgressDialog.dismiss();
                            startToast("게시물 업로드 실패");
                        }
                    });
        }
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        startToast("카메라와 저장소 권한이 필요합니다.");
                    }
                } else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        startToast("저장소 권한이 필요합니다.");
                    }
                }
            }
            break;
        }
    }

    private void setCustomProgressDialog() {
        customProgressDialog = new ProgressDialog(this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}