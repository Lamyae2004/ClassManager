package com.ensa.mobile.absence.api;

import com.ensa.mobile.absence.models.AbsenceResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AbsenceApiService {
    @GET("absences/student/{etudiantId}")
    Call<List<AbsenceResponse>> getAbsencesByStudent(@Path("etudiantId") Long etudiantId);

    @Multipart
    @POST("absences/{absenceId}/justify")
    Call<AbsenceResponse> uploadJustification(
            @Path("absenceId") Long absenceId,
            @Part MultipartBody.Part file
    );
}

