package com.daejin.subwayapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daejin.subwayapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FragmentSignup extends Fragment {

    private FirebaseAuth mAuth;
    FragmentManager fragmentManager;
    EditText et_Email;
    EditText et_Password;
    EditText et_Passwordcheck;
    Button btn_compSignup;
    Button btn_gotoLogin;

    String email;
    String password;
    String passwordCheck;

    String uid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getActivity().getSupportFragmentManager();
        et_Email = view.findViewById(R.id.emaileditText);
        et_Password = view.findViewById(R.id.passwordeditText);
        et_Passwordcheck = view.findViewById(R.id.passwordCheckeditText);
        btn_compSignup = view.findViewById(R.id.btn_compSignup);
        btn_gotoLogin = view.findViewById(R.id.btn_gotoLogin);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        btn_compSignup.setOnClickListener(onClickListener);
        btn_gotoLogin.setOnClickListener(onClickListener);
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_compSignup) {
                signUp();
            }
            else if (v.getId() == R.id.btn_gotoLogin){
                startLogInscreen();
            }
        }
    };

    private void signUp(){
        email = String.valueOf(et_Email.getText());
        password = String.valueOf(et_Password.getText());
        passwordCheck = String.valueOf(et_Passwordcheck.getText());

        if(!password.equals(passwordCheck)){
            startToast("비밀번호가 일치하지 않습니다.");
        }
        else{
            try {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    email = user.getEmail();
                                    uid = user.getUid();

                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("email", email);
                                    hashMap.put("uid", uid);
                                    hashMap.put("name", "");
                                    hashMap.put("image", "");
                                    hashMap.put("cover", "");

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(uid).setValue(hashMap);

                                    startToast("회원가입 완료! 로그인 창으로 이동합니다.");
                                    startLogInscreen();
                                }
                                else {
                                    String error = ((FirebaseAuthException)task.getException()).getErrorCode();
                                    switch (error){
                                        case "ERROR_INVALID_EMAIL":
                                            startToast("이메일 형식이 올바르지 않습니다.");
                                            break;
                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                            startToast("이미 사용 중인 이메일입니다.");
                                            break;
                                        case "ERROR_WEAK_PASSWORD":
                                            startToast("비밀번호는 최소 6 글자 이상부터 사용 가능합니다.");
                                            break;
                                    }
                                }
                            }
                        });
            }catch (IllegalArgumentException e){
                startToast("이메일이나 비밀번호를 모두 입력해주세요.");
            }
        }
    }

    private void startLogInscreen(){
        FragmentLogin fragmentLogin = new FragmentLogin();
        fragmentManager.beginTransaction().replace(R.id.signup_layout, fragmentLogin).commit();
    }

    private void startToast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}