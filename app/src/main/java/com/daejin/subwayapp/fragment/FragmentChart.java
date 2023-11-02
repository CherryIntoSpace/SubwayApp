package com.daejin.subwayapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.utils.OpenAPI;
import com.daejin.subwayapp.adapters.StationAdapter;
import com.daejin.subwayapp.list.StationList;
import com.daejin.subwayapp.list.StationTime;
import com.daejin.subwayapp.adapters.TimeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentChart extends Fragment {

    EditText et_stationInfo;
    Button btn_inputStation;
    String stationName;
    String sDow;
    String sDirection;
    String sCode;
    String sLNum;

    OpenAPI openAPI;

    private RecyclerView recyclerView;
    private ArrayList<StationList> stationList = new ArrayList<>();
    private ArrayList<StationTime> stationTime = new ArrayList<>();

    private StationAdapter stationAdapter = new StationAdapter();
    private TimeAdapter timeAdapter = new TimeAdapter();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        openAPI = new OpenAPI(requireActivity());
        et_stationInfo = view.findViewById(R.id.et_stationInfo);
        btn_inputStation = view.findViewById(R.id.btn_inputStation);
        recyclerView = view.findViewById(R.id.layout_recyclerView_Chart);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(container.getContext(), 1);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(container.getContext(), R.drawable.recyclerview_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        et_stationInfo.setOnClickListener(onClickListener);
        btn_inputStation.setOnClickListener(onClickListener);
        stationAdapter.setOnItemClickListener(onItemClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.et_stationInfo) {
                StationDialog dig = new StationDialog(requireActivity());
                dig.setDialogListener(new StationDialog.DialogListener() {
                    @Override
                    public void onConfirmClicked(String sname, String dow, String direction) {
                        if (sname.equals("서울")) {
                            stationName = "서울역";
                        } else {
                            stationName = sname;
                        }
                        sDow = dow;
                        sDirection = direction;
                        et_stationInfo.setText(getString(R.string.setChartet, stationName, sDow, sDirection));
                    }

                    @Override
                    public void onCancelClicked() {

                    }
                });
                dig.show();
            } else if (v.getId() == R.id.btn_inputStation) {
                recyclerView.setAdapter(stationAdapter);
                apiSearchSCode();
            }
        }
    };
    StationAdapter.onItemClickListener onItemClickListener = new StationAdapter.onItemClickListener() {
        @Override
        public void onItemClicked(int pos, String code, String num) {
            sCode = code;
            sLNum = num;
            stationList.clear();
            et_stationInfo.setText(getString(R.string.setChartet2, stationName, sLNum, sDow, sDirection));
            apiSearchSchedule();
        }
    };

    private void apiSearchSCode() {
        stationList.clear();
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray searchedStation = openAPI.searchStation(stationName);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (searchedStation != null) {
                                    for (int i = 0; i < searchedStation.length(); i++) {
                                        JSONObject temp = null;
                                        temp = searchedStation.getJSONObject(i);
                                        stationList.add(new StationList(R.drawable.baseline_search_24,
                                                temp.getString("STATION_CD")
                                                , temp.getString("STATION_NM"),
                                                temp.getString("LINE_NUM")));
                                    }
                                } else {
                                    startToast("해당 하는 역의 정보가 없습니다.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            stationAdapter.setsList(stationList);
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void apiSearchSchedule() {
        stationList.clear();
        stationTime.clear();
        String weekTag;
        String inoutTag;

        weekTag = convWeek(sDow);
        inoutTag = convDirection(sDirection);

        recyclerView.setAdapter(timeAdapter);
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray searchedSchedule = openAPI.searchSchedule(sCode, weekTag, inoutTag);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (searchedSchedule != null) {
                                    for (int i = 0; i < searchedSchedule.length(); i++) {
                                        JSONObject temp = null;
                                        temp = searchedSchedule.getJSONObject(i);
                                        stationTime.add(new StationTime(temp.getString("LEFTTIME"),
                                                temp.getString("SUBWAYSNAME"), temp.getString("SUBWAYENAME")));
                                    }
                                } else {
                                    startToast("해당 방향의 운행 정보가 없습니다.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            timeAdapter.setsList(stationTime);
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String convWeek(String sdow) {
        String weekTag = "";

        switch (sdow) {
            case "평일":
                weekTag = "1";
                break;
            case "토요일":
                weekTag = "2";
                break;
            case "휴일/일요일":
                weekTag = "3";
                break;
        }
        return weekTag;
    }

    private String convDirection(String sdirection) {
        String inoutTag = "";

        switch (sdirection) {
            case "상행":
                inoutTag = "1";
                break;
            case "하행":
                inoutTag = "2";
                break;
        }

        return inoutTag;
    }

    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}
