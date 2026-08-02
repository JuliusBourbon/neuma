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
                
                if (ivProfile != null) {
                    ivProfile.setPadding(0, 0, 0, 0);
                    ivProfile.setBackground(null);
                }

                int id = destination.getId();
                if (id == R.id.FirstFragment && ivHome != null) {
                    ivHome.setColorFilter(colorActive);
                } else if (id == R.id.MisiFragment && ivMisi != null) {
                    ivMisi.setColorFilter(colorActive);
                } else if (id == R.id.PencapaianFragment && ivPencapaian != null) {
                    ivPencapaian.setColorFilter(colorActive);
                } else if (id == R.id.ProfileFragment && ivProfile != null) {
                    // Beri border hijau tipis pada avatar saat aktif, tanpa tint color (agar wajah avatar tetap terlihat)
                    ivProfile.setPadding(3, 3, 3, 3);
                    ivProfile.setBackgroundResource(R.drawable.bg_avatar_active_nav);
                }
            });

            // Load Avatar dari DataManager (Sudah di-prefetch di SplashActivity)
            com.example.neuma.models.User user = com.example.neuma.utils.DataManager.getInstance().getCurrentUser();
            if (user != null && ivProfile != null) {
                String style = user.getAvatarStyle() != null ? user.getAvatarStyle() : "adventurer";
                String seed = user.getAvatarSeed() != null ? user.getAvatarSeed() : "Felix";
                String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed;
                
                com.bumptech.glide.Glide.with(MainActivity.this)
                    .load(avatarUrl)
                    .circleCrop()
                    .into(ivProfile);
            }
        }

        com.example.neuma.utils.TokenManager tokenManager = new com.example.neuma.utils.TokenManager(this);
        if (tokenManager.isFirstTimeTutorial()) {
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                showOnboardingTutorial(tokenManager);
            }, 1000);
        }
    }

    private void showOnboardingTutorial(com.example.neuma.utils.TokenManager tokenManager) {
        android.graphics.Typeface poppins = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.poppins);
        int primaryColor = R.color.primary;

        com.getkeepsafe.taptargetview.TapTarget targetHome = com.getkeepsafe.taptargetview.TapTarget.forView(findViewById(R.id.menu_home), "Menu Utama", "Mulai belajar dengan mengakses berbagai materi dan tantangan di sini.")
            .outerCircleColor(primaryColor).targetCircleColor(R.color.white).titleTypeface(poppins).descriptionTypeface(poppins).cancelable(false);
        com.getkeepsafe.taptargetview.TapTarget targetMisi = com.getkeepsafe.taptargetview.TapTarget.forView(findViewById(R.id.menu_misi), "Menu Misi", "Selesaikan misi harian untuk mendapatkan poin tambahan.")
            .outerCircleColor(primaryColor).targetCircleColor(R.color.white).titleTypeface(poppins).descriptionTypeface(poppins).cancelable(false);
        com.getkeepsafe.taptargetview.TapTarget targetPencapaian = com.getkeepsafe.taptargetview.TapTarget.forView(findViewById(R.id.menu_pencapaian), "Pencapaian", "Kumpulkan berbagai pencapaian keren selama kamu belajar.")
            .outerCircleColor(primaryColor).targetCircleColor(R.color.white).titleTypeface(poppins).descriptionTypeface(poppins).cancelable(false);
        com.getkeepsafe.taptargetview.TapTarget targetProfile = com.getkeepsafe.taptargetview.TapTarget.forView(findViewById(R.id.menu_profile), "Profil", "Atur avatar dan lihat statistik belajarmu di sini.")
            .outerCircleColor(primaryColor).targetCircleColor(R.color.white).titleTypeface(poppins).descriptionTypeface(poppins).cancelable(false);

        java.util.List<com.getkeepsafe.taptargetview.TapTarget> targets = new java.util.ArrayList<>();
        targets.add(targetHome);
        targets.add(targetMisi);
        targets.add(targetPencapaian);
        targets.add(targetProfile);

        androidx.recyclerview.widget.RecyclerView rvLevels = findViewById(R.id.rv_levels);
        if (rvLevels != null && rvLevels.getChildCount() > 0) {
            android.view.View firstLevel = rvLevels.getChildAt(0);
            if (firstLevel != null) {
                com.getkeepsafe.taptargetview.TapTarget targetLevel = com.getkeepsafe.taptargetview.TapTarget.forView(firstLevel, "Ayo Mulai!", "Pilih level pertamamu dan mulailah perjalananmu.")
                    .outerCircleColor(primaryColor).targetCircleColor(R.color.white).titleTypeface(poppins).descriptionTypeface(poppins).cancelable(false).transparentTarget(true);
                targets.add(targetLevel);
            }
        }

        new com.getkeepsafe.taptargetview.TapTargetSequence(this)
            .targets(targets)
            .listener(new com.getkeepsafe.taptargetview.TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {
                    tokenManager.setFirstTimeTutorial(false);
                }

                @Override
                public void onSequenceStep(com.getkeepsafe.taptargetview.TapTarget lastTarget, boolean targetClicked) {
                }

                @Override
                public void onSequenceCanceled(com.getkeepsafe.taptargetview.TapTarget lastTarget) {
                    tokenManager.setFirstTimeTutorial(false);
                }
            }).start();
    }
}