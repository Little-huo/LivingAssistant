// SplashActivity.java
package com.example.livingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);
        TextView appName = findViewById(R.id.app_name);
        TextView appDescription = findViewById(R.id.app_description);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
        Animation move = AnimationUtils.loadAnimation(this, R.anim.move);

        logo.startAnimation(fadeIn);
        appName.startAnimation(scale);
        appDescription.startAnimation(move);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 跳转到主界面
                Intent intent = new Intent(SplashActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
                // 结束引导页
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
