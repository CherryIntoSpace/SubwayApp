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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class FragmentPasswordReset extends Fragment {

    private FirebaseAuth mAuth;
    FragmentManager fragmentManager;
    EditText et_email;
    Button btn_send;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_reset, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getActivity().getSupportFragmentManager();
        et_email = view.findViewById(R.id.emaileditText);
        btn_send = view.findViewById(R.id.btn_send);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        btn_send.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_send) {
                send();
            }
        }
    };

    private void send() {
        String email = et_email.getText().toString();
        try {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        startToast("입력하신 이메일로 인증 메일을 전송하였습니다.");
                    }
                    else {
                        String error = ((FirebaseAuthException)task.getException()).getErrorCode();
                        switch (error){
                            case "ERROR_INVALID_EMAIL":
                                startToast("이메일 형식이 올바르지 않습니다.");
                                break;
                            case "ERROR_USER_NOT_FOUND":
                                startToast("해당 이메일로 가입한 사용자가 존재하지 않습니다.");
                                break;
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e){
            startToast("이메일을 입력해주세요.");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}