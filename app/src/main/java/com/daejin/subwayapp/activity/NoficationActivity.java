package com.daejin.subwayapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daejin.subwayapp.R;
import com.daejin.subwayapp.utils.SharedPreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class NoficationActivity extends AppCompatActivity {

    SwitchCompat swt_commentNofication;
    CheckBox cb_emergency;
    CheckBox cb_delay;

    boolean isEmergencyChecked;
    boolean isDelayChecked;

    private static final String TOPIC_EMERGENCY_NOFICATION = "EMERGENCY";
    private static final String TOPIC_DELAY_NOFICATION = "DELAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nofication);
        initId();
        initCheck();
        cb_emergency.setOnClickListener(onClickListener);
        cb_delay.setOnClickListener(onClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initId() {
        swt_commentNofication = findViewById(R.id.swt_commentNofication);
        cb_emergency = findViewById(R.id.cb_emergency);
        cb_delay = findViewById(R.id.cb_delay);
    }

    private void  initCheck() {
        isEmergencyChecked = SharedPreferenceManager.getEmergencyNofication(this);
        isDelayChecked = SharedPreferenceManager.getDelayNofication(this);
        cb_emergency.setChecked(isEmergencyChecked);
        cb_delay.setChecked(isDelayChecked);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean checked = ((CheckBox) view).isChecked();
            if (view.getId() == R.id.cb_emergency) {
                if (checked) {
                    SharedPreferenceManager.setEmergencyNofication(NoficationActivity.this, true);
                    subscribeEmergencyNofication();
                } else {
                    SharedPreferenceManager.setEmergencyNofication(NoficationActivity.this, false);
                    unsubscribeEmergencyNofication();
                }
            }
            if (view.getId() == R.id.cb_delay) {
                if (checked) {
                    SharedPreferenceManager.setDelayNofication(NoficationActivity.this, true);
                    subscribeDelayNofication();
                } else {
                    SharedPreferenceManager.setDelayNofication(NoficationActivity.this, false);
                    unsubscribeDelayNofication();
                }
            }
        }
    };

    private void unsubscribeDelayNofication() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_DELAY_NOFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "알림 설정 해제";
                        if (!task.isSuccessful()) {
                            msg = "설정 실패";
                        }
                        startToast(msg);
                    }
                });
    }

    private void subscribeDelayNofication() {
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_DELAY_NOFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "지연정보 알림 수신 설정 완료";
                        if (!task.isSuccessful()) {
                            msg = "설정 실패";
                        }
                        startToast(msg);
                    }
                });
    }

    private void unsubscribeEmergencyNofication() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_EMERGENCY_NOFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "알림 설정 해제";
                        if (!task.isSuccessful()) {
                            msg = "설정 실패";
                        }
                        startToast(msg);
                    }
                });
    }

    private void subscribeEmergencyNofication() {
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_EMERGENCY_NOFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "긴급상황 알림 수신 설정 완료";
                        if (!task.isSuccessful()) {
                            msg = "설정 실패";
                        }
                        startToast(msg);
                    }
                });
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}