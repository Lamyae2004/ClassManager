package com.ensa.mobile.authentification.api;

import com.ensa.mobile.authentification.models.AuthenticationRequest;
import com.ensa.mobile.authentification.models.AuthenticationResponse;
import com.ensa.mobile.authentification.models.ChangePassword;
import com.ensa.mobile.authentification.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthApiService {

    @POST("api/v1/auth/authenticate")
    Call<AuthenticationResponse> authenticate(@Body AuthenticationRequest request);

    @POST("api/v1/auth/forgot-password/request/{email}")
    Call<String> requestForgotPassword(@Path("email") String email);

    @POST("api/v1/auth/forgot-password/verify/{email}/{otp}")
    Call<String> verifyForgotPasswordOtp(@Path("email") String email, @Path("otp") int otp);

    @POST("api/v1/auth/forgot-password/change/{email}")
    Call<String> changePassword(@Path("email") String email, @Body ChangePassword changePassword);

    @POST("api/v1/auth/validate-account/request/{email}")
    Call<String> requestValidateAccount(@Path("email") String email);

    @POST("api/v1/auth/validate-account/verify/{email}/{otp}")
    Call<String> verifyAccountActivation(@Path("email") String email, @Path("otp") int otp);

    @POST("api/v1/auth/validate-account/set-password/{email}")
    Call<String> activateAccount(@Path("email") String email, @Body ChangePassword changePassword);

    @GET("api/users/profile")
    Call<UserResponse> getProfile(@Header("Authorization") String token);
}

