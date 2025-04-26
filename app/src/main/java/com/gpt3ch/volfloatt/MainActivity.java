package com.gpt3ch.volfloatt;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private Button startStopButton;
    private boolean isServiceRunning = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_main); // You might have a simple layout

        startStopButton = findViewById(R.id.startStopButton); // Assuming you have a button in activity_main.xml
        startStopButton.setOnClickListener(v -> toggleFloatingWidgetService());

        checkOverlayPermission();

        Intent serviceIntent = new Intent(this, VolumeControlService.class);
        startForegroundService(serviceIntent);
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
//            startFloatingWidgetService();
            isServiceRunning = true;
            updateButtonText();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
//                startFloatingWidgetService();
                isServiceRunning = true;
                updateButtonText();
            } else {
                Toast.makeText(this, "Overlay permission is required!", Toast.LENGTH_SHORT).show();
                // Optionally, finish the activity or disable functionality
            }
        }
    }

    private void startFloatingWidgetService() {
        final Intent serviceIntent = new Intent(this, FloatingWidgetService.class);
        startService(serviceIntent);
    }

    private void stopFloatingWidgetService() {
        final Intent serviceIntent = new Intent(this, FloatingWidgetService.class);
        stopService(serviceIntent);
    }

    private void toggleFloatingWidgetService() {
        if (isServiceRunning) {
//            stopFloatingWidgetService();
            isServiceRunning = false;
        } else {
            checkOverlayPermission(); // Re-check permission before starting again
        }
        updateButtonText();
    }

    private void updateButtonText() {
        startStopButton.setText(isServiceRunning ? "Stop Floating Widget" : "Start Floating Widget");
    }
}
