package com.ensa.mobile.gestionDocuments.api;

import com.ensa.mobile.gestionDocuments.models.MatiereProfDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EmploiApi {
    @GET("emploi/classes/matieres-profs")
    Call<List<MatiereProfDto>> getMatieresEtProfs(
            @Query("niveau") String niveau,
            @Query("filiere") String filiere
    );
}