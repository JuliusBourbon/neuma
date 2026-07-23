package com.example.neuma;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.neuma.models.AnswerRequest;
import com.example.neuma.models.AnswerResponse;
import com.example.neuma.models.AttemptResponse;
import com.example.neuma.models.FinishAttemptResponse;
import com.example.neuma.models.Material;
import com.example.neuma.models.Option;
import com.example.neuma.models.Question;
import com.example.neuma.models.SkipRequest;
import com.example.neuma.models.StartAttemptRequest;
import com.example.neuma.network.AttemptApi;
import com.example.neuma.network.LevelApi;
import com.example.neuma.utils.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearnActivity extends AppCompatActivity {

    private String levelId;
    private String attemptId;

    private List<Material> materials;
    private List<Question> questions;
    private int currentMaterialIndex = 0;
    private int currentQuestionIndex = 0;

    private ProgressBar progressBar;
    private View layoutMaterial, layoutQuiz;

    // Material Views
    private TextView tvMaterialType, tvMaterialTitle, tvMaterialContent;
    private ImageView ivMaterialMedia;
    private Button btnNextMaterial;

    // Quiz Views
    private TextView tvQuizHeader, tvQuestionText;
    private ImageView ivQuestionMedia;
    private RadioGroup radioGroupOptions;
    private Button btnSimulateCamera, btnSubmitAnswer, btnSkipQuestion;

    private LevelApi levelApi;
    private AttemptApi attemptApi;
    
    private String simulatedAnswer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        levelId = getIntent().getStringExtra("LEVEL_ID");

        progressBar = findViewById(R.id.progress_bar_learn);
        layoutMaterial = findViewById(R.id.layout_material);
        layoutQuiz = findViewById(R.id.layout_quiz);

        // Material
        tvMaterialType = findViewById(R.id.tv_material_type);
        tvMaterialTitle = findViewById(R.id.tv_material_title);
        tvMaterialContent = findViewById(R.id.tv_material_content);
        ivMaterialMedia = findViewById(R.id.iv_material_media);
        btnNextMaterial = findViewById(R.id.btn_next_material);

        // Quiz
        tvQuizHeader = findViewById(R.id.tv_quiz_header);
        tvQuestionText = findViewById(R.id.tv_question_text);
        ivQuestionMedia = findViewById(R.id.iv_question_media);
        radioGroupOptions = findViewById(R.id.radio_group_options);
        btnSimulateCamera = findViewById(R.id.btn_simulate_camera);
        btnSubmitAnswer = findViewById(R.id.btn_submit_answer);
        btnSkipQuestion = findViewById(R.id.btn_skip_question);

        levelApi = ApiClient.getAuthClient(this).create(LevelApi.class);
        attemptApi = ApiClient.getAuthClient(this).create(AttemptApi.class);

        btnNextMaterial.setOnClickListener(v -> handleNextMaterial());
        btnSubmitAnswer.setOnClickListener(v -> submitAnswer());
        btnSkipQuestion.setOnClickListener(v -> skipQuestion());
        
        btnSimulateCamera.setOnClickListener(v -> showSimulateCameraDialog());

        fetchData();
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        layoutMaterial.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.GONE);

        // Fetch Materials
        levelApi.getMaterials(levelId).enqueue(new Callback<List<Material>>() {
            @Override
            public void onResponse(Call<List<Material>> call, Response<List<Material>> response) {
                if (response.isSuccessful()) {
                    materials = response.body();
                    
                    // Fetch Questions
                    levelApi.getQuestions(levelId).enqueue(new Callback<List<Question>>() {
                        @Override
                        public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                questions = response.body();
                                startLearnFlow();
                            } else {
                                showError("Gagal memuat soal");
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Question>> call, Throwable t) {
                            showError("Error: " + t.getMessage());
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    showError("Gagal memuat materi");
                }
            }

            @Override
            public void onFailure(Call<List<Material>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void startLearnFlow() {
        if (materials != null && !materials.isEmpty()) {
            currentMaterialIndex = 0;
            showMaterial();
        } else {
            startAttemptAndShowQuiz();
        }
    }

    private void showMaterial() {
        layoutQuiz.setVisibility(View.GONE);
        layoutMaterial.setVisibility(View.VISIBLE);

        Material m = materials.get(currentMaterialIndex);
        tvMaterialType.setText(m.getType() != null ? m.getType().replace("_", " ") : "MATERI");
        tvMaterialTitle.setText(m.getTitle());
        tvMaterialContent.setText(m.getTextContent());

        if (m.getMediaUrl() != null && !m.getMediaUrl().isEmpty()) {
            ivMaterialMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(m.getMediaUrl()).into(ivMaterialMedia);
        } else {
            ivMaterialMedia.setVisibility(View.GONE);
        }
    }

    private void handleNextMaterial() {
        currentMaterialIndex++;
        if (currentMaterialIndex < materials.size()) {
            showMaterial();
        } else {
            startAttemptAndShowQuiz();
        }
    }

    private void startAttemptAndShowQuiz() {
        progressBar.setVisibility(View.VISIBLE);
        layoutMaterial.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.GONE);

        attemptApi.startAttempt(new StartAttemptRequest(levelId)).enqueue(new Callback<AttemptResponse>() {
            @Override
            public void onResponse(Call<AttemptResponse> call, Response<AttemptResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    attemptId = response.body().getAttemptId();
                    currentQuestionIndex = 0;
                    if (questions != null && !questions.isEmpty()) {
                        showQuestion();
                    } else {
                        finishAttempt();
                    }
                } else {
                    showError("Gagal memulai tes");
                }
            }

            @Override
            public void onFailure(Call<AttemptResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void showQuestion() {
        layoutMaterial.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.VISIBLE);
        simulatedAnswer = null;
        radioGroupOptions.clearCheck();
        radioGroupOptions.removeAllViews();

        Question q = questions.get(currentQuestionIndex);
        tvQuizHeader.setText("SOAL " + (currentQuestionIndex + 1) + " DARI " + questions.size());
        tvQuestionText.setText(q.getText());

        if (q.getMediaUrl() != null && !q.getMediaUrl().isEmpty()) {
            ivQuestionMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(q.getMediaUrl()).into(ivQuestionMedia);
        } else {
            ivQuestionMedia.setVisibility(View.GONE);
        }

        if ("MULTIPLE_CHOICE".equals(q.getType())) {
            radioGroupOptions.setVisibility(View.VISIBLE);
            btnSimulateCamera.setVisibility(View.GONE);
            
            if (q.getOptions() != null) {
                for (Option opt : q.getOptions()) {
                    RadioButton rb = new RadioButton(this);
                    rb.setText(opt.getText());
                    rb.setTag(opt.getText());
                    rb.setTextSize(16f);
                    rb.setPadding(0, 16, 0, 16);
                    radioGroupOptions.addView(rb);
                }
            }
        } else if ("SIGN_PRACTICE".equals(q.getType())) {
            radioGroupOptions.setVisibility(View.GONE);
            btnSimulateCamera.setVisibility(View.VISIBLE);
            btnSimulateCamera.setText("SIMULASIKAN DETEKSI KAMERA");
        }
    }
    
    private void showSimulateCameraDialog() {
        TextInputEditText input = new TextInputEditText(this);
        input.setHint("Masukkan jawaban deteksi (misal: A)");
        input.setPadding(32, 32, 32, 32);
        
        new AlertDialog.Builder(this)
            .setTitle("Simulasi ONNX (Kamera)")
            .setMessage("Seolah-olah model mendeteksi gerakan tangan Anda:")
            .setView(input)
            .setPositiveButton("Kirim Deteksi", (dialog, which) -> {
                String val = input.getText() != null ? input.getText().toString().trim() : "";
                if (!val.isEmpty()) {
                    simulatedAnswer = val;
                    btnSimulateCamera.setText("TERDETEKSI: " + val);
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void submitAnswer() {
        Question q = questions.get(currentQuestionIndex);
        String answer = null;

        if ("MULTIPLE_CHOICE".equals(q.getType())) {
            int selectedId = radioGroupOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Pilih jawaban terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton rb = findViewById(selectedId);
            answer = rb.getTag().toString();
        } else if ("SIGN_PRACTICE".equals(q.getType())) {
            if (simulatedAnswer == null) {
                Toast.makeText(this, "Lakukan simulasi deteksi kamera terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            answer = simulatedAnswer;
        }

        setLoadingState(true);
        attemptApi.submitAnswer(attemptId, new AnswerRequest(q.getId(), answer)).enqueue(new Callback<AnswerResponse>() {
            @Override
            public void onResponse(Call<AnswerResponse> call, Response<AnswerResponse> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    AnswerResponse ans = response.body();
                    if (ans.isCorrect()) {
                        showFeedbackDialog("Benar!", "Kamu mendapat " + ans.getTotalThisAnswer() + " poin.", true);
                    } else {
                        showFeedbackDialog("Salah!", "Jawaban salah. Coba lagi atau lewati soal ini.", false);
                    }
                } else {
                    showError("Gagal mengirim jawaban");
                }
            }

            @Override
            public void onFailure(Call<AnswerResponse> call, Throwable t) {
                setLoadingState(false);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void skipQuestion() {
        Question q = questions.get(currentQuestionIndex);
        setLoadingState(true);
        attemptApi.skipQuestion(attemptId, new SkipRequest(q.getId())).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                setLoadingState(false);
                if (response.isSuccessful()) {
                    Toast.makeText(LearnActivity.this, "Soal dilewati", Toast.LENGTH_SHORT).show();
                    nextQuestion();
                } else {
                    showError("Gagal melewati soal");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                setLoadingState(false);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void showFeedbackDialog(String title, String message, boolean isCorrect) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Lanjut", (dialog, which) -> {
                    if (isCorrect) {
                        nextQuestion();
                    }
                })
                .show();
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            showQuestion();
        } else {
            finishAttempt();
        }
    }

    private void finishAttempt() {
        progressBar.setVisibility(View.VISIBLE);
        layoutQuiz.setVisibility(View.GONE);

        attemptApi.finishAttempt(attemptId).enqueue(new Callback<FinishAttemptResponse>() {
            @Override
            public void onResponse(Call<FinishAttemptResponse> call, Response<FinishAttemptResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    FinishAttemptResponse res = response.body();
                    
                    Intent intent = new Intent(LearnActivity.this, ScoreActivity.class);
                    intent.putExtra("TOTAL_SCORE", res.getTotalScore());
                    
                    if (res.getNewAchievements() != null && !res.getNewAchievements().isEmpty()) {
                        String achievementsJson = new com.google.gson.Gson().toJson(res.getNewAchievements());
                        intent.putExtra("NEW_ACHIEVEMENTS", achievementsJson);
                    }
                    
                    startActivity(intent);
                    finish();
                } else {
                    showError("Gagal menyelesaikan sesi tes");
                }
            }

            @Override
            public void onFailure(Call<FinishAttemptResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSubmitAnswer.setEnabled(!isLoading);
        btnSkipQuestion.setEnabled(!isLoading);
        btnSimulateCamera.setEnabled(!isLoading);
        for(int i = 0; i < radioGroupOptions.getChildCount(); i++){
            radioGroupOptions.getChildAt(i).setEnabled(!isLoading);
        }
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
