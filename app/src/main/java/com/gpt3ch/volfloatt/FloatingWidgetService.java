package com.gpt3ch.volfloatt;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class FloatingWidgetService extends Service {
    private static final String SERVICE_NAME = "FloatingWidgetService";
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private View mFloatingView;
    private ScreenOnBroadcastReceiver screenOnBroadcastReceiver;
    private View.OnClickListener clickListener;
    private int initialY;
    private float initialTouchY;
    private boolean isDragging = false;
    private static final int TOUCH_TOLERANCE = 10; // Adjust this value as needed

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        if (!Settings.canDrawOverlays(this)) {
            Log.e("FloatingWidget", "Overlay permission NOT granted in Service!");
            return;
        }
        if(screenOnBroadcastReceiver == null) {
            screenOnBroadcastReceiver = new ScreenOnBroadcastReceiver(this);
        }
        IntentFilter screenOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenOnBroadcastReceiver, screenOnFilter);

        clickListener= new VolumeClickListener(this);

        // Ensure the widget is shown when the service starts (if needed)
        if (mFloatingView == null) {
            showWidget();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showWidget(){
        if (mFloatingView == null) {
            mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null);
            final ImageView floatingIcon = mFloatingView.findViewById(R.id.floating_icon);

            // For drawing over other apps
            // Allows touch events to pass through if needed
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // For drawing over other apps
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // Allows touch events to pass through if needed
                    PixelFormat.TRANSLUCENT
            );

            params.gravity = Gravity.BOTTOM | Gravity.END; // Initial position on the right edge, top aligned
            params.x = 0; // Offset from the right edge
            params.y = 200; // Initial vertical offset

            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);

            floatingIcon.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        params = ((WindowManager.LayoutParams) mFloatingView.getLayoutParams());
                        initialY = params.y;
                        initialTouchY = event.getRawY();
                        isDragging = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = initialTouchY - event.getRawY(); // Inverted Y calculation
                        if (!isDragging && Math.abs(deltaY) > TOUCH_TOLERANCE) {
                            isDragging = true;
                        }
                        if (isDragging) {
                            params.y = initialY + (int) deltaY;
                            mWindowManager.updateViewLayout(mFloatingView, params);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!isDragging) {
                            clickListener.onClick(v);
                        }
                        isDragging = false;
                        return true;
                }
                return false;
            });
        } else if (mFloatingView.getVisibility() != View.VISIBLE) {
            mFloatingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(SERVICE_NAME, "on destroy called for FloatingWidgetService");
        unregisterReceiver(screenOnBroadcastReceiver);
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
            mFloatingView = null;
        }
    }
}
