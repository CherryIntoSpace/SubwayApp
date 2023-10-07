package com.daejin.subwayapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daejin.subwayapp.MainActivity;
import com.daejin.subwayapp.OpenAPI;
import com.daejin.subwayapp.R;

import org.json.JSONException;

import java.io.IOException;

public class FragmentChart extends Fragment {

    EditText et_stationInfo;
    Button btn_inputStation;
    String stationName;
    String sdow;
    String sdirection;
    FragmentManager fragmentManager;
    TextView tv_result;

    String key = "745478546a686f6a31303570596e5162";
    String data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (fragmentManager.getBackStackEntryCount() <= 0) {
                    ((MainActivity) getActivity()).showDialog(getContext());
                } else {
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
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        et_stationInfo = view.findViewById(R.id.et_stationInfo);
        btn_inputStation = view.findViewById(R.id.btn_inputStation);
        tv_result = view.findViewById(R.id.tv_result);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        et_stationInfo.setOnClickListener(onClickListener);
        btn_inputStation.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.et_stationInfo) {
                StationDialog dig = new StationDialog(requireActivity());
                dig.setDialogListener(new StationDialog.DialogListener() {
                    @Override
                    public void onConfirmClicked(String sname, String dow, String direction) {
                        stationName = sname;
                        sdow = dow;
                        sdirection = direction;
                        et_stationInfo.setText(stationName + ", " + sdow + ", " + sdirection);
                    }

                    @Override
                    public void onCancelClicked() {

                    }
                });
                dig.show();
            } else if (v.getId() == R.id.btn_inputStation) {
                OpenAPI openAPI = new OpenAPI();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            openAPI.searchStation(stationName);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    };

    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
