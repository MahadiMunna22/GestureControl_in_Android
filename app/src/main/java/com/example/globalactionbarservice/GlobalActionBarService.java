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
    private WindowManager mWindowManager;
    private View mChatHeadView;
    float a,b;

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
        configureSwipeButton();

        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_chat_head, null);


        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);
        Log.d("Tag1","Working");

        //Drag and move chat head using user's touch action.
        final ImageView chatHeadImage = (ImageView) mChatHeadView.findViewById(R.id.chat_head_profile_iv);
        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            public int initialX;
            public int initialY;
            public float initialTouchX;
            public float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = event.getAction();

                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            // Cursor Tap will make it a TAP Operation
                            a = params.x;
                            b = params.y;
                            Tap(a,b);

                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Button Tap will make the TAP Operation
                        a = params.x;
                        b = params.y;
                        configureTapButton(a,b);
                        configureDragButton(a,b);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mChatHeadView, params);
                        lastAction = event.getAction();

                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the chat head layout we created

    }

    private void configurePowerButton() {
        Button powerButton = (Button) mLayout.findViewById(R.id.power);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
            }
        });
    }

    public void configureDragButton(final float x, final float y)  {
        Button dragButton = (Button) mLayout.findViewById(R.id.volume_up);
        dragButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Path tapPath = new Path();
                tapPath.moveTo(x,y);
                tapPath.lineTo(x, y);
                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(tapPath, 0, 1500));
                dispatchGesture(gestureBuilder.build(), null, null);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Path tapPath1 = new Path();
                tapPath1.moveTo(x,y);
                tapPath1.lineTo(x + 300 ,y);
                GestureDescription.Builder gestureBuilder1 = new GestureDescription.Builder();
                gestureBuilder1.addStroke(new GestureDescription.StrokeDescription(tapPath1, 0, 1500));
                dispatchGesture(gestureBuilder1.build(), null, null);

                Toast.makeText(getApplicationContext(),"Tapped "+x+" "+y,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void configureSwipeButton() {
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

    @SuppressLint("NewApi")
    public void Tap(float x, float y){

        Path tapPath = new Path();
        tapPath.moveTo(x,y);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(tapPath, 0, 100));
        dispatchGesture(gestureBuilder.build(), null, null);
        //Log.d("MainActivity",a+" "+b);
        Toast.makeText(GlobalActionBarService.this,x+" "+y,Toast.LENGTH_SHORT).show();
    }
    public void configureTapButton(final float x, final float y) {
        Button TapButton = (Button) mLayout.findViewById(R.id.tap);
        TapButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Path tapPath = new Path();
                tapPath.moveTo(x,y);
                GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(tapPath, 0, 150));
                dispatchGesture(gestureBuilder.build(), null, null);

                Toast.makeText(getApplicationContext(),"Tapped "+x+" "+y,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
}

