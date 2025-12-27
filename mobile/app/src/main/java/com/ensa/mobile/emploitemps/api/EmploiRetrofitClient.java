package com.ensa.mobile.emploitemps.api;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmploiRetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/"; // backend
    private static EmploiRetrofitClient instance;
    private EmploiApiService emploiApiService;

    private EmploiRetrofitClient() {
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

        emploiApiService = retrofit.create(EmploiApiService.class);
    }

    public static synchronized EmploiRetrofitClient getInstance() {
        if (instance == null) instance = new EmploiRetrofitClient();
        return instance;
    }

    public EmploiApiService getEmploiApiService() {
        return emploiApiService;
    }
}
