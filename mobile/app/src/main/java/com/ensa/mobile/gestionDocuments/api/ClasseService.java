package com.ensa.mobile.gestionDocuments.api;


import static java.security.AccessController.getContext;

import android.content.Context;

import com.ensa.mobile.gestionDocuments.models.ClasseDto;
import com.ensa.mobile.gestionDocuments.models.ClasseEtudiantDto;
import com.ensa.mobile.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;

public class ClasseService {
    private UserApi api;

    private String token;
    private long studentId;


    public ClasseService(Context context) {
        api = RetrofitClient.getInstance().create(UserApi.class);
        TokenManager tokenManager = TokenManager.getInstance(context);
        token = tokenManager.getToken();
    }

    public void getClasseId(String niveau, String filiere, Callback<ClasseDto> callback) {
        Call<ClasseDto> call = RetrofitClient.getInstance().create(ClasseApi.class).getClasseByNiveauAndFiliere(niveau, filiere);
        call.enqueue(callback);
    }


    public void getClasse(Long userId, Callback<ClasseEtudiantDto> callback) {
        api.getClasse(userId,"Bearer " + token).enqueue(callback);
    }
}