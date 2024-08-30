package com.example.livingassistant;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // 构造RemoteViews对象
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);

        // 设置点击事件以刷新天气
        Intent intent = new Intent(context, WeatherWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_weather_icon, pendingIntent);

        // 更新天气信息
        new FetchWeatherTask(context, appWidgetManager, views, appWidgetId).execute("https://restapi.amap.com/v3/weather/weatherInfo?city=340209&key=0ad5ffa34b53882a416cf8656d6aeae1");
    }

    private static class FetchWeatherTask extends AsyncTask<String, Void, String> {
        private Context context;
        private AppWidgetManager appWidgetManager;
        private RemoteViews views;
        private int appWidgetId;

        FetchWeatherTask(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.views = views;
            this.appWidgetId = appWidgetId;
        }

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

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // 解析JSON数据
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray livesArray = jsonObject.getJSONArray("lives");
                    if (livesArray.length() > 0) {
                        JSONObject weatherData = livesArray.getJSONObject(0);

                        String weather = weatherData.getString("weather");
                        String temperature = weatherData.getString("temperature");

                        views.setTextViewText(R.id.widget_weather_text, weather);
                        views.setTextViewText(R.id.widget_temperature_text, temperature + "°C");

                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
