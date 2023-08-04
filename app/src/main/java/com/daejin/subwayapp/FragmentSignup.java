package com.daejin.subwayapp;

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

public class FragmentSignup extends Fragment {

    private FirebaseAuth mAuth;
    FragmentManager fragmentManager;
    EditText et_Email;
    EditText et_Password;
    EditText et_Passwordcheck;
    Button btn_next;
    Button btn_gotoLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getActivity().getSupportFragmentManager();
        et_Email = view.findViewById(R.id.emaileditText);
        et_Password = view.findViewById(R.id.passwordeditText);
        et_Passwordcheck = view.findViewById(R.id.passwordCheckeditText);
        btn_next = view.findViewById(R.id.btn_next);
        btn_gotoLogin = view.findViewById(R.id.btn_gotoLogin);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        btn_next.setOnClickListener(onClickListener);
        btn_gotoLogin.setOnClickListener(onClickListener);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_next) {
                signUp();
            }
            else if (v.getId() == R.id.btn_gotoLogin){
                startLogInscreen();
            }
        }
    };

    private void signUp(){
        String email = String.valueOf(et_Email.getText());
        String password = String.valueOf(et_Password.getText());
        String passwordCheck = String.valueOf(et_Passwordcheck.getText());

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
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email", email);
                                    bundle.putString("password", password);
                                    startMemberscreen();
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

    private void startMemberscreen(){
        FragmentMemberInfo fragmentMemberInfo = new FragmentMemberInfo();
        fragmentManager.beginTransaction().replace(R.id.signup_layout, fragmentMemberInfo).commit();
    }
    private void startToast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}