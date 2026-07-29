package com.example.neuma;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.adapters.MisiAdapter;
import com.example.neuma.models.Misi;

import java.util.ArrayList;
import java.util.List;

public class MisiFragment extends Fragment {

    private RecyclerView rvMisiHarian;
    private RecyclerView rvMisiUpcoming;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_misi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMisiHarian = view.findViewById(R.id.rv_misi_harian);
        rvMisiUpcoming = view.findViewById(R.id.rv_misi_upcoming);

        rvMisiHarian.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMisiUpcoming.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Tampilkan Data Misi
        loadMisiData();

        // Setup Bottom Navigation
        setupBottomNavigation(view);
    }

    private void loadMisiData() {
        // Data Dummy Misi Harian
        List<Misi> listHarian = new ArrayList<>();
        listHarian.add(new Misi("Kumpulkan 3 huruf BISINDO", 1, 3, false));
        listHarian.add(new Misi("Selesaikan 1 Level Kuis", 1, 1, true));

        // Data Dummy Misi Akan Datang
        List<Misi> listUpcoming = new ArrayList<>();
        listUpcoming.add(new Misi("Pelajari 10 Kata Baru", 0, 10, false));
        listUpcoming.add(new Misi("Selesaikan 5 Level berturut-turut", 2, 5, false));

        // Set Adapter
        rvMisiHarian.setAdapter(new MisiAdapter(listHarian));
        rvMisiUpcoming.setAdapter(new MisiAdapter(listUpcoming));
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
