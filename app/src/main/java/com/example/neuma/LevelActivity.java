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
    private View progressBar;
    private androidx.constraintlayout.widget.Group groupContent;
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
        groupContent = findViewById(R.id.group_content);
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
                groupContent.setVisibility(View.VISIBLE);

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
                groupContent.setVisibility(View.VISIBLE);
                setupDummyLeaderboard();
            }
        });
    }

    private void setupLeaderboardData(List<LeaderboardEntry> list) {
        // Pad the list to ensure it has at least 10 elements
        List<LeaderboardEntry> paddedList = new ArrayList<>(list);
        while (paddedList.size() < 10) {
            LeaderboardEntry dummy = new LeaderboardEntry();
            dummy.setName("-");
            dummy.setScore(0);
            dummy.setAvatarStyle("adventurer");
            dummy.setAvatarSeed("Felix");
            paddedList.add(dummy);
        }

        // Top 1
        tvRank1Name.setText(paddedList.get(0).getName());
        tvRank1Score.setText(paddedList.get(0).getScore() + " Points");
        loadAvatar(findViewById(R.id.iv_rank1_avatar), paddedList.get(0));

        // Top 2
        tvRank2Name.setText(paddedList.get(1).getName());
        tvRank2Score.setText(paddedList.get(1).getScore() + " Points");
        loadAvatar(findViewById(R.id.iv_rank2_avatar), paddedList.get(1));

        // Top 3
        tvRank3Name.setText(paddedList.get(2).getName());
        tvRank3Score.setText(paddedList.get(2).getScore() + " Points");
        loadAvatar(findViewById(R.id.iv_rank3_avatar), paddedList.get(2));

        // Sisanya (peringkat 4 - 10) dimasukkan ke RecyclerView
        List<LeaderboardEntry> restList = paddedList.subList(3, 10);
        rvLeaderboard.setAdapter(new LeaderboardAdapter(restList));
    }

    private void loadAvatar(android.widget.ImageView imageView, LeaderboardEntry entry) {
        if (entry.getName().equals("-")) {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
            return;
        }
        String style = entry.getAvatarStyle() != null ? entry.getAvatarStyle() : "adventurer";
        String seed = entry.getAvatarSeed() != null ? entry.getAvatarSeed() : "Felix";
        String url = "https://api.dicebear.com/10.x/" + style + "/png?seed=" + seed;
        com.bumptech.glide.Glide.with(this).load(url).into(imageView);

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(LevelActivity.this, OtherProfileActivity.class);
            intent.putExtra("USER_ID", entry.getUserId());
            startActivity(intent);
        });
    }

    private void setupDummyLeaderboard() {
        setupLeaderboardData(new ArrayList<>());
    }
}
