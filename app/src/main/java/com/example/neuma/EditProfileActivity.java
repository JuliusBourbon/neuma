package com.example.neuma;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.UpdatePasswordRequest;
import com.example.neuma.models.UpdateProfileRequest;
import com.example.neuma.models.User;
import com.example.neuma.network.UserApi;
import com.example.neuma.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout btnMenuNama, btnMenuPassword;
    private TextView tvValueNama;
    private ProgressBar progressBar;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnBack = findViewById(R.id.btn_back_edit_profile);
        btnMenuNama = findViewById(R.id.btn_menu_nama);
        btnMenuPassword = findViewById(R.id.btn_menu_password);
        tvValueNama = findViewById(R.id.tv_value_nama);
        progressBar = findViewById(R.id.progress_bar_edit_profile);

        userApi = ApiClient.getAuthClient(this).create(UserApi.class);

        btnBack.setOnClickListener(v -> finish());
        btnMenuNama.setOnClickListener(v -> showUpdateNameDialog());
        btnMenuPassword.setOnClickListener(v -> showUpdatePasswordDialog());

        loadProfile();
    }

    private void loadProfile() {
        setLoading(true);
        userApi.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    tvValueNama.setText(response.body().getName());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
            }
        });
    }

    private void showUpdateNameDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_name, null);
        EditText etNama = view.findViewById(R.id.et_dialog_nama);
        Button btnBatal = view.findViewById(R.id.btn_dialog_batal_nama);
        Button btnSimpan = view.findViewById(R.id.btn_dialog_simpan_nama);

        etNama.setText(tvValueNama.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnBatal.setOnClickListener(v -> dialog.dismiss());
        btnSimpan.setOnClickListener(v -> {
            String newName = etNama.getText().toString().trim();
            if (!newName.isEmpty()) {
                performUpdateName(newName);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void performUpdateName(String newName) {
        setLoading(true);
        userApi.updateProfile(new UpdateProfileRequest(newName, null, null)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    tvValueNama.setText(response.body().getName());
                    Toast.makeText(EditProfileActivity.this, "Nama berhasil diubah!", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Gagal mengubah nama";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(EditProfileActivity.this, "Gagal: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                setLoading(false);
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdatePasswordDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_password, null);
        EditText etOldPass = view.findViewById(R.id.et_dialog_old_password);
        EditText etNewPass = view.findViewById(R.id.et_dialog_new_password);
        Button btnBatal = view.findViewById(R.id.btn_dialog_batal_password);
        Button btnSimpan = view.findViewById(R.id.btn_dialog_simpan_password);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnBatal.setOnClickListener(v -> dialog.dismiss());
        btnSimpan.setOnClickListener(v -> {
            String oldPass = etOldPass.getText().toString();
            String newPass = etNewPass.getText().toString();
            if (!oldPass.isEmpty() && !newPass.isEmpty()) {
                performUpdatePassword(oldPass, newPass);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void performUpdatePassword(String oldPass, String newPass) {
        setLoading(true);
        userApi.updatePassword(new UpdatePasswordRequest(oldPass, newPass)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Gagal! Password lama salah?";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(EditProfileActivity.this, "Gagal: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                setLoading(false);
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
