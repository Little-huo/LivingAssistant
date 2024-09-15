package com.example.livingassistant;

public class ScheduleItem {
    private static int idCounter = 0;

    private int id;
    private String title;
    private String event;
    private String time;

    public ScheduleItem(String title, String event, String time) {
        this.id = idCounter++;
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
