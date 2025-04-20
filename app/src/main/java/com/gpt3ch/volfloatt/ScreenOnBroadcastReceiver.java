package com.gpt3ch.volfloatt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOnBroadcastReceiver extends BroadcastReceiver {
    private final FloatingWidgetService service;

    public ScreenOnBroadcastReceiver(final FloatingWidgetService service) {
        this.service = service;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            service.showWidget();
        }
    }
}