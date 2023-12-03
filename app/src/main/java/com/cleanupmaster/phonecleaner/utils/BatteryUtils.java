package com.cleanupmaster.phonecleaner.utils;

import static android.content.Context.BATTERY_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryUtils {

    private Context context;

    public BatteryUtils(Context context) {
        this.context = context;
    }

    public int getBatteryPercentage() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float) scale;
            return (int) (batteryPct * 100);
        }

        return -1;
    }

    public int getBatteryCurrent() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = context.registerReceiver(null, intentFilter);
        BatteryManager batteryManager;
        batteryManager = (BatteryManager)context.getSystemService(BATTERY_SERVICE);

        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
    }

    public double getBatteryPower() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = context.registerReceiver(null, intentFilter);
        BatteryManager batteryManager;
        batteryManager = (BatteryManager)context.getSystemService(BATTERY_SERVICE);

        int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        int current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);


        // Convert voltage to volts
        double voltageVolts = voltage / 1000.0;

        // Convert current to amperes
        double currentAmperes = current / 1000.0;

        // Calculate power in watts
        return voltageVolts * currentAmperes;

       // return voltage * current / 1000.0;
    }

    public double getBatteryTemperature() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = context.registerReceiver(null, intentFilter);

        int temperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        // Battery temperature
        return temperature / 10.0;
    }
}
