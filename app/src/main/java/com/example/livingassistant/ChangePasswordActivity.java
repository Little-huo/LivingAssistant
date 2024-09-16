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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextOldPassword, editTextNewPassword, editTextConfirmPassword;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // 初始化视图
        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        submitButton = findViewById(R.id.submitButton);

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
            // 调用后端 API 更改密码
            updatePassword(currentUserEmail, oldPassword, newPassword);
        } else {
            Toast.makeText(ChangePasswordActivity.this, "用户未登录", Toast.LENGTH_SHORT).show();
        }
    }

    // 调用后端 API 更改密码
    private void updatePassword(String email, String oldPassword, String newPassword) {
        PasswordUpdateDTO passwordUpdateDTO = new PasswordUpdateDTO(email, oldPassword, newPassword);

        // 使用 Retrofit 调用后端 API
        RetrofitClient.getInstance().getApiService().updatePassword(passwordUpdateDTO).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "密码更改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "密码更改失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "网络请求失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 获取当前用户的邮箱
    private String getCurrentUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getString("current_user_email", null);
    }
}
