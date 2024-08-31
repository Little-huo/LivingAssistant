package com.example.livingassistant;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView weatherTextView;
    private TextView temperatureTextView;
    private ImageView weatherIconImageView;

    private JSONObject weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }



        View weatherCard = findViewById(R.id.weather_card);
        View scheduleCard = findViewById(R.id.schedule_card);
        View healthCard = findViewById(R.id.health_card);

        ImageButton moreButton = findViewById(R.id.more_button);
        moreButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoreActivity.class);
            startActivity(intent);
        });

        weatherTextView = findViewById(R.id.weather_text);
        temperatureTextView = findViewById(R.id.temperature_text);
        weatherIconImageView = findViewById(R.id.tian_qi);

        applyAnimation(weatherCard, R.anim.card_animation);
        applyAnimation(scheduleCard, R.anim.card_animation);
        applyAnimation(healthCard, R.anim.card_animation);

        // 获取天气数据
        new FetchWeatherTask().execute("https://restapi.amap.com/v3/weather/weatherInfo?city=340209&key=0ad5ffa34b53882a416cf8656d6aeae1");

//         设置点击事件
        weatherCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
            if (weatherData != null) {
                intent.putExtra("weather_data", weatherData.toString());
            }
            startActivity(intent);
        });

        scheduleCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        healthCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StepCounterActivity.class);
            startActivity(intent);
        });
    }

    private void applyAnimation(View view, int animatorResId) {
        AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(this, animatorResId);
        animator.setTarget(view);
        animator.start();
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return result.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private int getWeatherIconResourceId(String weather) {
            switch (weather) {
                case "晴":
                    return R.drawable.ic_qingtian;
                case "多云":
                    return R.drawable.ic_duoyun;
                case "雨天":
                    return R.drawable.ic_yu;
                case "雪天":
                    return R.drawable.ic_xue;
                default:
                    return R.drawable.ic_tq; // 默认图标
            }
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // 解析JSON数据
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray livesArray = jsonObject.getJSONArray("lives");
                    if (livesArray.length() > 0) {
                        weatherData = livesArray.getJSONObject(0);

                        String weather = weatherData.getString("weather");
                        String temperature = weatherData.getString("temperature");

                        // 更新UI
                        weatherTextView.setText(weather);
                        temperatureTextView.setText(temperature);
                        int weatherIconResId = getWeatherIconResourceId(weather);
                        weatherIconImageView.setImageResource(weatherIconResId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
