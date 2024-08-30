package com.example.livingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView eventTextView;
    private TextView timeTextView;
    private Button editButton;
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
        editButton = findViewById(R.id.edit_button);
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
        timeTextView.setText(time);

        // 设置编辑按钮的点击事件
        editButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(DetailActivity.this, EditScheduleActivity.class);
            editIntent.putExtra("EXTRA_ID", id);  // 传递 ID
            editIntent.putExtra("EXTRA_TITLE", title);
            editIntent.putExtra("EXTRA_EVENT", event);
            editIntent.putExtra("EXTRA_TIME", time);
            startActivity(editIntent);
        });

        // 设置删除按钮的点击事件
        deleteButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("DELETE_ID", id);  // 传递 ID
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
