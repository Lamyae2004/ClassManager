package com.ensa.mobile.gestionDocuments.api;


import com.ensa.mobile.gestionDocuments.models.ClasseDto;
import com.ensa.mobile.gestionDocuments.models.ClasseEtudiantDto;

import retrofit2.Callback;

public class ClasseService {
    private UserApi api;

    public ClasseService() {
        api = RetrofitClient.getInstance().create(UserApi.class);
    }

    public void getClasse(Long userId, Callback<ClasseEtudiantDto> callback) {
        api.getClasse(userId).enqueue(callback);
    }
}