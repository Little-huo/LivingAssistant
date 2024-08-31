package com.example.livingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        // 初始化UI组件
        View changePasswordCard = findViewById(R.id.change_password_card);

        // 设置点击事件
        changePasswordCard.setOnClickListener(v -> {
            Intent intent = new Intent(MoreActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }
}
