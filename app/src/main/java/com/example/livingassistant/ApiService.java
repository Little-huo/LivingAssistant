package com.example.livingassistant;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/auth/add")
    Call<Result> register(@Body User user);

    @POST("/auth/login")
    Call<Result> login(@Body LoginRequest loginRequest);

//    @POST("/updatePassword")
//    Call<Result> updatePassword(@Body PasswordUpdateDTO passwordUpdateDTO);

    @POST("/auth/updatePassword")
    Call<Void> updatePassword(@Body PasswordUpdateDTO passwordUpdateDTO);

}
