package com.cleanupmaster.phonecleaner.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.view.View;
import android.widget.ImageView;

import com.cleanupmaster.phonecleaner.R;
import com.github.lzyzsd.circleprogress.ArcProgress;
import java.lang.ref.WeakReference;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ArcProgress storage_progressBar;
    private ImageView battery_saver_ImageView, cpu_cooler_ImageView;
    private int percentageOccupied;


    private static class UpdateHandler extends Handler {
        private final WeakReference<MainActivity> activityWeakReference;

        public UpdateHandler(MainActivity activity) {
            super(Looper.getMainLooper());  // Use the non-deprecated constructor
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public void updateProgress(int progress) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                // Call the separate method to update the progress bar
                activity.updateProgressBarWithAnimation(progress);
            }
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.updateStorageInfo();
                // Schedule the next update after 5 seconds
                sendEmptyMessageDelayed(0, 5000);
            }
        }

    }

    private final UpdateHandler updateHandler = new UpdateHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage_progressBar = findViewById(R.id.storage_progressBar);
        battery_saver_ImageView = findViewById(R.id.battery_saver_ImageView);
        battery_saver_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BatterySaverActivity.class);
                startActivity(intent);
            }
        });
        cpu_cooler_ImageView = findViewById(R.id.cpu_cooler_ImageView);
        cpu_cooler_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CPUActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        // Start periodic updates when the activity is resumed
        updateHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop updates when the activity is paused
        updateHandler.removeMessages(0);
    }

    private void updateStorageInfo() {
        // Get total and available storage
        long totalStorage = getTotalInternalStorage();
        long availableStorage = getAvailableInternalStorage();

        // Calculate the percentage of total storage occupied
        percentageOccupied = (int) (((totalStorage - availableStorage) * 100) / totalStorage);

        // Set text in TextViews
       String totalStorageTextView = (formatSize(totalStorage));
       String availableStorageTextView = (formatSize(availableStorage));

        // Set progress for ProgressBar based on the percentage
        // Use the updateProgress method of UpdateHandler
        updateHandler.updateProgress(percentageOccupied);
        // Set storage_textView
        storage_progressBar.setBottomText(availableStorageTextView + "/" + totalStorageTextView);
    }

    private long getTotalInternalStorage() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        return totalBlocks * blockSize;
    }


    private long getAvailableInternalStorage() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    private String formatSize(long size) {
        // Convert size to human-readable format
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format(Locale.US,"%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private void updateProgressBarWithAnimation(int newProgress) {
        // Use ObjectAnimator to animate the progress
        ObjectAnimator anim = ObjectAnimator.ofInt(storage_progressBar, "progress", storage_progressBar.getProgress(), newProgress);
        anim.setDuration(1000); // Set the duration of the animation in milliseconds
        anim.start();
    }
}