package com.daejin.subwayapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.daejin.subwayapp.R;

import java.util.Objects;

public class StationDialog extends Dialog {
    private Context mContext;
    private DialogListener dialogListener;
    public EditText et_sname;
    public RadioGroup rg_day;
    public RadioGroup rg_direction;
    public String sname;
    public String dow;
    public String direction;

    private boolean rgdayChecked;
    private boolean rgdirectionChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_dialog);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_confirm = findViewById(R.id.btn_confirm);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        et_sname = findViewById(R.id.et_sname);
        rg_day = findViewById(R.id.rg_day);
        rg_direction = findViewById(R.id.rg_direction);
        rg_day.setOnCheckedChangeListener(groupDay);
        rg_direction.setOnCheckedChangeListener(groupDirection);

        btn_confirm.setOnClickListener(onClickListener);
        btn_cancel.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_confirm){
                if(rgdayChecked && rgdirectionChecked &&
                        !TextUtils.isEmpty(et_sname.getText().toString())){
                    sname = et_sname.getText().toString();
                    dialogListener.onConfirmClicked(sname, dow, direction);
                    dismiss();
                }
                else{
                    startToast("모든 칸을 입력해주세요.");
                }
            }
            else if(v.getId() == R.id.btn_cancel){
                cancel();
                dismiss();
            }
        }
    };

    public StationDialog(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    interface DialogListener {
        void onConfirmClicked(String sname, String dow, String direction);
        void onCancelClicked();
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    private RadioGroup.OnCheckedChangeListener groupDay = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            rgdayChecked = true;
            RadioButton radioButton = findViewById(i);
            dow = radioButton.getText().toString();
        }
    };
    private RadioGroup.OnCheckedChangeListener groupDirection = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            rgdirectionChecked = true;
            RadioButton radioButton = findViewById(i);
            direction = radioButton.getText().toString();
        }
    };

    private void startToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
