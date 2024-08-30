package com.example.livingassistant;

public class ScheduleItem {
    private static int idCounter = 0;  // 用于生成唯一的 ID

    private int id;
    private String title;
    private String event;
    private String time;

    public ScheduleItem(String title, String event, String time) {
        this.id = idCounter++;  // 为每个新项分配一个唯一 ID
        this.title = title;
        this.event = event;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEvent() {
        return event;
    }

    public String getTime() {
        return time;
    }
}
