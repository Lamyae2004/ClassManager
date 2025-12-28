package com.ensa.mobile.gestionDocuments.api;


import com.ensa.mobile.gestionDocuments.models.MatiereProfDto;

import java.util.List;

import retrofit2.Callback;

public class EmploiService {
    private EmploiApi api;

    public EmploiService() {
        api = RetrofitClient.getInstance().create(EmploiApi.class);
    }

    public void getMatieresEtProfs(String niveau, String filiere, Callback<List<MatiereProfDto>> callback) {
        api.getMatieresEtProfs(niveau, filiere).enqueue(callback);
    }
}