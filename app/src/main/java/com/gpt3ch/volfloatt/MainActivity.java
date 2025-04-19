package com.gpt3ch.volfloatt;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private Button startStopButton;
    private boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "starting main");
        setContentView(R.layout.activit_main); // You might have a simple layout

        startStopButton = findViewById(R.id.startStopButton); // Assuming you have a button in activity_main.xml
        startStopButton.setOnClickListener(v -> toggleFloatingWidgetService());

        checkOverlayPermission();
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
            startFloatingWidgetService();
            isServiceRunning = true;
            updateButtonText();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startFloatingWidgetService();
                    isServiceRunning = true;
                    updateButtonText();
                } else {
                    Toast.makeText(this, "Overlay permission is required!", Toast.LENGTH_SHORT).show();
                    // Optionally, finish the activity or disable functionality
                }
            }
        }
    }

    private void startFloatingWidgetService() {
        Intent serviceIntent = new Intent(this, FloatingWidgetService.class);
        startService(serviceIntent);
    }

    private void stopFloatingWidgetService() {
        Intent serviceIntent = new Intent(this, FloatingWidgetService.class);
        stopService(serviceIntent);
    }

    private void toggleFloatingWidgetService() {
        if (isServiceRunning) {
            stopFloatingWidgetService();
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
