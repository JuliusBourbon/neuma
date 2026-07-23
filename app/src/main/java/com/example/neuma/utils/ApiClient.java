package com.example.neuma.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Ubah BASE_URL ini sesuai dengan IP komputer/localhost Anda saat testing di device fisik (misalnya 192.168.x.x)
    // Jika menggunakan emulator Android Studio, Anda bisa gunakan http://10.0.2.2:3000/api/
    private static final String BASE_URL = "http://192.168.1.2:3000/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
