package com.example.neuma;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);

        if (navHostFragment != null) {
            final NavController navController = navHostFragment.getNavController();

            // Memperbaiki pemanggilan klik menu kustom
            findViewById(R.id.menu_home).setOnClickListener(v -> navController.navigate(R.id.FirstFragment));
            findViewById(R.id.menu_misi).setOnClickListener(v -> navController.navigate(R.id.MisiFragment));
            findViewById(R.id.menu_pencapaian).setOnClickListener(v -> navController.navigate(R.id.PencapaianFragment));
            findViewById(R.id.menu_profile).setOnClickListener(v -> navController.navigate(R.id.ProfileFragment));

            android.widget.ImageView ivHome = findViewById(R.id.iv_menu_home);
            android.widget.ImageView ivMisi = findViewById(R.id.iv_menu_misi);
            android.widget.ImageView ivPencapaian = findViewById(R.id.iv_menu_pencapaian);
            android.widget.ImageView ivProfile = findViewById(R.id.iv_menu_profile);

            int colorActive = androidx.core.content.ContextCompat.getColor(this, R.color.green_button);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (ivHome != null) ivHome.clearColorFilter();
                if (ivMisi != null) ivMisi.clearColorFilter();
                if (ivPencapaian != null) ivPencapaian.clearColorFilter();
                if (ivProfile != null) ivProfile.clearColorFilter();

                int id = destination.getId();
                if (id == R.id.FirstFragment && ivHome != null) {
                    ivHome.setColorFilter(colorActive);
                } else if (id == R.id.MisiFragment && ivMisi != null) {
                    ivMisi.setColorFilter(colorActive);
                } else if (id == R.id.PencapaianFragment && ivPencapaian != null) {
                    ivPencapaian.setColorFilter(colorActive);
                } else if (id == R.id.ProfileFragment && ivProfile != null) {
                    ivProfile.setColorFilter(colorActive);
                }
            });
        }
    }
}