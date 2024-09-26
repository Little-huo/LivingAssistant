package com.example.livingassistant;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity implements ScheduleAdapter.OnDeleteClickListener {

    private static final String PREFS_NAME = "user_prefs";
    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private TextView noSchedulesText;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        scheduleRecyclerView = findViewById(R.id.schedule_recycler_view);
        Button addScheduleButton = findViewById(R.id.add_schedule_button);
        noSchedulesText = findViewById(R.id.no_schedules_text);

        // 从 SharedPreferences 中获取当前登录的用户 email
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("current_user_email", null);

        scheduleList = new ArrayList<>();
        loadSchedules(currentUserEmail);

        scheduleAdapter = new ScheduleAdapter(scheduleList, this);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(scheduleAdapter);

        addScheduleButton.setOnClickListener(v -> openCreateScheduleDialog());
    }

    private void openCreateScheduleDialog() {
        // 加载自定义布局
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_schedule, null);

        final EditText titleInput = dialogView.findViewById(R.id.schedule_title);
        final EditText eventInput = dialogView.findViewById(R.id.schedule_event);

        // 创建一个 AlertDialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.create_schedule)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        new TimePickerDialog(this, (timePicker, hour, minute) -> {
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);

                            String title = titleInput.getText().toString().trim();
                            String event = eventInput.getText().toString().trim();
                            String scheduleTime = dateFormat.format(calendar.getTime());

                            if (!title.isEmpty() && !event.isEmpty()) {
                                ScheduleItem newItem = new ScheduleItem(currentUserEmail, title, event, scheduleTime);
                                scheduleList.add(newItem);
                                saveSchedule(newItem);
                                scheduleAdapter.notifyDataSetChanged();
                                updateNoSchedulesTextVisibility();
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadSchedules(String email) {
        ScheduleApi api = ApiClient.getClient().create(ScheduleApi.class);
        Call<List<ScheduleItem>> call = api.getSchedulesByEmail(email);

        call.enqueue(new Callback<List<ScheduleItem>>() {
            @Override
            public void onResponse(Call<List<ScheduleItem>> call, Response<List<ScheduleItem>> response) {
                if (response.isSuccessful()) {
                    scheduleList.clear();
                    scheduleList.addAll(response.body());
                    scheduleAdapter.notifyDataSetChanged();
                    updateNoSchedulesTextVisibility();
                } else {
                    Log.e("API Error", "Error fetching schedules");
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleItem>> call, Throwable t) {
                Log.e("API Error", "Failed to fetch schedules", t);
            }
        });
    }


private void saveSchedule(ScheduleItem newItem) {
    ScheduleApi api = ApiClient.getClient().create(ScheduleApi.class);
    Call<Void> call = api.addSchedule(newItem);

    call.enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            // Print request URL, headers, and body
            Log.d("API Request", "URL: " + call.request().url());
            Log.d("API Request", "Headers: " + call.request().headers());
            Log.d("API Request", "Request body: " + call.request().body());

            if (response.isSuccessful()) {
                Log.d("API", "Schedule added successfully");

                // 在日程保存成功后，设置提醒
                scheduleReminder(newItem);  // 这里调用提醒设置的方法
            } else {
                Log.e("API Error", "Error adding schedule: " + response.code() + " " + response.message());
                try {
                    if (response.errorBody() != null) {
                        Log.e("API Error", "Response error body: " + response.errorBody().string());
                    }
                } catch (IOException e) {
                    Log.e("API Error", "Error reading response error body", e);
                }
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.e("API Error", "Failed to add schedule", t);
        }
    });
}


    private void removeSchedule(ScheduleItem item) {
        ScheduleApi api = ApiClient.getClient().create(ScheduleApi.class);
        Call<Void> call = api.deleteSchedule(item.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Schedule deleted successfully");
                } else {
                    Log.e("API Error", "Error deleting schedule");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API Error", "Failed to delete schedule", t);
            }
        });
    }

    @Override
    public void onDeleteClick(ScheduleItem item) {
        removeSchedule(item);
        scheduleList.remove(item);
        scheduleAdapter.notifyDataSetChanged();
        updateNoSchedulesTextVisibility();
    }

    private void updateNoSchedulesTextVisibility() {
        if (scheduleList.isEmpty()) {
            noSchedulesText.setVisibility(View.VISIBLE);
        } else {
            noSchedulesText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ScheduleAdapter.REQUEST_CODE_DETAIL && resultCode == RESULT_OK) {
            int deleteId = data.getIntExtra("DELETE_ID", -1);
            if (deleteId != -1) {
                ScheduleItem itemToDelete = null;
                for (ScheduleItem item : scheduleList) {
                    if (item.getId() == deleteId) {
                        itemToDelete = item;
                        break;
                    }
                }
                if (itemToDelete != null) {
                    onDeleteClick(itemToDelete);
                }
            }
        }
    }

    private void scheduleReminder(ScheduleItem item) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 创建一个 PendingIntent，触发 BroadcastReceiver 来显示通知
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("event", item.getEvent());

//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE // 使用 FLAG_IMMUTABLE 标志
        );
        // 设置提醒时间
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(item.getTime()));  // 根据日程时间设置提醒
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置定时提醒，考虑 API 版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }


    }

}
