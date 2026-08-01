package com.example.neuma;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etNama, etEmail, etPesan;
    private Button btnKirim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        btnBack = findViewById(R.id.btn_back_support);
        etNama = findViewById(R.id.et_support_nama);
        etEmail = findViewById(R.id.et_support_email);
        etPesan = findViewById(R.id.et_support_pesan);
        btnKirim = findViewById(R.id.btn_kirim_support);

        // Aksi tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // Aksi kirim support
        btnKirim.setOnClickListener(v -> performSubmitSupport());
    }

    private void performSubmitSupport() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pesan = etPesan.getText().toString().trim();

        if (nama.isEmpty() || email.isEmpty() || pesan.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom bertanda *", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proses pengiriman pesan
        Toast.makeText(this, "Pesan support berhasil dikirim!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
