package com.ensa.mobile.gestionDocuments.api;

import com.ensa.mobile.gestionDocuments.models.Document;

import java.util.List;

import retrofit2.Callback;

public class DocumentService {

    private final DocumentApi api;

    public DocumentService() {
        api = RetrofitClient.getInstance().create(DocumentApi.class);
    }

    public void getDocuments(Long classeId, Long moduleId, String type,
                             Callback<List<Document>> callback) {
        api.getDocuments(classeId, moduleId, type).enqueue(callback);
    }
}
