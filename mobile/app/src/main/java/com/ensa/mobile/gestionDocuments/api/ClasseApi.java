package com.ensa.mobile.gestionDocuments.api;

import com.ensa.mobile.gestionDocuments.models.ClasseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ClasseApi {


    @GET("emploi/classes/search")
    Call<ClasseDto> getClasseByNiveauAndFiliere(
            @Query("niveau") String niveau,
            @Query("filiere") String filiere
    );
    @GET("emploi/classes/prof/{profId}")
    Call<List<ClasseDto>> getClassesByProfessor(
            @Path("profId") Long profId
    );
}
