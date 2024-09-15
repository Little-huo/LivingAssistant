//package com.example.livingassistant;
//
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class RetrofitClient {
//
//    private static final String BASE_URL = "http://192.168.137.126:8080/"; // 替换为你的后端 API 地址
//    private static RetrofitClient instance;
//    private ApiService apiService;
//
//    private RetrofitClient() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        apiService = retrofit.create(ApiService.class);
//    }
//
//    public static synchronized RetrofitClient getInstance() {
//        if (instance == null) {
//            instance = new RetrofitClient();
//        }
//        return instance;
//    }
//
//    public ApiService getApiService() {
//        return apiService;
//    }
//}

package com.example.livingassistant;

import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.168.41:8080/"; // 替换为你的后端 API 地址
    private static RetrofitClient instance;
    private ApiService apiService;
    private static final String TAG = "RetrofitClient"; // 日志标签

    private RetrofitClient() {
        try {
            Log.d(TAG, "Initializing Retrofit...");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
            Log.d(TAG, "Retrofit initialized successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Retrofit: " + e.getMessage());
        }
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            Log.d(TAG, "Creating a new instance of RetrofitClient.");
            instance = new RetrofitClient();
        } else {
            Log.d(TAG, "Returning existing RetrofitClient instance.");
        }
        return instance;
    }

    public ApiService getApiService() {
        if (apiService != null) {
            Log.d(TAG, "Returning ApiService instance.");
        } else {
            Log.e(TAG, "ApiService is null. Retrofit initialization might have failed.");
        }
        return apiService;
    }
}

