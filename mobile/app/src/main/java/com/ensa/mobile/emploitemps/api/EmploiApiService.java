package com.ensa.mobile.emploitemps.api;

import com.ensa.mobile.emploitemps.models.EmploiDuTempsDTO;
import com.ensa.mobile.emploitemps.models.EmploiEtudiantResponse;
import com.ensa.mobile.emploitemps.models.EmploiProfDTO;
import com.ensa.mobile.emploitemps.models.SeanceActionRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
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

    @GET("emploi/prof/{id}")
    Call<List<EmploiProfDTO>> getEmploiByProfId(@Path("id") Long profId);

    @POST("emploi/actions/declare")
    Call<Map<String, Object>> declareAction(@Body SeanceActionRequest request);
}
