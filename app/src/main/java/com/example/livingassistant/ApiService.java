package com.example.livingassistant;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/add")
    Call<Void> register(@Body User user);

    @POST("/login")
    Call<Void> login(@Body LoginRequest loginRequest);

    @POST("/updatePassword")
    Call<Void> updatePassword(@Body PasswordUpdateDTO passwordUpdateDTO);
}
