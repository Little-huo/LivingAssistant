package com.example.livingassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCounterActivity extends AppCompatActivity implements StepCounterManager.OnStepCountChangeListener {

    private static final int REQUEST_CODE_PERMISSIONS = 100;

    private StepCounterManager stepCounterManager;
    private TextView stepCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        stepCountTextView = findViewById(R.id.step_count_text_view);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    REQUEST_CODE_PERMISSIONS);
        } else {
            setupStepCounter();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterManager != null) {
            stepCounterManager.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stepCounterManager != null) {
            stepCounterManager.stop();
        }
    }

    @Override
    public void onStepCountChanged(int stepCount) {
        stepCountTextView.setText(String.format("今日步数: %d", stepCount));
        saveStepCountToFile(stepCount);
    }

    private void setupStepCounter() {
        stepCounterManager = new StepCounterManager(this, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupStepCounter();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access step count.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveStepCountToFile(int stepCount) {
        String fileName = getFileNameForToday();
        File file = new File(getFilesDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(String.valueOf(stepCount).getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileNameForToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return "step_count_" + sdf.format(new Date()) + ".txt";
    }
}
