package com.example.pizzaapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // IP đặc biệt dành cho Android Emulator để trỏ về máy tính
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson để dịch JSON
                    .build();
        }
        return retrofit;
    }
}