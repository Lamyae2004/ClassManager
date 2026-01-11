package com.ensa.mobile.absence.api;

import com.ensa.mobile.utils.ApiConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AbsenceRetrofitClient {
    // Use API Gateway URL from shared configuration
    private static final String BASE_URL = ApiConfig.API_GATEWAY_BASE_URL;

    private static AbsenceRetrofitClient instance;
    private AbsenceApiService absenceApiService;

    private AbsenceRetrofitClient() {
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
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        absenceApiService = retrofit.create(AbsenceApiService.class);
    }

    public static synchronized AbsenceRetrofitClient getInstance() {
        if (instance == null) {
            instance = new AbsenceRetrofitClient();
        }
        return instance;
    }

    public AbsenceApiService getAbsenceApiService() {
        return absenceApiService;
    }
}

