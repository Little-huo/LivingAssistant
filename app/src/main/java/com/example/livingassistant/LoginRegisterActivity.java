package com.example.livingassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginRegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private TextView switchText;
    private Button submitButton;
    private boolean isLoginMode = true;

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // 初始化视图
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        switchText = findViewById(R.id.switchText);
        submitButton = findViewById(R.id.submitButton);

        dbHelper = new UserDatabaseHelper(this);

        // 切换登录和注册模式
        switchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoginMode) {
                    isLoginMode = false;
                    editTextConfirmPassword.setVisibility(View.VISIBLE);
                    submitButton.setText("注册");
                    switchText.setText("已有账号？登录");
                } else {
                    isLoginMode = true;
                    editTextConfirmPassword.setVisibility(View.GONE);
                    submitButton.setText("登录");
                    switchText.setText("没有账号？注册");
                }
            }
        });

        // 提交按钮点击事件
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSubmit();
            }
        });
    }

    private void handleSubmit() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginRegisterActivity.this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isLoginMode && !password.equals(confirmPassword)) {
            Toast.makeText(LoginRegisterActivity.this, "密码不匹配", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isLoginMode) {
            loginUser(email, password);
        } else {
            registerUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        if (dbHelper.checkUser(email, password)) {
            // 登录成功，存储用户信息
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("current_user_email", email);
            editor.apply();

            Toast.makeText(LoginRegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginRegisterActivity.this, "登录失败，邮箱或密码错误", Toast.LENGTH_SHORT).show();
        }
    }


    private void registerUser(String email, String password) {
        long result = dbHelper.addUser(email, password);
        if (result != -1) {
            Toast.makeText(LoginRegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginRegisterActivity.this, LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginRegisterActivity.this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

}
