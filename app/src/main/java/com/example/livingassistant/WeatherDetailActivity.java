package com.example.livingassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

public class WeatherDetailActivity extends AppCompatActivity {

    private TextView cityProvinceTextView;
    private TextView weatherDescriptionTextView;
    private TextView temperatureTextView;
    private TextView windTextView;
    private TextView humidityTextView;
    private TextView reportTimeTextView;
    private ImageView weatherIconImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        cityProvinceTextView = findViewById(R.id.city_province_text);
        weatherDescriptionTextView = findViewById(R.id.weather_description_text);
        temperatureTextView = findViewById(R.id.temperature_text);
        windTextView = findViewById(R.id.wind_text);
        humidityTextView = findViewById(R.id.humidity_text);
        reportTimeTextView = findViewById(R.id.report_time_text);
        weatherIconImageView = findViewById(R.id.weather_icon);

        // 从 Intent 中获取天气信息
        String weatherDataString = getIntent().getStringExtra("weather_data");
        if (weatherDataString != null) {
            try {
                JSONObject weatherData = new JSONObject(weatherDataString);

                String province = weatherData.getString("province");
                String city = weatherData.getString("city");
                String weather = weatherData.getString("weather");
                String temperature = weatherData.getString("temperature");
                String windDirection = weatherData.getString("winddirection");
                String windPower = weatherData.getString("windpower");
                String humidity = weatherData.getString("humidity");
                String reportTime = weatherData.getString("reporttime");

                // 设置文本内容
                cityProvinceTextView.setText(String.format("%s, %s", city, province));
                weatherDescriptionTextView.setText(weather);
                temperatureTextView.setText(String.format("%s°C", temperature));
                windTextView.setText(String.format("风向: %s\n风力: %s", windDirection, windPower));
                humidityTextView.setText(String.format("湿度: %s%%", humidity));
                reportTimeTextView.setText(String.format("更新时间: %s", reportTime));
                int weatherIconResId = getWeatherIconResourceId(weather);
                weatherIconImageView.setImageResource(weatherIconResId);

            } catch (Exception e) {
                e.printStackTrace();
            }
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

}
