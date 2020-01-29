package com.example.globalactionbarservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayDeque;
import java.util.Deque;

public class GlobalActionBarService extends AccessibilityService {

    FrameLayout mLayout;

    @Override
    protected void onServiceConnected() {
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
        wm.addView(mLayout, lp);

        configurePowerButton();
        configureVolumeButton();
        configureSwipeButton();
        configureTapButton();
    }

    private void configurePowerButton() {
        Button powerButton = (Button) mLayout.findViewById(R.id.power);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
            }
        });
    }

    private void configureVolumeButton() {
        Button volumeUpButton = (Button) mLayout.findViewById(R.id.volume_up);
        volumeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });
    }

    private void configureSwipeButton() {
        Button swipeButton = (Button) mLayout.findViewById(R.id.swipe);
        swipeButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Path swipePath = new Path();
                swipePath.moveTo(500, 500);
                swipePath.lineTo(100, 500);
                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 100));
                dispatchGesture(gestureBuilder.build(), null, null);
            }
        });
    }

    private void configureTapButton() {
        Button TapButton = (Button) mLayout.findViewById(R.id.tap);
        TapButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Path swipePath = new Path();
                swipePath.moveTo(500, 600);
                //swipePath.lineTo(500, 500);
                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 150));
                dispatchGesture(gestureBuilder.build(), null, null);
            }
        });
    }



    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
}
