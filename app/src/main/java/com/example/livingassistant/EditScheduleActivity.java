package com.example.livingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditScheduleActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText eventEditText;
    private EditText timeEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        titleEditText = findViewById(R.id.edit_title);
        eventEditText = findViewById(R.id.edit_event);
        timeEditText = findViewById(R.id.edit_time);
        saveButton = findViewById(R.id.save_button);

        // 获取 Intent 并填充数据
        Intent intent = getIntent();
        titleEditText.setText(intent.getStringExtra("EXTRA_TITLE"));
        eventEditText.setText(intent.getStringExtra("EXTRA_EVENT"));
        timeEditText.setText(intent.getStringExtra("EXTRA_TIME"));

        // 设置保存按钮的点击事件
        saveButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("EXTRA_TITLE", titleEditText.getText().toString());
            resultIntent.putExtra("EXTRA_EVENT", eventEditText.getText().toString());
            resultIntent.putExtra("EXTRA_TIME", timeEditText.getText().toString());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
