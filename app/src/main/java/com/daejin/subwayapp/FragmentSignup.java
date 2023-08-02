package com.daejin.subwayapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
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

public class FragmentSignup extends Fragment {

    private FirebaseAuth mAuth;
    FragmentManager fragmentManager;
    EditText inputEmail;
    EditText inputPassword;
    EditText inputPasswordcheck;
    Button inputSignup;
    Button gotoLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getActivity().getSupportFragmentManager();
        inputEmail = view.findViewById(R.id.emaileditText);
        inputPassword = view.findViewById(R.id.passwordeditText);
        inputPasswordcheck = view.findViewById(R.id.passwordCheckeditText);
        inputSignup = view.findViewById(R.id.btn_inputSignup);
        gotoLogin = view.findViewById(R.id.btn_gotoLogin);

        inputSignup.setOnClickListener(onClickListener);
        gotoLogin.setOnClickListener(onClickListener);

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
            if (v.getId() == R.id.btn_inputSignup) {
                signUp();
            }
            else if (v.getId() == R.id.btn_gotoLogin){
                startLogInscreen();
            }
        }
    };

    private void signUp(){
        String email = String.valueOf(inputEmail.getText());
        String password = String.valueOf(inputPassword.getText());
        String passwordCheck = String.valueOf(inputPasswordcheck.getText());

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
                                    startToast("회원가입이 완료되었습니다.");
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
        fragmentManager.beginTransaction().replace(R.id.signup_layout, fragmentLogin)
                .addToBackStack(null).commit();
    }
    private void startToast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}