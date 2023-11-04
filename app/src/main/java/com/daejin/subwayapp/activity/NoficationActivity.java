package com.daejin.subwayapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.daejin.subwayapp.R;

public class NoficationActivity extends AppCompatActivity {

    SwitchCompat swt_keywordNofication;
    LinearLayout layout_keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nofication);
        initId();
        layout_keyword.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        swt_keywordNofication.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            checkChangeListener(isChecked);
        }
    };

    private void initId() {
        swt_keywordNofication = findViewById(R.id.swt_keywordNofication);
        layout_keyword = findViewById(R.id.layout_keyword);
    }

    private void checkChangeListener(boolean isChecked) {
        if (isChecked && layout_keyword.getVisibility() == View.GONE) {
            expand();
        } else {
            collapse();
        }
    }

    private void expand() {
        layout_keyword.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        layout_keyword.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, layout_keyword.getMeasuredHeight());
        mAnimator.start();

    }
    private void collapse() {
        int finalHeight = layout_keyword.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                layout_keyword.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layout_keyword.getLayoutParams();
                layoutParams.height = value;
                layout_keyword.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}