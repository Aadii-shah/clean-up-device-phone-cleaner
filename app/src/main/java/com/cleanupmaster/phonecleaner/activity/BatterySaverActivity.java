package com.cleanupmaster.phonecleaner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
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

import com.airbnb.lottie.Lottie;
import com.cleanupmaster.phonecleaner.R;
import com.cleanupmaster.phonecleaner.helper.BatteryManagerBroadcastReceiver;
import com.cleanupmaster.phonecleaner.utils.BatteryUtils;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class BatterySaverActivity extends AppCompatActivity {
    private LinearLayout music_linearLayout, game_linearLayout, video_linearLayout, tiktok_linearLayout;
    private MaterialButton battery_optimize_button;
    private ImageView back_arrow;
    private DonutProgress battery_charging_progressBar;
    private TextView battery_current, battery_power, battery_temperature, charge_complete_remaining;
    private BatteryManagerBroadcastReceiver batteryReceiver;
    private Handler handler;
    private BatteryUtils batteryUtils;
    private BatteryManager batteryManager;
    private Lottie battery_charging_anim, charging_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_saver);
        batteryUtils = new BatteryUtils(this);
        handler = new Handler();

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
        charge_complete_remaining = findViewById(R.id.charge_complete_remaining);
        batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE); // Initialize batteryManager

        //battery_charging_anim = findViewById(R.id.battery_charging_anim);
       // charging_anim = findViewById(R.id.charging_anim);

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

        // Schedule periodic updates every 5 seconds
        handler.postDelayed(updateRunnable, 1000);
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
    protected void onDestroy() {
        super.onDestroy();
        // Stop the periodic updates
        handler.removeCallbacks(updateRunnable);
    }
    private void updateBatteryInformation() {
        BatteryUtils.BatteryInfo batteryInfo = batteryUtils.getBatteryInfo();
        // Battery percentage
        String batteryPercentage = String.valueOf((batteryInfo.level * 100) / batteryInfo.scale);
        // Battery power
        double power = batteryInfo.voltage * batteryInfo.current / 1000.0; // Convert to watts
        // Convert to kilowatts if the power is large
        String formattedPower;
        if (power >= 1000) {
            formattedPower = String.format(Locale.US, "%.2f kW", power / 1000.0);
        } else {
            formattedPower = String.format(Locale.US, "%.2f W", power);
        }
        // Battery current
        double current = batteryInfo.current / 1000.0; // Convert to amperes
        String formattedCurrent = String.format(Locale.US, "%.2f A", current);

        // Battery temperature
        double tempInCelsius = batteryInfo.temperature / 10.0; // Convert to degrees Celsius

        runOnUiThread(() -> {
            battery_current.setText(formattedCurrent);
            battery_power.setText(formattedPower);
            battery_temperature.setText(String.valueOf(tempInCelsius));
            battery_charging_progressBar.setDonut_progress(batteryPercentage);
            battery_charging_progressBar.setText(batteryPercentage);
            charge_complete_remaining.setText(calculateChargeTimeRemaining(batteryInfo));

            // Show/hide Lottie animations based on charging status
            boolean isCharging = batteryInfo.status == BatteryManager.BATTERY_STATUS_CHARGING
                    || batteryInfo.status == BatteryManager.BATTERY_STATUS_FULL;
            int visibility = isCharging ? View.VISIBLE : View.INVISIBLE;
            findViewById(R.id.battery_charging_anim).setVisibility(visibility);
            findViewById(R.id.charging_anim).setVisibility(visibility);
        });

    }
    private String calculateChargeTimeRemaining(BatteryUtils.BatteryInfo batteryInfo) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            // Check if the device is charging
            if (batteryInfo.status == BatteryManager.BATTERY_STATUS_CHARGING || batteryInfo.status == BatteryManager.BATTERY_STATUS_FULL) {
                // Use the default method for devices with API level 29 or higher
                long chargeTimeRemaining = batteryManager.computeChargeTimeRemaining();

                // Handle the case when the chargeTimeRemaining is not available
                if (chargeTimeRemaining >= 0) {
                    // Calculate hours and minutes
                    long hours = chargeTimeRemaining / 3600;
                    long minutes = (chargeTimeRemaining % 3600) / 60;

                    return String.format(Locale.US, "Charging, need %d h %d min", hours, minutes);
                } else {
                    return "Unable to estimate remaining charging time.";
                }
            } else {
                return "Not charging or full.";
            }
        } else {
            // For devices with API level lower than 29, calculate charge time based on charging current
            double chargingCurrent = Math.abs(batteryInfo.current / 1000.0); // Convert to amperes

            if (chargingCurrent > 0) {
                int remainingCapacity = batteryInfo.scale - batteryInfo.level;

                // Ensure remainingCapacity is non-negative
                remainingCapacity = Math.max(remainingCapacity, 0);

                long secondsRemaining = (long) (remainingCapacity * 3600L / chargingCurrent);

                // Log additional information

                if (secondsRemaining >= 0) {
                    // Calculate hours and minutes
                    long hours = secondsRemaining / 3600;
                    long minutes = (secondsRemaining % 3600) / 60;

                    return String.format(Locale.US, "Charging, need %d h %d min", hours, minutes);
                } else {
                    return "Unable to estimate remaining charging time.";
                }
            } else {
                return "Unable to estimate remaining charging time. Current is 0.";
            }
        }
    }

}