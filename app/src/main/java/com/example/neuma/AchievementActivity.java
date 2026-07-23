package com.example.neuma;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.models.Achievement;
import com.example.neuma.network.AchievementApi;
import com.example.neuma.utils.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AchievementActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView rvAchievements;
    private AchievementApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        progressBar = findViewById(R.id.progress_bar_achievement);
        rvAchievements = findViewById(R.id.rv_achievements);
        rvAchievements.setLayoutManager(new LinearLayoutManager(this));

        api = ApiClient.getAuthClient(this).create(AchievementApi.class);

        fetchAchievements();
    }

    private void fetchAchievements() {
        progressBar.setVisibility(View.VISIBLE);
        api.getAchievements().enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    AchievementAdapter adapter = new AchievementAdapter(response.body());
                    rvAchievements.setAdapter(adapter);
                } else {
                    Toast.makeText(AchievementActivity.this, "Gagal memuat achievements", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AchievementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
