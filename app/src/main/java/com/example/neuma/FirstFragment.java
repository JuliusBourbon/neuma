package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.neuma.utils.TokenManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // Pastikan import ini benar
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

        // UBAH: Menggunakan LinearLayoutManager agar tersusun vertikal dari atas ke bawah
        rvLevels.setLayoutManager(new LinearLayoutManager(getContext()));

        loadLevels();

        return view;
    }

    private void loadLevels() {
        TokenManager tokenManager = new TokenManager(requireContext());
        String currentToken = tokenManager.getToken();

        // Print Token ke Logcat
        android.util.Log.d("DEBUG_NEUMA", "TOKEN SAAT INI: " + currentToken);

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        LevelApi levelApi = ApiClient.getAuthClient(requireContext()).create(LevelApi.class);
        Call<List<Level>> call = levelApi.getLevels();

        call.enqueue(new Callback<List<Level>>() {
            @Override
            public void onResponse(Call<List<Level>> call, Response<List<Level>> response) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                // Print Status Code dari Server
                android.util.Log.d("DEBUG_NEUMA", "RESPONSE CODE: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<Level> levels = response.body();
                    adapter = new LevelAdapter(levels, level -> {
                        Intent intent = new Intent(requireActivity(), LevelActivity.class);
                        intent.putExtra("LEVEL_ID", level.getId());
                        startActivity(intent);
                    });
                    rvLevels.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat level: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Level>> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}