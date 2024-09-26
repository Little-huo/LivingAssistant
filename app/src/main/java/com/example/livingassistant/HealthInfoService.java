package com.example.livingassistant;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HealthInfoService {
    @GET("/health/{email}")
    Call<HealthInfoResponse> getHealthInfo(@Path("email") String email);
}
