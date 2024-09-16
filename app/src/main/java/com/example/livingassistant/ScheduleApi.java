package com.example.livingassistant;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

//public interface ScheduleApi {
//
//    @GET("/schedules/{email}")
//    Call<List<ScheduleItem>> getSchedulesByEmail(@Path("email") String email);
//
//    @POST("/schedules/add")
//    Call<Void> addSchedule(@Body ScheduleItem scheduleItem);
//
//    @DELETE("/schedules/{scheduleId}")
//    Call<Void> deleteSchedule(@Path("scheduleId") int scheduleId);
//}
public interface ScheduleApi {
    @POST("schedules/add")
    Call<Void> addSchedule(@Body ScheduleItem scheduleItem);

    @GET("schedules")
    Call<List<ScheduleItem>> getSchedulesByEmail(@Query("email") String email);

    @DELETE("schedules/{id}")
    Call<Void> deleteSchedule(@Path("id") Integer id);
}
