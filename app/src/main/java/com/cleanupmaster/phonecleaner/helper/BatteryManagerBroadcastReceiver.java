package com.cleanupmaster.phonecleaner.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


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


