package com.cleanupmaster.phonecleaner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cleanupmaster.phonecleaner.R;
import com.cleanupmaster.phonecleaner.helper.BatteryManagerBroadcastReceiver;
import com.cleanupmaster.phonecleaner.utils.BatteryUtils;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.material.button.MaterialButton;

public class BatterySaverActivity extends AppCompatActivity implements
        BatteryManagerBroadcastReceiver.BatteryUpdateListener {
    private LinearLayout music_linearLayout, game_linearLayout, video_linearLayout, tiktok_linearLayout;
    private MaterialButton battery_optimize_button;
    private ImageView back_arrow;
    private DonutProgress battery_charging_progressBar;
    private TextView battery_current, battery_power, battery_temperature;
    private BatteryManagerBroadcastReceiver batteryReceiver;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_saver);
        music_linearLayout = findViewById(R.id.music_linearLayout);
        game_linearLayout = findViewById(R.id.game_linearLayout);
        video_linearLayout = findViewById(R.id.video_linearLayout);
        tiktok_linearLayout = findViewById(R.id.tiktok_linearLayout);
        battery_optimize_button = findViewById(R.id.battery_optimize_button);
        back_arrow = findViewById(R.id.back_arrow);
        battery_charging_progressBar = findViewById(R.id.battery_charging_progressBar);
        battery_current  = findViewById(R.id.battery_current);
        battery_power = findViewById(R.id.battery_power);
        battery_temperature = findViewById(R.id.battery_temperature);


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_right);
        animation.setDuration(400);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);

        LinearLayout container = findViewById(R.id.list_linearLayout);
        container.setLayoutAnimation(controller);

        music_linearLayout.startAnimation(animation);
        game_linearLayout.startAnimation(animation);
        video_linearLayout.startAnimation(animation);
        tiktok_linearLayout.startAnimation(animation);
        battery_optimize_button.startAnimation(animation);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        // Initialize BatteryReceiver
        batteryReceiver = new BatteryManagerBroadcastReceiver(this);
        // Initialize handler
        handler = new Handler();
        // Schedule periodic updates every 5 seconds
        handler.postDelayed(updateRunnable, 1000);

        // Update battery information
        updateBatteryInformation();

        // Start listening for battery updates
        registerBatteryReceiver();


    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            // Update battery information periodically
            updateBatteryInformation();
            // Schedule the next update after 5 seconds
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerBatteryReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop listening for battery updates
        unregisterBatteryReceiver();
        // Stop the periodic updates
        handler.removeCallbacks(updateRunnable);
    }

    private void registerBatteryReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    private void unregisterBatteryReceiver() {
        unregisterReceiver(batteryReceiver);
    }


    @Override
    public void onBatteryUpdate(Intent intent) {
        if (intent != null) {
            // Log the values to check in real-time
            BatteryUtils batteryUtils = new BatteryUtils(this);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

            // Log the values to check in real-time
            Log.d("BatteryUpdate", "Battery Level: " + level + "/" + scale);
            Log.d("BatteryUpdate", "Battery Voltage: " + voltage);
            Log.d("BatteryUpdate", "Battery Current: " + batteryUtils.getBatteryCurrent());
            Log.d("BatteryUpdate", "Battery Temperature: " + intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));

            // Update your UI or perform any other actions based on the real-time battery information
            // For example, you can update TextViews, progress bars, etc.

            // Battery percentage
            String batteryPercentage = String.valueOf((level * 100) / scale);
            battery_charging_progressBar.setDonut_progress(batteryPercentage);
            battery_charging_progressBar.setText(batteryPercentage);

            // Battery power
            String batteryPower = String.valueOf(batteryUtils.getBatteryPower());
            battery_power.setText(batteryPower);

            // Battery current
            String batteryCurrent = String.valueOf(batteryUtils.getBatteryCurrent());
            battery_current.setText(batteryCurrent);

            // Battery temperature
            String batteryTemperature = String.valueOf(batteryUtils.getBatteryTemperature());
            battery_temperature.setText(batteryTemperature);
        }
    }



    private void updateBatteryInformation() {
        BatteryUtils batteryUtils = new BatteryUtils(this);
        // Battery percentage
        String batteryPercentage = String.valueOf(batteryUtils.getBatteryPercentage());
        battery_charging_progressBar.setDonut_progress(batteryPercentage);
        battery_charging_progressBar.setText(batteryPercentage);

        // Battery power
        String batteryPower = String.valueOf(batteryUtils.getBatteryPower());
        battery_power.setText(batteryPower);

        // Battery Current
        String batteryCurrent = String.valueOf(batteryUtils.getBatteryCurrent());
        battery_current.setText(batteryCurrent);

        // Battery Temperature
        String batteryTemperature = String.valueOf(batteryUtils.getBatteryTemperature());
        battery_current.setText(batteryTemperature);
    }
}