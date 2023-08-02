package com.daejin.subwayapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class FragmentLogin extends Fragment {
    private FirebaseAuth mAuth;
    FragmentManager fragmentManager;
    EditText inputEmail;
    EditText inputPassword;
    Button inputLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        inputEmail = view.findViewById(R.id.emaileditText);
        inputPassword = view.findViewById(R.id.passwordeditText);
        inputLogin = view.findViewById(R.id.btn_inputLogin);

        inputLogin.setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_inputLogin) {
                logIn();
            }
        }
    };

    private void logIn(){
        String email = String.valueOf(inputEmail.getText());
        String password = String.valueOf(inputPassword.getText());

        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인에 성공하였습니다!");
                                successLogin();
                            } else {
                                String error = ((FirebaseAuthException)task.getException()).getErrorCode();
                                switch (error){
                                    case "ERROR_INVALID_EMAIL":
                                        startToast("이메일 형식이 올바르지 않습니다.");
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        startToast("존재하지 않는 사용자입니다.");
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        startToast("존재하지 않는 사용자이거나 비밀번호가 잘못되었습니다.");
                                        break;
                                }
                            }
                        }
                    });
        } catch (IllegalArgumentException e){
            startToast("이메일이나 비밀번호를 모두 입력해주세요.");
        }
    }

    private void successLogin(){
        FragmentSNS fragmentsns = new FragmentSNS();
        fragmentManager.beginTransaction().replace(R.id.login_layout, fragmentsns).commit();
    }

    private void startToast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}