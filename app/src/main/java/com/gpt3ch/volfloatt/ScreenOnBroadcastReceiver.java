package com.gpt3ch.volfloatt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOnBroadcastReceiver extends BroadcastReceiver {
    private final FloatingWidgetService service;

    public ScreenOnBroadcastReceiver(FloatingWidgetService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Log.d("FloatingWidget", "Screen ON detected, ensuring widget is visible");
            service.showWidget();
        }
    }
}