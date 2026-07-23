package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.FinishAttemptResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    private TextView tvTotalScore, tvAchievementList;
    private LinearLayout layoutAchievements;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        tvTotalScore = findViewById(R.id.tv_total_score);
        tvAchievementList = findViewById(R.id.tv_achievement_list);
        layoutAchievements = findViewById(R.id.layout_achievements);
        btnHome = findViewById(R.id.btn_home);

        int totalScore = getIntent().getIntExtra("TOTAL_SCORE", 0);
        tvTotalScore.setText(String.valueOf(totalScore));

        String achievementsJson = getIntent().getStringExtra("NEW_ACHIEVEMENTS");
        if (achievementsJson != null && !achievementsJson.isEmpty()) {
            try {
                Type listType = new TypeToken<List<FinishAttemptResponse.Achievement>>() {}.getType();
                List<FinishAttemptResponse.Achievement> achievements = new Gson().fromJson(achievementsJson, listType);
                
                if (achievements != null && !achievements.isEmpty()) {
                    layoutAchievements.setVisibility(View.VISIBLE);
                    StringBuilder sb = new StringBuilder();
                    for (FinishAttemptResponse.Achievement a : achievements) {
                        sb.append("🏆 ").append(a.getTitle()).append("\n");
                    }
                    tvAchievementList.setText(sb.toString().trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
