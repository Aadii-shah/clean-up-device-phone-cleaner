package com.cleanupmaster.phonecleaner.activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.cleanupmaster.phonecleaner.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CPUActivity extends AppCompatActivity {
    private TextView textView_cpu_temperature;
    private Handler handler = new Handler();
    private static final int DELAY_MILLIS = 3000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpuactivity);
        textView_cpu_temperature = findViewById(R.id.textView_cpu_temperature);
        // Run getCpuTemp initially
        getCpuTemp();
        // Schedule getCpuTemp to run every 5 seconds
        handler.postDelayed(updateRunnable, DELAY_MILLIS);
    }
    // Define a Runnable to be executed periodically
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            getCpuTemp();

            // Schedule the Runnable to run again after 5 seconds
            handler.postDelayed(this, DELAY_MILLIS);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the callbacks to prevent memory leaks
        handler.removeCallbacks(updateRunnable);
    }

    public float getCpuTemp() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = reader.readLine();
            float temp = Float.parseFloat(line) / 1000.0f;

            // Set the CPU temperature in the TextView
            textView_cpu_temperature.setText(temp + " °C");

            // Load Lottie animations based on temperature
            if (temp >= 0 && temp <= 35) {
                loadLottieAnimation("wow_cpu.json");
            } else {
                loadLottieAnimation("hot_temperature.json");
            }

            return temp;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private void loadLottieAnimation(String animationFileName) {
        LottieAnimationView cpuTempAnimation = findViewById(R.id.CPU_anim);
        cpuTempAnimation.setAnimation(animationFileName);
        cpuTempAnimation.playAnimation();
    }
}