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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daejin.subwayapp.utils.OpenAPI;
import com.daejin.subwayapp.R;
import com.daejin.subwayapp.utils.StationAdapter;
import com.daejin.subwayapp.utils.StationList;
import com.daejin.subwayapp.utils.StationTime;
import com.daejin.subwayapp.utils.TimeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentChart extends Fragment {

    EditText et_stationInfo;
    Button btn_inputStation;
    String stationName;
    String sdow;
    String sdirection;
    String sCode;
    String sLNum;
    FragmentManager fragmentManager;

    private RecyclerView recyclerView;
    private ArrayList<StationList> list = new ArrayList<>();
    private ArrayList<StationTime> list2 = new ArrayList<>();

    private StationAdapter adapter = new StationAdapter();
    private TimeAdapter adapter2 = new TimeAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        et_stationInfo = view.findViewById(R.id.et_stationInfo);
        btn_inputStation = view.findViewById(R.id.btn_inputStation);
        recyclerView = view.findViewById(R.id.recyclerView);

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
        adapter.setOnItemClickListener(onItemClickListener);
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
                        et_stationInfo.setText(getString(R.string.setChartet, stationName, sdow, sdirection));
                    }

                    @Override
                    public void onCancelClicked() {

                    }
                });
                dig.show();
            } else if (v.getId() == R.id.btn_inputStation) {
                recyclerView.setAdapter(adapter);
                api_searchScode();
            }
        }
    };
    StationAdapter.onItemClickListener onItemClickListener = new StationAdapter.onItemClickListener() {
        @Override
        public void onItemClicked(int pos, String code, String num) {
            sCode = code;
            sLNum = num;
            list.clear();
            adapter.notifyDataSetChanged();
            et_stationInfo.setText(getString(R.string.setChartet2, stationName, sLNum, sdow, sdirection));
            api_searchSchedule();
        }
    };

    private void api_searchScode() {
        list.clear();
        OpenAPI openAPI = new OpenAPI();
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray searchedStation = openAPI.searchStation(stationName);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < searchedStation.length(); i++) {
                                JSONObject temp = null;
                                try {
                                    temp = searchedStation.getJSONObject(i);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                try {
                                    list.add(new StationList(R.drawable.baseline_search_24, temp.getString("STATION_CD")
                                            , temp.getString("STATION_NM"), temp.getString("LINE_NUM")));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            adapter.setsList(list);
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void api_searchSchedule() {
        list.clear();
        String weekTag;
        String inoutTag;

        weekTag = convWeek(sdow);
        inoutTag = convDirection(sdirection);

        recyclerView.setAdapter(adapter2);
        OpenAPI openAPI = new OpenAPI();
        new Thread() {
            @Override
            public void run() {
                try {
                    JSONArray searchedSchedule = openAPI.searchSchedule(sCode, weekTag, inoutTag);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < searchedSchedule.length(); i++) {
                                JSONObject temp = null;
                                try {
                                    temp = searchedSchedule.getJSONObject(i);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                try {
                                    list2.add(new StationTime(temp.getString("ARRIVETIME"),
                                            temp.getString("SUBWAYSNAME"), temp.getString("SUBWAYENAME")));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            adapter2.setsList(list2);
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

    private String convDirection(String sdirection){
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
