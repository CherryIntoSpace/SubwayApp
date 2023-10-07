package com.daejin.subwayapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

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
    EditText et_name;
    EditText et_station;
    View memberInfo;
    Button btn_next;
    Button btn_gotoLogin;


    String name;
    String station;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        fragmentManager = getActivity().getSupportFragmentManager();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(fragmentManager.getBackStackEntryCount() <= 0){
                    ((MainActivity) getActivity()).showDialog(getContext());
                }
                else{
                    this.setEnabled(false);
                    fragmentManager.popBackStack();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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
                                    showMemberInfo();
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

    private void showMemberInfo(){
        AlertDialog.Builder d = new AlertDialog.Builder(requireActivity());
        d.setTitle("회원 정보 입력");

        memberInfo = (View) View.inflate(requireActivity(), R.layout.edit_minfo, null);
        d.setView(memberInfo);

        d.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser user = mAuth.getCurrentUser();
                et_name = memberInfo.findViewById(R.id.et_name);
                et_station = memberInfo.findViewById(R.id.et_station);

                name = et_name.getText().toString();
                station = et_station.getText().toString();
            }
        });
        d.show();
    }

    private void startLogInscreen(){
        FragmentLogin fragmentLogin = new FragmentLogin();
        fragmentManager.beginTransaction().replace(R.id.signup_layout, fragmentLogin).commit();
    }

    private void startToast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}