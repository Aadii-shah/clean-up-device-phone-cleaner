package com.cleanupmaster.phonecleaner.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.widget.Toast;

// BatteryManagerBroadcastReceiver.java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryManagerBroadcastReceiver extends BroadcastReceiver {

    private BatteryUpdateListener listener;

    public BatteryManagerBroadcastReceiver(BatteryUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            listener.onBatteryUpdate(intent);
        }
    }

    public interface BatteryUpdateListener {
        void onBatteryUpdate(Intent intent);
    }
}


