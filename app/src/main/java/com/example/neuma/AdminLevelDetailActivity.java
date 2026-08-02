package com.example.neuma;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neuma.models.Material;
import com.example.neuma.models.Option;
import com.example.neuma.models.Question;
import com.example.neuma.network.AdminApi;
import com.example.neuma.network.LevelApi;
import com.example.neuma.utils.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLevelDetailActivity extends AppCompatActivity {

    private String levelId;
    private String levelLetter;

    private TextView tvTitle;
    private View progressBar;
    private LinearLayout layoutItems;

    private LevelApi levelApi;
    private AdminApi adminApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_level_detail);

        levelId = getIntent().getStringExtra("LEVEL_ID");
        levelLetter = getIntent().getStringExtra("LEVEL_LETTER");

        tvTitle = findViewById(R.id.tv_admin_level_title);
        progressBar = findViewById(R.id.progress_bar_admin_detail);
        layoutItems = findViewById(R.id.layout_admin_items);

        tvTitle.setText("Kostumisasi Level " + levelLetter);

        levelApi = ApiClient.getAuthClient(this).create(LevelApi.class);
        adminApi = ApiClient.getAuthClient(this).create(AdminApi.class);

        fetchData();
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        layoutItems.removeAllViews();

        levelApi.getMaterials(levelId).enqueue(new Callback<List<Material>>() {
            @Override
            public void onResponse(Call<List<Material>> call, Response<List<Material>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Material> materials = response.body();
                    for (Material m : materials) {
                        addMaterialItem(m);
                    }
                    
                    // Fetch Questions
                    levelApi.getQuestions(levelId).enqueue(new Callback<List<Question>>() {
                        @Override
                        public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                List<Question> questions = response.body();
                                for (Question q : questions) {
                                    addQuestionItem(q);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Question>> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Material>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void addMaterialItem(Material m) {
        TextView tv = new TextView(this);
        tv.setText("[MATERIAL] " + m.getType() + "\n" + m.getTextContent());
        tv.setPadding(16, 24, 16, 24);
        tv.setBackgroundResource(android.R.drawable.list_selector_background);
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setOnClickListener(v -> showEditMaterialDialog(m));
        layoutItems.addView(tv);
    }

    private void addQuestionItem(Question q) {
        TextView tv = new TextView(this);
        tv.setText("[QUESTION] " + q.getType() + "\n" + q.getText());
        tv.setPadding(16, 24, 16, 24);
        tv.setBackgroundResource(android.R.drawable.list_selector_background);
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setOnClickListener(v -> showEditQuestionDialog(q));
        layoutItems.addView(tv);
    }

    private void showEditMaterialDialog(Material m) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_material, null);
        TextInputEditText etContent = view.findViewById(R.id.et_content);
        TextInputEditText etMediaUrl = view.findViewById(R.id.et_media_url);

        etContent.setText(m.getTextContent());
        etMediaUrl.setText(m.getMediaUrl());

        new AlertDialog.Builder(this)
                .setTitle("Edit Material")
                .setView(view)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("content", etContent.getText().toString());
                    body.put("mediaUrl", etMediaUrl.getText().toString());
                    
                    progressBar.setVisibility(View.VISIBLE);
                    adminApi.updateMaterial(m.getId(), body).enqueue(new Callback<Material>() {
                        @Override
                        public void onResponse(Call<Material> call, Response<Material> response) {
                            Toast.makeText(AdminLevelDetailActivity.this, "Berhasil update", Toast.LENGTH_SHORT).show();
                            fetchData();
                        }
                        @Override
                        public void onFailure(Call<Material> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showEditQuestionDialog(Question q) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_question, null);
        TextInputEditText etPrompt = view.findViewById(R.id.et_prompt);
        TextInputEditText etMediaUrl = view.findViewById(R.id.et_media_url);
        TextInputEditText etCorrectAnswer = view.findViewById(R.id.et_correct_answer);
        View layoutCorrectAnswer = view.findViewById(R.id.layout_correct_answer);
        View layoutOptionsContainer = view.findViewById(R.id.layout_options_container);
        LinearLayout layoutOptionsList = view.findViewById(R.id.layout_options_list);

        etPrompt.setText(q.getText());
        etMediaUrl.setText(q.getMediaUrl());
        
        // Correct answer hidden for multiple choice, shown for others
        List<View> optionViews = new java.util.ArrayList<>();
        List<android.widget.RadioButton> radioButtons = new java.util.ArrayList<>();
        
        if ("MULTIPLE_CHOICE".equals(q.getType()) || "TRUE_FALSE_VISUAL".equals(q.getType())) {
            layoutCorrectAnswer.setVisibility(View.GONE);
            layoutOptionsContainer.setVisibility(View.VISIBLE);
            
            if (q.getOptions() != null) {
                for (Option opt : q.getOptions()) {
                    View optView = LayoutInflater.from(this).inflate(R.layout.item_admin_option, layoutOptionsList, false);
                    android.widget.RadioButton rbCorrect = optView.findViewById(R.id.radio_is_correct);
                    TextInputEditText etContent = optView.findViewById(R.id.et_option_content);
                    TextInputEditText etOptMedia = optView.findViewById(R.id.et_option_media_url);
                    
                    String label = opt.getLabel() != null ? opt.getLabel() : "X";
                    rbCorrect.setText("Opsi " + label + " (Jawaban Benar)");
                    etContent.setText(opt.getText());
                    etOptMedia.setText(opt.getMediaUrl());
                    
                    rbCorrect.setOnClickListener(v -> {
                        for (android.widget.RadioButton rb : radioButtons) {
                            rb.setChecked(rb == rbCorrect);
                        }
                    });
                    
                    // We store the Option object as tag for later retrieval
                    optView.setTag(opt);
                    radioButtons.add(rbCorrect);
                    optionViews.add(optView);
                    layoutOptionsList.addView(optView);
                }
            }
        } else {
            layoutCorrectAnswer.setVisibility(View.VISIBLE);
            layoutOptionsContainer.setVisibility(View.GONE);
        }

        new AlertDialog.Builder(this)
                .setTitle("Edit Question")
                .setView(view)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("prompt", etPrompt.getText().toString());
                    body.put("mediaUrl", etMediaUrl.getText().toString());
                    
                    if ("MULTIPLE_CHOICE".equals(q.getType()) || "TRUE_FALSE_VISUAL".equals(q.getType())) {
                        List<Map<String, Object>> optionsData = new java.util.ArrayList<>();
                        String selectedCorrectAnswer = null;
                        
                        for (int i = 0; i < optionViews.size(); i++) {
                            View optView = optionViews.get(i);
                            Option opt = (Option) optView.getTag();
                            android.widget.RadioButton rb = radioButtons.get(i);
                            TextInputEditText etContent = optView.findViewById(R.id.et_option_content);
                            TextInputEditText etOptMedia = optView.findViewById(R.id.et_option_media_url);
                            
                            Map<String, Object> optMap = new HashMap<>();
                            optMap.put("id", opt.getId());
                            optMap.put("label", opt.getLabel() != null ? opt.getLabel() : "X");
                            optMap.put("content", etContent.getText().toString());
                            optMap.put("mediaUrl", etOptMedia.getText().toString());
                            optionsData.add(optMap);
                            
                            if (rb.isChecked()) {
                                selectedCorrectAnswer = opt.getLabel() != null ? opt.getLabel() : opt.getId();
                            }
                        }
                        body.put("options", optionsData);
                        if (selectedCorrectAnswer != null) {
                            body.put("correctAnswer", selectedCorrectAnswer);
                        }
                    } else {
                        if (!etCorrectAnswer.getText().toString().isEmpty()) {
                            body.put("correctAnswer", etCorrectAnswer.getText().toString());
                        }
                    }
                    
                    progressBar.setVisibility(View.VISIBLE);
                    adminApi.updateQuestion(q.getId(), body).enqueue(new Callback<Question>() {
                        @Override
                        public void onResponse(Call<Question> call, Response<Question> response) {
                            Toast.makeText(AdminLevelDetailActivity.this, "Berhasil update", Toast.LENGTH_SHORT).show();
                            fetchData();
                        }
                        @Override
                        public void onFailure(Call<Question> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
