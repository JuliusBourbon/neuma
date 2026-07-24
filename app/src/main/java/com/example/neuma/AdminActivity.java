package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.adapters.LevelAdapter;
import com.example.neuma.models.Level;
import com.example.neuma.network.LevelApi;
import com.example.neuma.utils.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView rvLevels;
    private ProgressBar progressBar;
    private LevelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        rvLevels = findViewById(R.id.rv_admin_levels);
        progressBar = findViewById(R.id.progress_bar_admin);

        rvLevels.setLayoutManager(new GridLayoutManager(this, 4));

        loadLevels();
    }

    private void loadLevels() {
        progressBar.setVisibility(View.VISIBLE);
        rvLevels.setVisibility(View.GONE);

        LevelApi levelApi = ApiClient.getAuthClient(this).create(LevelApi.class);
        Call<List<Level>> call = levelApi.getLevels();

        call.enqueue(new Callback<List<Level>>() {
            @Override
            public void onResponse(Call<List<Level>> call, Response<List<Level>> response) {
                progressBar.setVisibility(View.GONE);
                rvLevels.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Level> levels = response.body();

                    adapter = new LevelAdapter(levels, level -> {
                        Intent intent = new Intent(AdminActivity.this, AdminLevelDetailActivity.class);
                        intent.putExtra("LEVEL_ID", level.getId());
                        intent.putExtra("LEVEL_LETTER", level.getLetter());
                        startActivity(intent);
                    });
                    rvLevels.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminActivity.this, "Gagal memuat level", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
