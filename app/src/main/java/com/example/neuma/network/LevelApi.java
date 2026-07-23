package com.example.neuma.network;

import com.example.neuma.models.Level;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface LevelApi {
    @GET("levels")
    Call<List<Level>> getLevels();
}
