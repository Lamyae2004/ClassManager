package com.ensa.mobile.gestionDocuments.api;

import com.ensa.mobile.gestionDocuments.models.ClasseDto;
import com.ensa.mobile.gestionDocuments.models.ClasseEtudiantDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserApi {
    @GET("api/users/{id}/classe")
    Call<ClasseEtudiantDto> getClasse(@Path("id") Long userId);
}

