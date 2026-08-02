package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Diubah dari TextInputEditText
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.AuthResponse;
import com.example.neuma.models.RegisterRequest;
import com.example.neuma.network.AuthApi;
import com.example.neuma.utils.ApiClient;
import com.example.neuma.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etPassword, etTanggalLahir, etGender;
    private Button btnDaftar;
    private TextView tvLoginLink;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tokenManager = new TokenManager(this);

        // Menyesuaikan ID dengan activity_signup.xml
        etName = findViewById(R.id.editTextNamaPenggunaDaftar);
        etPassword = findViewById(R.id.editTextPasswordDaftar);


        btnDaftar = findViewById(R.id.buttonDaftar);
        tvLoginLink = findViewById(R.id.textViewSudahPunyaAkun);

        // Aksi ketika teks "Masuk" diklik
        tvLoginLink.setOnClickListener(v -> finish()); // Kembali ke halaman Login

        // Aksi ketika tombol "Daftar" diklik
        btnDaftar.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Jika backend teman Anda nanti membutuhkan Tanggal Lahir dan Gender,
        // Anda bisa mengambil datanya di sini:
        // String tanggalLahir = etTanggalLahir.getText().toString().trim();
        // String gender = etGender.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nama dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        btnDaftar.setEnabled(false);
        btnDaftar.setText("Memproses...");

        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);

        // Tetap menggunakan name dan password saja sesuai logika asli teman Anda
        Call<AuthResponse> call = authApi.register(new RegisterRequest(name, password));

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnDaftar.setEnabled(true);
                btnDaftar.setText("Daftar"); // Disesuaikan dengan kapitalisasi di desain (Daftar, bukan DAFTAR)

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    tokenManager.saveToken(authResponse.getToken());
                    if(authResponse.getUser() != null) {
                        tokenManager.saveUsername(authResponse.getUser().getName());
                    }

                    Toast.makeText(SignupActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();

                    // User baru register -> arahkan ke Onboarding Screen
                    Intent intent = new Intent(SignupActivity.this, OnboardingActivity.class);
                    // Hapus backstack agar tidak bisa back ke login/register
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnDaftar.setEnabled(true);
                btnDaftar.setText("Daftar");
                Toast.makeText(SignupActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
