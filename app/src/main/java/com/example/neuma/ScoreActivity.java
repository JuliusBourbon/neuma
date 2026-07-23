package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Placeholder untuk Tahap G
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        int totalScore = getIntent().getIntExtra("TOTAL_SCORE", 0);

        TextView tv = new TextView(this);
        tv.setText("Ini Nilai Screen (Tahap G)\n\nSkor yang didapat: " + totalScore + "\n\nNanti di Tahap G kita akan percantik UI-nya dan handle Notifikasi Achievement.");
        tv.setTextSize(20);
        layout.addView(tv);
        
        Button btnBack = new Button(this);
        btnBack.setText("KEMBALI KE HOMEPAGE");
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        layout.addView(btnBack);

        setContentView(layout);
    }
}
