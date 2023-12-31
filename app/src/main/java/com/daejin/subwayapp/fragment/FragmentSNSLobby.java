package com.daejin.subwayapp.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.daejin.subwayapp.R;

public class FragmentSNSLobby extends Fragment {

    FragmentManager fragmentManager;
    Button btn_login, btn_signup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentManager = getActivity().getSupportFragmentManager();
        View view = inflater.inflate(R.layout.fragment_main_sns, container, false);
        btn_signup = view.findViewById(R.id.btn_signup);
        btn_login = view.findViewById(R.id.btn_login);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        btn_signup.setOnClickListener(onClickListener);
        btn_login.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_signup){
                startSignupscreen();
            }
            else if(v.getId() == R.id.btn_login){
                startLoginscreen();
            }
        }
    };

    private void startSignupscreen() {
        FragmentSignup fragmentSignup = new FragmentSignup();
        fragmentManager.beginTransaction().replace(R.id.sns_main_layout, fragmentSignup)
                .addToBackStack(null).commit();
    }

    private void startLoginscreen(){
        FragmentLogin fragmentLogin = new FragmentLogin();
        fragmentManager.beginTransaction().replace(R.id.sns_main_layout, fragmentLogin)
                .addToBackStack(null).commit();
    }
}