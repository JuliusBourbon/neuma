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
    private View progressBar;
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
        if (progressBar != null) progressBar.setVisibility(View.GONE);

        List<Level> levels = com.example.neuma.utils.DataManager.getInstance().getLevels();
        if (levels != null && !levels.isEmpty()) {
            adapter = new LevelAdapter(levels, level -> {
                Intent intent = new Intent(requireActivity(), LevelActivity.class);
                intent.putExtra("LEVEL_ID", level.getId());
                startActivity(intent);
            });
            rvLevels.setAdapter(adapter);
        } else {
            Toast.makeText(requireContext(), "Data level tidak tersedia. Silakan muat ulang aplikasi.", Toast.LENGTH_SHORT).show();
        }
    }
}