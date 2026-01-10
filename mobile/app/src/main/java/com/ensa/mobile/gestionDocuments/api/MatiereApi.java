package com.ensa.mobile.gestionDocuments.api;

import com.ensa.mobile.gestionDocuments.models.ClasseDto;
import com.ensa.mobile.gestionDocuments.models.MatiereDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MatiereApi {
    @GET("emploi/matieres/classe/prof/{classe}/{currentUserId}")
    Call<List<MatiereDto>> getMatiereByClasseAndTeacher (
            @Path("classe") Long classeId ,
            @Path("currentUserId") Long currentUserId
    );
}
