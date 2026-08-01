package com.example.neuma;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.adapters.MisiAdapter;
import com.example.neuma.models.Achievement;
import com.example.neuma.network.AchievementApi;
import com.example.neuma.utils.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisiFragment extends Fragment {

    private RecyclerView rvMisiList;
    private AchievementApi achievementApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_misi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMisiList = view.findViewById(R.id.rv_misi_list);
        rvMisiList.setLayoutManager(new LinearLayoutManager(requireContext()));

        achievementApi = ApiClient.getAuthClient(requireContext()).create(AchievementApi.class);

        // Tampilkan Data Misi (dari API Achievements)
        loadMisiData();

        // Setup Bottom Navigation
        setupBottomNavigation(view);
    }

    private void loadMisiData() {
        achievementApi.getAchievements().enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Achievement> achievements = response.body();
                    rvMisiList.setAdapter(new MisiAdapter(achievements));
                } else {
                    try {
                        String errBody = response.errorBody() != null ? response.errorBody().string() : "No Error Body";
                        Toast.makeText(requireContext(), "Gagal: " + response.code() + " " + errBody, Toast.LENGTH_LONG).show();
                        Log.e("MisiFragment", "HTTP " + response.code() + " - " + errBody);
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Gagal memuat: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MisiFragment", "Error loading achievements", t);
            }
        });
    }

    private void setupBottomNavigation(View view) {
        View bottomNav = view.findViewById(R.id.include_bottom_nav);
        if (bottomNav != null) {
            NavController navController = NavHostFragment.findNavController(this);

            View btnHome = bottomNav.findViewById(R.id.menu_home);
            View btnPencapaian = bottomNav.findViewById(R.id.menu_pencapaian);
            View btnProfile = bottomNav.findViewById(R.id.menu_profile);

            if (btnHome != null) btnHome.setOnClickListener(v -> navController.navigate(R.id.FirstFragment));
            if (btnPencapaian != null) btnPencapaian.setOnClickListener(v -> navController.navigate(R.id.PencapaianFragment));
            if (btnProfile != null) btnProfile.setOnClickListener(v -> navController.navigate(R.id.ProfileFragment));
        }
    }
}
