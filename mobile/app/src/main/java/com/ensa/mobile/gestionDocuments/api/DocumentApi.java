package com.ensa.mobile.gestionDocuments.api;

import com.ensa.mobile.gestionDocuments.models.DocumentDto;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DocumentApi {

    @Multipart
    @POST("/api/document/upload")
    Call<ResponseBody> uploadDocument(
            @Part("title") RequestBody title,
            @Part("type") RequestBody type,
            @Part("moduleId") RequestBody moduleId,
            @Part("classeId") RequestBody classeId,
            @Part("profId") RequestBody profId,
            @Part("fileName") RequestBody fileName,
            @Part MultipartBody.Part file
    );
}

