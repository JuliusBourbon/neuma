package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.adapters.LeaderboardAdapter;
import com.example.neuma.models.LeaderboardEntry;
import com.example.neuma.models.LevelDetailResponse;
import com.example.neuma.network.LevelApi;
import com.example.neuma.utils.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvRank1Name, tvRank1Score;
    private TextView tvRank2Name, tvRank2Score;
    private TextView tvRank3Name, tvRank3Score;

    private RecyclerView rvLeaderboard;
    private ProgressBar progressBar;
    private Button btnMulai;
    private String levelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        levelId = getIntent().getStringExtra("LEVEL_ID");

        tvTitle = findViewById(R.id.tv_level_title);
        tvRank1Name = findViewById(R.id.tv_rank1_name);
        tvRank1Score = findViewById(R.id.tv_rank1_score);
        tvRank2Name = findViewById(R.id.tv_rank2_name);
        tvRank2Score = findViewById(R.id.tv_rank2_score);
        tvRank3Name = findViewById(R.id.tv_rank3_name);
        tvRank3Score = findViewById(R.id.tv_rank3_score);

        rvLeaderboard = findViewById(R.id.rv_leaderboard);
        progressBar = findViewById(R.id.progress_bar);
        btnMulai = findViewById(R.id.btn_mulai);

        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));

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
        Call<LevelDetailResponse> call = api.getLevelDetail(levelId != null ? levelId : "1");

        call.enqueue(new Callback<LevelDetailResponse>() {
            @Override
            public void onResponse(Call<LevelDetailResponse> call, Response<LevelDetailResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    LevelDetailResponse data = response.body();
                    tvTitle.setText("Level " + (data.getLevel() != null ? data.getLevel().getLetter() : "A"));

                    if (data.getLeaderboard() != null && !data.getLeaderboard().isEmpty()) {
                        setupLeaderboardData(data.getLeaderboard());
                    } else {
                        setupDummyLeaderboard();
                    }
                } else {
                    setupDummyLeaderboard();
                }
            }

            @Override
            public void onFailure(Call<LevelDetailResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                setupDummyLeaderboard();
            }
        });
    }

    private void setupLeaderboardData(List<LeaderboardEntry> list) {
        //Top 3 jika data tersedia
        if (list.size() > 0) {
            tvRank1Name.setText(list.get(0).getName());
            tvRank1Score.setText(list.get(0).getScore() + " Points");
        }
        if (list.size() > 1) {
            tvRank2Name.setText(list.get(1).getName());
            tvRank2Score.setText(list.get(1).getScore() + " Points");
        }
        if (list.size() > 2) {
            tvRank3Name.setText(list.get(2).getName());
            tvRank3Score.setText(list.get(2).getScore() + " Points");
        }

        // Sisanya (peringkat 4 - 10) dimasukkan ke RecyclerView
        if (list.size() > 3) {
            List<LeaderboardEntry> restList = list.subList(3, list.size());
            rvLeaderboard.setAdapter(new LeaderboardAdapter(restList));
        }
    }

    private void setupDummyLeaderboard() {
        tvRank1Name.setText("Hasan Sajjad");
        tvRank1Score.setText("100 Points");

        tvRank2Name.setText("Masuma");
        tvRank2Score.setText("100 Points");

        tvRank3Name.setText("Tanim");
        tvRank3Score.setText("100 Points");

        List<LeaderboardEntry> dummyRest = new ArrayList<>();
        // Mengisi data dummy persis seperti gambar figma
        dummyRest.add(createDummyEntry("Rina Wati", 100));
        dummyRest.add(createDummyEntry("Arif Rahman", 100));
        dummyRest.add(createDummyEntry("Fitri Yani", 100));
        dummyRest.add(createDummyEntry("Hendra Setiawan", 100));
        dummyRest.add(createDummyEntry("Rian Ardianto", 100));
        dummyRest.add(createDummyEntry("Dian Sastro", 100));
        dummyRest.add(createDummyEntry("Tika", 100));

        rvLeaderboard.setAdapter(new LeaderboardAdapter(dummyRest));
    }

    private LeaderboardEntry createDummyEntry(String name, int score) {
        // Asumsi model LeaderboardEntry kamu memiliki setter/constructor yang sesuai
        return new LeaderboardEntry();
    }
}
