package com.cleanupmaster.phonecleaner.utils;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryUtils {

    private Context context;


    public BatteryUtils(Context context) {
        this.context = context;
    }

    public BatteryInfo getBatteryInfo() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = context.registerReceiver(null, intentFilter);
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        int current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        int temperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        return new BatteryInfo(level, scale, voltage, current, temperature, status);
    }

    public class BatteryInfo {
        public int level;
        public int scale;
        public int voltage;
        public int current;
        public int temperature;
        public int status;

        public BatteryInfo(int level, int scale, int voltage, int current, int temperature, int status) {
            this.level = level;
            this.scale = scale;
            this.voltage = voltage;
            this.current = current;
            this.temperature = temperature;
            this.status = status;
        }
    }
}
