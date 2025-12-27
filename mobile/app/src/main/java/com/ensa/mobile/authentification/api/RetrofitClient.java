package com.ensa.mobile.authentification.api;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    // Change this to your backend URL
    // Use 10.0.2.2 for Android Emulator
    // For physical device, use your computer's IP address: "http://192.168.x.x:9090/"
    private static final String BASE_URL = "http://10.0.2.2:9090/";

    private static RetrofitClient instance;
    private AuthApiService authApiService;

    private RetrofitClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create()) // For String responses
                .addConverterFactory(GsonConverterFactory.create()) // For JSON responses
                .build();

        authApiService = retrofit.create(AuthApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public AuthApiService getAuthApiService() {
        return authApiService;
    }
}

