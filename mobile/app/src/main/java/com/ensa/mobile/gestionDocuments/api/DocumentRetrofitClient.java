package com.ensa.mobile.gestionDocuments.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DocumentRetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // backend
    private static DocumentRetrofitClient instance;

    private Retrofit retrofit;

    private DocumentRetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized DocumentRetrofitClient getInstance() {
        if (instance == null) {
            instance = new DocumentRetrofitClient();
        }
        return instance;
    }

    public DocumentApi getDocumentApi() {
        return retrofit.create(DocumentApi.class);
    }

}
