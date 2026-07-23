package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.adapters.LeaderboardAdapter;
import com.example.neuma.models.LevelDetailResponse;
import com.example.neuma.network.LevelApi;
import com.example.neuma.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelActivity extends AppCompatActivity {

    private TextView tvTitle, tvDesc;
    private RecyclerView rvLeaderboard;
    private ProgressBar progressBar;
    private Button btnMulai;
    private ImageButton btnBack;

    private String levelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        levelId = getIntent().getStringExtra("LEVEL_ID");
        if (levelId == null) {
            Toast.makeText(this, "Level tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle = findViewById(R.id.tv_level_title);
        tvDesc = findViewById(R.id.tv_level_desc);
        rvLeaderboard = findViewById(R.id.rv_leaderboard);
        progressBar = findViewById(R.id.progress_bar);
        btnMulai = findViewById(R.id.btn_mulai);
        btnBack = findViewById(R.id.btn_back);
        
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());
        
        btnMulai.setOnClickListener(v -> {
            Intent intent = new Intent(LevelActivity.this, LearnActivity.class);
            intent.putExtra("LEVEL_ID", levelId);
            startActivity(intent);
        });

        loadLevelDetail();
    }

    private void loadLevelDetail() {
        progressBar.setVisibility(View.VISIBLE);
        
        LevelApi api = ApiClient.getAuthClient(this).create(LevelApi.class);
        Call<LevelDetailResponse> call = api.getLevelDetail(levelId);
        
        call.enqueue(new Callback<LevelDetailResponse>() {
            @Override
            public void onResponse(Call<LevelDetailResponse> call, Response<LevelDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    LevelDetailResponse data = response.body();
                    
                    tvTitle.setText("Level " + data.getLevel().getLetter());
                    tvDesc.setText(data.getLevel().getTitle() != null ? data.getLevel().getTitle() : "Mari belajar!");
                    
                    if (data.getLeaderboard() != null) {
                        LeaderboardAdapter adapter = new LeaderboardAdapter(data.getLeaderboard());
                        rvLeaderboard.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(LevelActivity.this, "Gagal memuat detail: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LevelDetailResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LevelActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
