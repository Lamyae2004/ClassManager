package com.ensa.mobile.gestionDocuments.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MatiereRetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // backend
    private static MatiereRetrofitClient instance;

    private Retrofit retrofit;

    private     MatiereRetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized MatiereRetrofitClient getInstance() {
        if (instance == null) {
            instance = new MatiereRetrofitClient();
        }
        return instance;
    }

    public MatiereApi getMatiereApi() {
        return retrofit.create(MatiereApi.class);
    }
}
