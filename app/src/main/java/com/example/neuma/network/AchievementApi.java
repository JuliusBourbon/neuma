package com.example.neuma.network;

import com.example.neuma.models.Achievement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AchievementApi {
    @GET("achievements")
    Call<List<Achievement>> getAchievements();
}
