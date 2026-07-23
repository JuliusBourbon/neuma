package com.example.neuma;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Placeholder sementara untuk Tahap F
        TextView tv = new TextView(this);
        String levelId = getIntent().getStringExtra("LEVEL_ID");
        tv.setText("Ini Learn Screen untuk Level ID: " + levelId + "\n\nNanti di Tahap F akan ada materi dan tes ONNX.");
        tv.setTextSize(20);
        tv.setPadding(32, 32, 32, 32);
        
        setContentView(tv);
    }
}
