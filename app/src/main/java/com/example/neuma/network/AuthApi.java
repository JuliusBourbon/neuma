package com.example.neuma.network;

import com.example.neuma.models.AuthResponse;
import com.example.neuma.models.LoginRequest;
import com.example.neuma.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
}
