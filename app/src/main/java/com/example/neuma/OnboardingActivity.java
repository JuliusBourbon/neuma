package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    private Button buttonMulaiPetualangan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Menghubungkan variabel dengan ID tombol di XML
        buttonMulaiPetualangan = findViewById(R.id.buttonMulaiPetualangan);

        // Menambahkan aksi ketika tombol diklik
        buttonMulaiPetualangan.setOnClickListener(v -> {
            // Arahkan pengguna ke MainActivity (halaman utama aplikasi)
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            // Menghapus riwayat halaman sebelumnya agar pengguna tidak bisa kembali ke onboarding dengan tombol 'Back'
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
