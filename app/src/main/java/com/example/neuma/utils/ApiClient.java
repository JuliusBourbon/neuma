package com.example.neuma.utils;

import android.content.Context;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.1.87:3000/api/";
    private static Retrofit retrofit = null;

    // Client Publik (Login / Register)
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Client Terautentikasi (Daftar Level, Profil, dll)
    public static Retrofit getAuthClient(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();

                    // Panggil TokenManager di dalam Interceptor agar selalu mengambil token terbaru dari SharedPreferences
                    TokenManager tokenManager = new TokenManager(context.getApplicationContext());
                    String token = tokenManager.getToken();

                    if (token != null && !token.isEmpty()) {
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(request);
                    }
                    return chain.proceed(original);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
