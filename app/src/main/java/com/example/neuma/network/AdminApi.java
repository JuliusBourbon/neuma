package com.example.neuma.network;

import com.example.neuma.models.Material;
import com.example.neuma.models.Question;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AdminApi {
    @PUT("admin/materials/{id}")
    Call<Material> updateMaterial(@Path("id") String id, @Body Map<String, Object> body);

    @PUT("admin/questions/{id}")
    Call<Question> updateQuestion(@Path("id") String id, @Body Map<String, Object> body);
}
