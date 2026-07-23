package com.example.neuma.network;

import com.example.neuma.models.OnboardingRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OnboardingApi {
    @POST("onboarding")
    Call<Void> submitOnboarding(@Body OnboardingRequest request);
}
