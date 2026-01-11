package com.ensa.mobile.gestionDocuments.api;


import com.ensa.mobile.gestionDocuments.models.ClasseDto;
import com.ensa.mobile.gestionDocuments.models.ClasseEtudiantDto;

import retrofit2.Call;
import retrofit2.Callback;

public class ClasseService {
    private UserApi api;


    public ClasseService() {
        api = RetrofitClient.getInstance().create(UserApi.class);
    }

    public void getClasseId(String niveau, String filiere, Callback<ClasseDto> callback) {
        Call<ClasseDto> call = RetrofitClient.getInstance().create(ClasseApi.class).getClasseByNiveauAndFiliere(niveau, filiere);
        call.enqueue(callback);
    }


    public void getClasse(Long userId, Callback<ClasseEtudiantDto> callback) {
        api.getClasse(userId).enqueue(callback);
    }
}