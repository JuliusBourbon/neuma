package com.example.neuma;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Category;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LearnActivity extends AppCompatActivity implements HandLandmarkerHelper.LandmarkerListener {

    private static final String TAG = "LearnActivity";
    private static final int CAMERA_PERMISSION_CODE = 101;

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
    private LinearLayout layoutOptionsContainer, layoutTrueFalse;
    private Button btnTrue, btnFalse;
    private Button btnSubmitAnswer, btnSkipQuestion;

    // Camera / SIGN_PRACTICE Views
    private FrameLayout layoutCameraContainer;
    private PreviewView cameraPreview;
    private OverlayView overlayView;
    private TextView tvDetectionResult;

    // Camera / Detection components
    private HandLandmarkerHelper handLandmarkerHelper;
    private OnnxHelper onnxHelper;
    private ExecutorService cameraExecutor;
    private int lastImageWidth = 1;
    private int lastImageHeight = 1;
    private boolean isCameraRunning = false;

    private LevelApi levelApi;
    private AttemptApi attemptApi;

    private String selectedAnswer = null;
    private java.util.List<View> optionViews = new java.util.ArrayList<>();

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
        layoutOptionsContainer = findViewById(R.id.layout_options_container);
        layoutTrueFalse = findViewById(R.id.layout_true_false);
        btnTrue = findViewById(R.id.btn_true);
        btnFalse = findViewById(R.id.btn_false);
        btnSubmitAnswer = findViewById(R.id.btn_submit_answer);
        btnSkipQuestion = findViewById(R.id.btn_skip_question);

        // Camera
        layoutCameraContainer = findViewById(R.id.layout_camera_container);
        cameraPreview = findViewById(R.id.camera_preview);
        overlayView = findViewById(R.id.overlay_view);
        tvDetectionResult = findViewById(R.id.tv_detection_result);

        levelApi = ApiClient.getAuthClient(this).create(LevelApi.class);
        attemptApi = ApiClient.getAuthClient(this).create(AttemptApi.class);

        btnNextMaterial.setOnClickListener(v -> handleNextMaterial());
        btnSubmitAnswer.setOnClickListener(v -> submitAnswer());
        btnSkipQuestion.setOnClickListener(v -> skipQuestion());

        fetchData();
    }

    // ─── Camera / Detection Lifecycle ───────────────────────────────────────

    private void initCameraComponents() {
        if (onnxHelper == null) {
            onnxHelper = new OnnxHelper(this);
        }
        if (handLandmarkerHelper == null) {
            handLandmarkerHelper = new HandLandmarkerHelper(this, this);
        }
        if (cameraExecutor == null) {
            cameraExecutor = Executors.newSingleThreadExecutor();
        }
    }

    private void startCameraForSignPractice() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            bindCamera();
        }
    }

    private void bindCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeFrame);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
                isCameraRunning = true;

            } catch (Exception e) {
                Log.e(TAG, "bindCamera error: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void stopCamera() {
        if (!isCameraRunning) return;
        try {
            ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
            future.addListener(() -> {
                try {
                    ProcessCameraProvider provider = future.get();
                    provider.unbindAll();
                    isCameraRunning = false;
                } catch (Exception e) {
                    Log.e(TAG, "stopCamera error: " + e.getMessage());
                }
            }, ContextCompat.getMainExecutor(this));
        } catch (Exception e) {
            Log.e(TAG, "stopCamera outer error: " + e.getMessage());
        }
    }

    private void analyzeFrame(ImageProxy imageProxy) {
        Bitmap bitmap = yuv420ToBitmap(imageProxy);
        if (bitmap != null) {
            int rotation = imageProxy.getImageInfo().getRotationDegrees();
            Bitmap rotatedBitmap = rotateBitmap(bitmap, rotation);
            MPImage mpImage = new BitmapImageBuilder(rotatedBitmap).build();
            long frameTime = System.currentTimeMillis();
            lastImageWidth = rotatedBitmap.getWidth();
            lastImageHeight = rotatedBitmap.getHeight();
            if (handLandmarkerHelper != null) {
                handLandmarkerHelper.detectAsync(mpImage, frameTime);
            }
        }
        imageProxy.close();
    }

    private Bitmap yuv420ToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
        ImageProxy.PlaneProxy uPlane = image.getPlanes()[1];
        ImageProxy.PlaneProxy vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        int width = image.getWidth();
        int height = image.getHeight();
        byte[] nv21 = new byte[width * height * 3 / 2];

        int yRowStride = yPlane.getRowStride();
        int yPixelStride = yPlane.getPixelStride();
        int pos = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                nv21[pos++] = yBuffer.get(row * yRowStride + col * yPixelStride);
            }
        }

        int uvRowStride = uPlane.getRowStride();
        int uvPixelStride = uPlane.getPixelStride();
        int uvHeight = height / 2;
        int uvWidth = width / 2;

        for (int row = 0; row < uvHeight; row++) {
            for (int col = 0; col < uvWidth; col++) {
                int vuIndex = row * uvRowStride + col * uvPixelStride;
                nv21[pos++] = vBuffer.get(vuIndex);
                nv21[pos++] = uBuffer.get(vuIndex);
            }
        }

        android.graphics.YuvImage yuvImage = new android.graphics.YuvImage(
                nv21, android.graphics.ImageFormat.NV21, width, height, null);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, width, height), 90, out);
        byte[] imageBytes = out.toByteArray();
        return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        if (rotationDegrees == 0) return bitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // ─── HandLandmarkerHelper.LandmarkerListener ────────────────────────────

    @Override
    public void onResults(HandLandmarkerResult result) {
        runOnUiThread(() -> {
            overlayView.setResults(result, lastImageWidth, lastImageHeight);

            float[] leftHand = new float[63];
            float[] rightHand = new float[63];

            List<List<NormalizedLandmark>> allLandmarks = result.landmarks();
            List<List<Category>> allHandedness = result.handednesses();

            for (int i = 0; i < allLandmarks.size(); i++) {
                List<NormalizedLandmark> handLandmarks = allLandmarks.get(i);
                String handLabel = allHandedness.get(i).get(0).categoryName();

                float[] coords = new float[63];
                for (int j = 0; j < handLandmarks.size(); j++) {
                    NormalizedLandmark lm = handLandmarks.get(j);
                    coords[j * 3]     = lm.x();
                    coords[j * 3 + 1] = lm.y();
                    coords[j * 3 + 2] = lm.z();
                }

                if (handLabel.equals("Left")) {
                    leftHand = coords;
                } else {
                    rightHand = coords;
                }
            }

            float[] features156 = FeatureExtractor.extractFullFeatures(leftHand, rightHand);

            if (onnxHelper != null) {
                OnnxHelper.PredictionResult prediction = onnxHelper.predict(features156);
                if (prediction != null) {
                    overlayView.setPrediction(prediction.label, prediction.confidence);
                    selectedAnswer = prediction.label;
                    tvDetectionResult.setText("Terdeteksi: " + prediction.label
                            + " (" + String.format("%.1f", prediction.confidence) + "%)");
                }
            }
        });
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "HandLandmarker error: " + error);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bindCamera();
        } else {
            Toast.makeText(this, "Izin kamera diperlukan untuk fitur ini", Toast.LENGTH_LONG).show();
        }
    }

    // ─── Learn Flow ─────────────────────────────────────────────────────────

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        layoutMaterial.setVisibility(View.GONE);
        layoutQuiz.setVisibility(View.GONE);

        levelApi.getMaterials(levelId).enqueue(new Callback<List<Material>>() {
            @Override
            public void onResponse(Call<List<Material>> call, Response<List<Material>> response) {
                if (response.isSuccessful()) {
                    materials = response.body();
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
        stopCamera();
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
        selectedAnswer = null;

        // Reset semua section
        layoutOptionsContainer.setVisibility(View.GONE);
        layoutTrueFalse.setVisibility(View.GONE);
        layoutCameraContainer.setVisibility(View.GONE);
        layoutOptionsContainer.removeAllViews();
        optionViews.clear();
        btnTrue.setAlpha(1.0f);
        btnFalse.setAlpha(1.0f);

        // Hentikan kamera jika soal sebelumnya SIGN_PRACTICE
        stopCamera();

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
            layoutOptionsContainer.setVisibility(View.VISIBLE);
            if (q.getOptions() != null) {
                for (Option opt : q.getOptions()) {
                    View optionView = android.view.LayoutInflater.from(this)
                            .inflate(R.layout.item_quiz_option, layoutOptionsContainer, false);
                    RadioButton radioIndicator = optionView.findViewById(R.id.radio_option_indicator);
                    TextView tvText = optionView.findViewById(R.id.tv_option_text);
                    ImageView ivMedia = optionView.findViewById(R.id.iv_option_media);

                    tvText.setText(opt.getText());
                    if (opt.getMediaUrl() != null && !opt.getMediaUrl().isEmpty()) {
                        ivMedia.setVisibility(View.VISIBLE);
                        Glide.with(this).load(opt.getMediaUrl()).into(ivMedia);
                    } else {
                        ivMedia.setVisibility(View.GONE);
                    }

                    optionView.setOnClickListener(v -> {
                        selectedAnswer = opt.getLabel() != null ? opt.getLabel() : opt.getId();
                        for (View ov : optionViews) {
                            RadioButton rb = ov.findViewById(R.id.radio_option_indicator);
                            rb.setChecked(ov == optionView);
                        }
                    });

                    optionViews.add(optionView);
                    layoutOptionsContainer.addView(optionView);
                }
            }
        } else if ("TRUE_FALSE_VISUAL".equals(q.getType())) {
            layoutTrueFalse.setVisibility(View.VISIBLE);
            btnTrue.setOnClickListener(v -> {
                selectedAnswer = "TRUE";
                btnTrue.setAlpha(1.0f);
                btnFalse.setAlpha(0.5f);
            });
            btnFalse.setOnClickListener(v -> {
                selectedAnswer = "FALSE";
                btnTrue.setAlpha(0.5f);
                btnFalse.setAlpha(1.0f);
            });
        } else if ("SIGN_PRACTICE".equals(q.getType())) {
            layoutCameraContainer.setVisibility(View.VISIBLE);
            tvDetectionResult.setText("Arahkan kamera ke tangan Anda");
            initCameraComponents();
            startCameraForSignPractice();
        }
    }

    private void submitAnswer() {
        Question q = questions.get(currentQuestionIndex);

        if ("SIGN_PRACTICE".equals(q.getType())) {
            if (selectedAnswer == null) {
                Toast.makeText(this, "Belum ada tangan yang terdeteksi, arahkan tangan ke kamera", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if ("MULTIPLE_CHOICE".equals(q.getType()) || "TRUE_FALSE_VISUAL".equals(q.getType())) {
            if (selectedAnswer == null) {
                Toast.makeText(this, "Pilih jawaban terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String answer = selectedAnswer;
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
        stopCamera();
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
        btnTrue.setEnabled(!isLoading);
        btnFalse.setEnabled(!isLoading);
        for (View v : optionViews) {
            v.setEnabled(!isLoading);
            View rb = v.findViewById(R.id.radio_option_indicator);
            if (rb != null) rb.setEnabled(!isLoading);
        }
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCamera();
        if (handLandmarkerHelper != null) {
            handLandmarkerHelper.close();
            handLandmarkerHelper = null;
        }
        if (onnxHelper != null) {
            onnxHelper.close();
            onnxHelper = null;
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            cameraExecutor = null;
        }
    }
}
