package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class FirstFragment extends Fragment {

    private RecyclerView rvLevels;
    private ProgressBar progressBar;
    private LevelAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        
        rvLevels = view.findViewById(R.id.rv_levels);
        progressBar = view.findViewById(R.id.progress_bar);
        
        rvLevels.setLayoutManager(new GridLayoutManager(getContext(), 4)); // 4 kolom (bulatan)
        
        loadLevels();
        
        return view;
    }

    private void loadLevels() {
        progressBar.setVisibility(View.VISIBLE);
        rvLevels.setVisibility(View.GONE);

        LevelApi levelApi = ApiClient.getAuthClient(requireContext()).create(LevelApi.class);
        Call<List<Level>> call = levelApi.getLevels();
        
        call.enqueue(new Callback<List<Level>>() {
            @Override
            public void onResponse(Call<List<Level>> call, Response<List<Level>> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                rvLevels.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Level> levels = response.body();
                    
                    adapter = new LevelAdapter(levels, level -> {
                        // Ke Level View (Tahap E)
                        Intent intent = new Intent(requireActivity(), LevelActivity.class);
                        intent.putExtra("LEVEL_ID", level.getId());
                        intent.putExtra("LEVEL_LETTER", level.getLetter());
                        startActivity(intent);
                    });
                    rvLevels.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat level: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}