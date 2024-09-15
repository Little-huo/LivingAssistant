package com.example.livingassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextOldPassword, editTextNewPassword, editTextConfirmPassword;
    private Button submitButton;

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // 初始化视图
        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        submitButton = findViewById(R.id.submitButton);

        dbHelper = new UserDatabaseHelper(this);

        // 提交按钮点击事件
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSubmit();
            }
        });
    }

    private void handleSubmit() {
        String oldPassword = editTextOldPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "新密码和确认密码不匹配", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail != null) {
            if (dbHelper.checkUser(currentUserEmail, oldPassword)) {
                if (dbHelper.updatePassword(currentUserEmail, newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "密码更改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "密码更改失败，请重试", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ChangePasswordActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ChangePasswordActivity.this, "用户未登录", Toast.LENGTH_SHORT).show();
        }
    }

    // 获取当前用户的邮箱
    private String getCurrentUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("current_user_email", null);
    }
}
