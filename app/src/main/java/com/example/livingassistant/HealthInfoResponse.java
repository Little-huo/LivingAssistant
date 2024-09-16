package com.example.livingassistant;

import java.util.List;

public class HealthInfoResponse {
    private int code;
    private String msg;
    private List<HealthInfo> data;

    // Getters and Setters

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<HealthInfo> getData() {
        return data;
    }

    public void setData(List<HealthInfo> data) {
        this.data = data;
    }
}
