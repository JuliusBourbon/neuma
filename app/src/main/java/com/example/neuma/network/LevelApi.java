package com.example.neuma.network;

import com.example.neuma.models.Level;
import com.example.neuma.models.LevelDetailResponse;
import com.example.neuma.models.Material;
import com.example.neuma.models.Question;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

import retrofit2.http.Path;

public interface LevelApi {
    @GET("levels")
    Call<List<Level>> getLevels();

    @GET("levels/{id}")
    Call<LevelDetailResponse> getLevelDetail(@Path("id") String id);

    @GET("levels/{id}/materials")
    Call<List<Material>> getMaterials(@Path("id") String id);

    @GET("levels/{id}/questions")
    Call<List<Question>> getQuestions(@Path("id") String id);
}
