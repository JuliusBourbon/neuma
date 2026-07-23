package com.example.neuma;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Placeholder sementara, nanti akan pakai layout tahap E
        TextView tv = new TextView(this);
        String letter = getIntent().getStringExtra("LEVEL_LETTER");
        tv.setText("Ini Level View untuk Huruf: " + letter);
        tv.setTextSize(24);
        tv.setPadding(32, 32, 32, 32);
        
        setContentView(tv);
    }
}
