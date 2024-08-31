package com.example.livingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView eventTextView;
    private TextView timeTextView;
    private Button deleteButton;

    private int id;
    private String title;
    private String event;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        titleTextView = findViewById(R.id.detail_title);
        eventTextView = findViewById(R.id.detail_event);
        timeTextView = findViewById(R.id.detail_time);
        deleteButton = findViewById(R.id.delete_button);

        // 获取 Intent 并提取数据
        Intent intent = getIntent();
        id = intent.getIntExtra("EXTRA_ID", -1);  // 获取 ID
        title = intent.getStringExtra("EXTRA_TITLE");
        event = intent.getStringExtra("EXTRA_EVENT");
        time = intent.getStringExtra("EXTRA_TIME");

        // 显示数据
        titleTextView.setText(title);
        eventTextView.setText(event);
        timeTextView.setText(formatTime(time));  // 格式化时间

        // 设置删除按钮的点击事件
        deleteButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("DELETE_ID", id);  // 传递 ID
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // 格式化时间字符串的方法
    private String formatTime(String timeString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = inputFormat.parse(timeString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timeString;  // 返回原始时间字符串作为回退
        }
    }
}
