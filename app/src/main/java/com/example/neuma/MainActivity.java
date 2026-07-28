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
        }
    }
}