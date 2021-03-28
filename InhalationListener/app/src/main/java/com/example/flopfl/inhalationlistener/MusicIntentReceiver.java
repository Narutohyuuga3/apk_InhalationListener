package com.example.flopfl.inhalationlistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MusicIntentReceiver extends BroadcastReceiver {
    int status;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    status = 0;
                    Log.d(TAG, "Headset is unplugged");
                    break;
                case 1:
                    status = 1;
                    Log.d(TAG, "Headset is plugged");
                    break;
                default:
                    Log.d(TAG, "I have no idea what the headset state is");
            }
        } else {
            status = -1;
        }
    }

    public int getStatus() {
        return status;
    }
}