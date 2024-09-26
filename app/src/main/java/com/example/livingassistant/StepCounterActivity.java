package com.example.livingassistant;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StepCounterActivity extends AppCompatActivity implements StepCounterManager.OnStepCountChangeListener {

    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String PREFS_NAME = "user_prefs";
    private StepCounterManager stepCounterManager;
    private TextView stepCountTextView;
    private TextView healthInfoTextView;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        stepCountTextView = findViewById(R.id.step_count_text_view);
        healthInfoTextView = findViewById(R.id.health_info_text_view);

        // 获取 SharedPreferences 实例并读取当前用户邮箱
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("current_user_email", null);

        // 确保邮箱不为空
        if (currentUserEmail != null) {
            fetchHealthInfo(currentUserEmail);
        } else {
            healthInfoTextView.setText("未找到用户邮箱。");
        }

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
                Toast.makeText(this, "权限被拒绝，无法访问步数信息。", Toast.LENGTH_LONG).show();
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

    private void fetchHealthInfo(String email) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.168.41:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HealthInfoService service = retrofit.create(HealthInfoService.class);

        Call<HealthInfoResponse> call = service.getHealthInfo(email);
        Log.d("fetchHealthInfo", "正在发起网络请求以获取健康信息");
        call.enqueue(new Callback<HealthInfoResponse>() {
            @Override
            public void onResponse(Call<HealthInfoResponse> call, Response<HealthInfoResponse> response) {
                Log.d("onResponse", "onResponse 被调用");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("onResponse", "响应体: " + response.body().toString());
                    HealthInfoResponse healthInfoResponse = response.body();
                    if (healthInfoResponse.getCode() == 1) {
                        HealthInfo healthInfo = healthInfoResponse.getData().get(0);
                        String infoText = String.format(
                                "邮箱: %s\n\n身高: %.2f cm\n\n体重: %.2f kg\n\n血压: %s\n\n心率: %d bpm\n\n最后更新时间: %s",
                                healthInfo.getEmail(),
                                healthInfo.getHeight(),
                                healthInfo.getWeight(),
                                healthInfo.getBloodpressure(),
                                healthInfo.getHeartrate(),
                                healthInfo.getLastupdated()
                        );
                        healthInfoTextView.setText(infoText);
                    } else {
                        healthInfoTextView.setText("错误: " + healthInfoResponse.getMsg());
                    }
                } else {
                    Log.d("onResponse", "未能获取数据: " + response.errorBody());
                    healthInfoTextView.setText("未能获取数据。");
                }
            }

            @Override
            public void onFailure(Call<HealthInfoResponse> call, Throwable t) {
                Log.d("onFailure", "网络错误: " + t.getMessage());
                healthInfoTextView.setText("网络错误: " + t.getMessage());
            }
        });
    }
}
