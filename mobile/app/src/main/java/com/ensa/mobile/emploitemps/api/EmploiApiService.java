package com.ensa.mobile.emploitemps.api;

import com.ensa.mobile.emploitemps.models.EmploiDuTempsDTO;
import com.ensa.mobile.emploitemps.models.EmploiEtudiantResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EmploiApiService {

    @GET("emploi/student")
    Call<List<EmploiDuTempsDTO>> getEmploiForStudent(
          
    );


    @GET("emploi/student/{id}")
    Call<EmploiEtudiantResponse> getEmploiByStudentId(
            @Path("id") Long studentId
    );

}
