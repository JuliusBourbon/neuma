package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.OnboardingRequest;
import com.example.neuma.network.OnboardingApi;
import com.example.neuma.utils.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingActivity extends AppCompatActivity {

    private TextInputEditText etSchool, etAge, etGrade, etHobby;
    private Button btnSimpan, btnLewati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        etSchool = findViewById(R.id.et_school);
        etAge = findViewById(R.id.et_age);
        etGrade = findViewById(R.id.et_grade);
        etHobby = findViewById(R.id.et_hobby);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnLewati = findViewById(R.id.btn_lewati);

        btnSimpan.setOnClickListener(v -> submitOnboarding());
        btnLewati.setOnClickListener(v -> showTutorialAndNavigate());
    }

    private void submitOnboarding() {
        String schoolStr = etSchool.getText() != null ? etSchool.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String gradeStr = etGrade.getText() != null ? etGrade.getText().toString().trim() : "";
        String hobbyStr = etHobby.getText() != null ? etHobby.getText().toString().trim() : "";

        Integer age = null;
        if (!ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Umur harus berupa angka", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Karena API menerima opsional, string kosong kita jadikan null agar tidak membuang space di DB
        String finalSchool = schoolStr.isEmpty() ? null : schoolStr;
        String finalGrade = gradeStr.isEmpty() ? null : gradeStr;
        String finalHobby = hobbyStr.isEmpty() ? null : hobbyStr;

        btnSimpan.setEnabled(false);
        btnSimpan.setText("Menyimpan...");

        // Gunakan getAuthClient karena endpoint ini butuh token
        OnboardingApi api = ApiClient.getAuthClient(this).create(OnboardingApi.class);
        Call<Void> call = api.submitOnboarding(new OnboardingRequest(finalSchool, age, finalGrade, finalHobby));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnSimpan.setEnabled(true);
                btnSimpan.setText("SIMPAN & LANJUT");

                if (response.isSuccessful()) {
                    Toast.makeText(OnboardingActivity.this, "Data tersimpan!", Toast.LENGTH_SHORT).show();
                    showTutorialAndNavigate();
                } else {
                    Toast.makeText(OnboardingActivity.this, "Gagal menyimpan: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSimpan.setEnabled(true);
                btnSimpan.setText("SIMPAN & LANJUT");
                Toast.makeText(OnboardingActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTutorialAndNavigate() {
        new AlertDialog.Builder(this)
                .setTitle("Selamat Datang di Neumá!")
                .setMessage("Aplikasi ini akan membantumu belajar Bahasa Isyarat secara interaktif.\n\n" +
                        "1. Pilih level huruf yang terbuka\n" +
                        "2. Pelajari materinya\n" +
                        "3. Jawab kuis dan kumpulkan poinnya!\n\n" +
                        "Mari kita mulai!")
                .setPositiveButton("Mulai Belajar", (dialog, which) -> {
                    Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                    // Hapus backstack agar tidak bisa back ke onboarding
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
