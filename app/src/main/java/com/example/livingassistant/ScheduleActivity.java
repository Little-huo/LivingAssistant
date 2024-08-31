package com.example.livingassistant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ScheduleActivity extends AppCompatActivity implements ScheduleAdapter.OnDeleteClickListener {

    private static final int REQUEST_CODE_DETAIL = 1;
    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String SCHEDULE_KEY = "ScheduleList";

    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<ScheduleItem> scheduleList;
    private SharedPreferences sharedPreferences;
    private Set<String> scheduleSet;
    private TextView noSchedulesText;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        scheduleRecyclerView = findViewById(R.id.schedule_recycler_view);
        Button addScheduleButton = findViewById(R.id.add_schedule_button);
        noSchedulesText = findViewById(R.id.no_schedules_text);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        scheduleSet = sharedPreferences.getStringSet(SCHEDULE_KEY, new HashSet<>());

        scheduleList = new ArrayList<>();
        loadSchedules();

        scheduleAdapter = new ScheduleAdapter(scheduleList, this);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(scheduleAdapter);

        addScheduleButton.setOnClickListener(v -> openCreateScheduleDialog());
    }

    private void openCreateScheduleDialog() {
        Log.d("openCreateScheduleDialog", "openCreateScheduleDialog: ...");

        // 加载自定义布局
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_schedule, null);

        final EditText titleInput = dialogView.findViewById(R.id.schedule_title);
        final EditText eventInput = dialogView.findViewById(R.id.schedule_event);

        // 创建一个 AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_schedule)  // 使用中文标题
                .setView(dialogView)  // 将自定义布局添加到对话框中
                .setPositiveButton(R.string.ok, (dialog, which) -> {  // 使用中文按钮文本
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
                            String scheduleTime = dateFormat.format(calendar.getTime());  // 格式化时间

                            if (!title.isEmpty() && !event.isEmpty()) {
                                ScheduleItem newItem = new ScheduleItem(title, event, scheduleTime);
                                scheduleList.add(newItem);
                                saveSchedule(newItem);
                                scheduleAdapter.notifyDataSetChanged();
                                updateNoSchedulesTextVisibility();
                            } else {
                                Log.d("openCreateScheduleDialog", "Title or event text is empty.");
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // 用户点击 Cancel 按钮时关闭对话框
                    dialog.dismiss();
                });

        // 显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadSchedules() {
        for (String scheduleString : scheduleSet) {
            String[] parts = scheduleString.split(";");
            if (parts.length == 3) {
                scheduleList.add(new ScheduleItem(parts[0], parts[1], parts[2]));
            }
        }
        updateNoSchedulesTextVisibility();
    }

    private void saveSchedule(ScheduleItem item) {
        Log.d("saveSchedule", "saveSchedule: ...");
        scheduleSet.add(item.getTitle() + ";" + item.getEvent() + ";" + item.getTime());
        sharedPreferences.edit().putStringSet(SCHEDULE_KEY, scheduleSet).apply();
    }

    private void removeSchedule(ScheduleItem item) {
        scheduleSet.remove(item.getTitle() + ";" + item.getEvent() + ";" + item.getTime());
        sharedPreferences.edit().putStringSet(SCHEDULE_KEY, scheduleSet).apply();
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
}
