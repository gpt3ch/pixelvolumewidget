package com.gpt3ch.volfloatt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class VolumeClickListener implements android.view.View.OnClickListener {

    private final Context mContext;

    public VolumeClickListener(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(android.view.View v) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);

            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                AlertDialog dialog = builder.create();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
                dialog.setOnShowListener(DialogInterface::dismiss);
                dialog.show();
            } catch (Exception e) {
                Log.e("FloatingWidget", "Error showing transparent dialog: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(mContext, "Could not open volume controls.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("FloatingWidget", "AudioManager is null");
            Toast.makeText(mContext, "Could not access volume controls.", Toast.LENGTH_SHORT).show();
        }
    }
}