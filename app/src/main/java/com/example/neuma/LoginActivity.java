package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.AuthResponse;
import com.example.neuma.models.LoginRequest;
import com.example.neuma.network.AuthApi;
import com.example.neuma.utils.ApiClient;
import com.example.neuma.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etName, etPassword;
    private Button btnMasuk;
    private TextView tvRegisterLink;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManager = new TokenManager(this);

        etName = findViewById(R.id.et_email); // ID dari layout adalah et_email, tapi digunakan untuk nama
        etPassword = findViewById(R.id.et_password);
        btnMasuk = findViewById(R.id.btn_masuk);
        tvRegisterLink = findViewById(R.id.tv_register_link);

        btnMasuk.setOnClickListener(v -> performLogin());

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
                btnMasuk.setText("MASUK");
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    tokenManager.saveToken(authResponse.getToken());
                    
                    Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                    
                    // User sudah pernah onboarding jika bisa login (sesuai asumsi alur)
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                btnMasuk.setText("MASUK");
                Toast.makeText(LoginActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
