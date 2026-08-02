package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.AuthResponse;
import com.example.neuma.models.LoginRequest;
import com.example.neuma.network.AuthApi;
import com.example.neuma.utils.ApiClient;
import com.example.neuma.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etPassword;
    private Button btnMasuk;
    private TextView tvRegisterLink;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = new TokenManager(this);

        // Menghubungkan ID dengan komponen di XML terbaru
        etName = findViewById(R.id.editTextNamaPengguna);
        etPassword = findViewById(R.id.editTextPassword);
        btnMasuk = findViewById(R.id.buttonMasuk);

        // Hanya menggunakan ID teks yang bisa di-klik ("Daftar Sekarang!")
        tvRegisterLink = findViewById(R.id.textViewBlmPunyaAkun);

        // Aksi tombol login
        btnMasuk.setOnClickListener(v -> performLogin());

        // Aksi klik teks daftar
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nama dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        btnMasuk.setEnabled(false);
        btnMasuk.setText("Memproses...");

        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        Call<AuthResponse> call = authApi.login(new LoginRequest(name, password));

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    tokenManager.saveToken(authResponse.getToken());
                    if(authResponse.getUser() != null) {
                        tokenManager.saveUsername(authResponse.getUser().getName());
                    }

                    Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                    // Hapus backstack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("Masuk");
                Toast.makeText(LoginActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
