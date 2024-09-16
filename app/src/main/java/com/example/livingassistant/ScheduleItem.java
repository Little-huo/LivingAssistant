package com.example.livingassistant;

public class ScheduleItem {
    private int id;
    private String email;
    private String title;
    private String event;
    private String time;

    public ScheduleItem(String email, String title, String event, String time) {
        this.email = email;
        this.title = title;
        this.event = event;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
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
