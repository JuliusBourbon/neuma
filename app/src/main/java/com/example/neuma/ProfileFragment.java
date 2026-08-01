package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.neuma.models.User;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName;
    private ImageButton btnSetting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tv_profile_name);
        btnSetting = view.findViewById(R.id.btn_setting);

        // Aksi Tombol Setting Ke SettingActivity
        if (btnSetting != null) {
            btnSetting.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), SettingActivity.class);
                startActivity(intent);
            });
        }

        setupBottomNavigation(view);

        loadProfileData();
    }

    private void loadProfileData() {
        UserApi userApi = ApiClient.getAuthClient(requireContext()).create(UserApi.class);
        userApi.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    tvProfileName.setText(response.body().getName());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (isAdded()) {
                    tvProfileName.setText("Prabowo Widodo");
                }
            }
        });
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
                btnPencapaian.setOnClickListener(v -> navController.navigate(R.id.PencapaianFragment));
            }
            if (btnProfile != null) {
                // Halaman saat ini
            }
        }
    }
}
