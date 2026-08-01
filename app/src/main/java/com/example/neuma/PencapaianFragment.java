package com.example.neuma;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.models.Achievement;
import com.example.neuma.network.AchievementApi;
import com.example.neuma.utils.ApiClient;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PencapaianFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView rvPencapaian;
    private AchievementApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pencapaian, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar_pencapaian);
        rvPencapaian = view.findViewById(R.id.rv_pencapaian);
        rvPencapaian.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inisialisasi API Retrofit
        api = ApiClient.getAuthClient(requireContext()).create(AchievementApi.class);

        // Load data pencapaian
        fetchAchievements();

        // Setup Bottom Navigation agar tombol dapat diklik
        setupBottomNavigation(view);
    }

    private void fetchAchievements() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        api.getAchievements().enqueue(new Callback<List<Achievement>>() {
            @Override
            public void onResponse(Call<List<Achievement>> call, Response<List<Achievement>> response) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Achievement> allAchievements = response.body();
                    List<Achievement> unlockedAchievements = new java.util.ArrayList<>();
                    for (Achievement a : allAchievements) {
                        if (a.isUnlocked()) unlockedAchievements.add(a);
                    }
                    AchievementAdapter adapter = new AchievementAdapter(unlockedAchievements);
                    rvPencapaian.setAdapter(adapter);
                } else {
                    // Fallback data dummy jika API kosong
                    loadDummyAchievements();
                }
            }

            @Override
            public void onFailure(Call<List<Achievement>> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                loadDummyAchievements();
            }
        });
    }

    private void loadDummyAchievements() {
        List<Achievement> list = new ArrayList<>();

        list.add(new Achievement("1", "PROFIL", "Profil Keren", "Kamu sudah memasang nama dan memilih Avatar yang lucu!", true));
        list.add(new Achievement("2", "RAJIN", "Si Paling Rajin", "Hebat! Kamu belajar BISINDO 3 hari berturut-turut tanpa bolos.", true));
        list.add(new Achievement("3", "BINTANG", "Kolektor Bintang", "Wow! Kamu berhasil mengumpulkan total 500 XP dari hasil belajarmu.", true));
        list.add(new Achievement("4", "TANGAN", "Tangan Ajaib", "Kamu memperagakan 10 huruf di kamera AR tanpa salah sama sekali.", true));
        list.add(new Achievement("5", "HURUF", "Detektif Huruf", "Kamu berhasil menamatkan belajar 5 huruf pertamamu di peta level.", true));

        list.add(new Achievement("6", "LIBURAN", "Pejuang Liburan", "Rajinnya! Kamu tetap semangat belajar isyarat di hari Sabtu atau Minggu.", false));
        list.add(new Achievement("7", "KAMERA", "Sahabat Kamera", "Kamu sudah berani tampil dan berlatih di depan kamera AR sebanyak 20 kali.", false));

        AchievementAdapter adapter = new AchievementAdapter(list);
        rvPencapaian.setAdapter(adapter);
    }

    private void setupBottomNavigation(View view) {
        View bottomNav = view.findViewById(R.id.include_bottom_nav);
        if (bottomNav != null) {
            NavController navController = NavHostFragment.findNavController(this);

            View btnHome = bottomNav.findViewById(R.id.menu_home);
            View btnMisi = bottomNav.findViewById(R.id.menu_misi);
            View btnPencapaian = bottomNav.findViewById(R.id.menu_pencapaian);
            View btnProfile = bottomNav.findViewById(R.id.menu_profile);

            if (btnHome != null) {
                btnHome.setOnClickListener(v -> navController.navigate(R.id.FirstFragment));
            }
            if (btnMisi != null) {
                btnMisi.setOnClickListener(v -> navController.navigate(R.id.MisiFragment));
            }
            if (btnPencapaian != null) {
                // Halaman saat ini
            }
            if (btnProfile != null) {
                btnProfile.setOnClickListener(v -> navController.navigate(R.id.ProfileFragment));
            }
        }
    }
}
