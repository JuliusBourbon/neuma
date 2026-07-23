package com.example.neuma.network;

import com.example.neuma.models.UpdatePasswordRequest;
import com.example.neuma.models.UpdateProfileRequest;
import com.example.neuma.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApi {
    @GET("users/me")
    Call<User> getProfile();

    @PUT("users/profile")
    Call<User> updateProfile(@Body UpdateProfileRequest request);

    @PUT("users/password")
    Call<Void> updatePassword(@Body UpdatePasswordRequest request);
}
