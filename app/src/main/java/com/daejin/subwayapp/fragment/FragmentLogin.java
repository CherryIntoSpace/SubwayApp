package com.daejin.subwayapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.utils.SharedPreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FragmentLogin extends Fragment {

    private FirebaseAuth mAuth;
    FragmentManager fragmentManager;
    EditText et_Email;
    EditText et_Password;
    SwitchCompat swt_Autologin;
    private boolean isAutologin;
    Button btn_Login;
    Button btn_gotoreset;
    Button btn_gotoSignUp;

    String email;
    String password;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        et_Email = view.findViewById(R.id.emaileditText);
        et_Password = view.findViewById(R.id.passwordeditText);
        swt_Autologin = view.findViewById(R.id.swt_autologin);
        btn_Login = view.findViewById(R.id.btn_inputLogin);
        btn_gotoreset = view.findViewById(R.id.btn_gotoreset);
        btn_gotoSignUp = view.findViewById(R.id.btn_gotoSignup);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        btn_Login.setOnClickListener(onClickListener);
        btn_gotoreset.setOnClickListener(onClickListener);
        btn_gotoSignUp.setOnClickListener(onClickListener);
        swt_Autologin.setOnCheckedChangeListener(onCheckedChangeListener);

        Map<String, String> loginInfo = SharedPreferenceManager.getLoginInfo(requireActivity());
        if (!loginInfo.isEmpty()) {
            email = loginInfo.get("email");
            password = loginInfo.get("password");
            logIn(email, password);
        }
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            isAutologin = isChecked;
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_inputLogin) {
                if (et_Email.length() != 0 && et_Password.length() != 0) {
                    String inputEmail = String.valueOf(et_Email.getText());
                    String inputPassword = String.valueOf(et_Password.getText());
                    logIn(inputEmail, inputPassword);
                } else {
                    startToast("이메일과 패스워드를 모두 입력해주세요.");
                }
            } else if (v.getId() == R.id.btn_gotoreset) {
                setBtn_gotoreset();
            } else if (v.getId() == R.id.btn_gotoSignup) {
                startSignupscreen();
            }
        }
    };

    private void logIn(String email, String password) {
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (isAutologin) {
                                    SharedPreferenceManager.setLoginInfo(requireActivity(), email, password);
                                }

                                FirebaseUser user = mAuth.getCurrentUser();
                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    String email = user.getEmail();
                                    String uid = user.getUid();

                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("email", email);
                                    hashMap.put("uid", uid);
                                    hashMap.put("name", "");
                                    hashMap.put("image", "");
                                    hashMap.put("cover", "");

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(uid).setValue(hashMap);
                                }

                                startToast("계정 정보 : " + email + "\n로그인에 성공하였습니다!");
                                successLogin();
                            } else {
                                String error = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (error) {
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
        } catch (IllegalArgumentException ignored) {

        }
    }

    private void successLogin() {
        fragmentManager.popBackStack();
        FragmentSNS fragmentsns = new FragmentSNS();
        fragmentManager.beginTransaction().replace(R.id.sns_main_layout, fragmentsns).commit();
    }

    private void setBtn_gotoreset() {
        et_Password.setText(null);
        FragmentPasswordReset fragmentPasswordReset = new FragmentPasswordReset();
        fragmentManager.beginTransaction().replace(R.id.login_layout, fragmentPasswordReset).addToBackStack(null).commit();

    }

    private void startSignupscreen(){
        FragmentSignup fragmentSignup = new FragmentSignup();
        fragmentManager.beginTransaction().replace(R.id.login_layout, fragmentSignup).commit();
    }

    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


}