package com.example.neuma;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;

import java.util.List;

public class OverlayView extends View {

    private HandLandmarkerResult results;
    private int imageWidth = 1;
    private int imageHeight = 1;

    private String predictedLabel = "";
    private float predictedConfidence = 0f;
    private boolean hasPrediction = false;

    private final Paint cardPaint = new Paint();
    private final Paint titleTextPaint = new Paint();
    private final Paint scoreTextPaint = new Paint();

    private static final float CARD_WIDTH = 900f;
    private static final float CARD_HEIGHT = 600f;
    private static final float CARD_CORNER_RADIUS = 20f;
    private static final float CARD_OFFSET_ABOVE_HAND = 60f;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayView(Context context) {
        super(context);
        init();
    }

    private void init() {
        cardPaint.setColor(0xCC1A1A2E);
        cardPaint.setStyle(Paint.Style.FILL);
        cardPaint.setAntiAlias(true);

        titleTextPaint.setColor(0xFFFFFFFF);
        titleTextPaint.setTextSize(100f);
        titleTextPaint.setAntiAlias(true);
        titleTextPaint.setFakeBoldText(true);

        scoreTextPaint.setColor(0xFF4ADE80);
        scoreTextPaint.setTextSize(150f);
        scoreTextPaint.setAntiAlias(true);
    }

    public void setResults(HandLandmarkerResult result, int imgWidth, int imgHeight) {
        this.results = result;
        this.imageWidth = imgWidth;
        this.imageHeight = imgHeight;
        invalidate();
    }

    public void setPrediction(String label, float confidence) {
        this.predictedLabel = label;
        this.predictedConfidence = confidence;
        this.hasPrediction = true;
        invalidate();
    }

    public void clearPrediction() {
        this.hasPrediction = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (results == null || results.landmarks().isEmpty()) return;

        float scaleX = (float) getWidth() / imageWidth;
        float scaleY = (float) getHeight() / imageHeight;

        for (List<NormalizedLandmark> handLandmarks : results.landmarks()) {
            float minY = Float.MAX_VALUE;
            float sumX = 0f;

            for (NormalizedLandmark lm : handLandmarks) {
                float y = lm.y() * imageHeight * scaleY;
                if (y < minY) minY = y;
                sumX += lm.x() * imageWidth * scaleX;
            }

            float centerX = sumX / handLandmarks.size();
            float cardCenterX = mirrorX(centerX);
            float cardBottomY = minY - CARD_OFFSET_ABOVE_HAND;

            drawInfoCard(canvas, cardCenterX, cardBottomY);
        }
    }

    private void drawInfoCard(Canvas canvas, float centerX, float bottomY) {
        float left = centerX - CARD_WIDTH / 2f;
        float right = centerX + CARD_WIDTH / 2f;
        float top = bottomY - CARD_HEIGHT;
        float bottom = bottomY;

        RectF cardRect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(cardRect, CARD_CORNER_RADIUS, CARD_CORNER_RADIUS, cardPaint);

        String titleText = "Tangan Terdeteksi";
        String scoreText = hasPrediction
                ? String.format("%s — %.1f%%", predictedLabel, predictedConfidence)
                : "Menganalisis...";

        float titleX = centerX - (titleTextPaint.measureText(titleText) / 2f);
        float scoreX = centerX - (scoreTextPaint.measureText(scoreText) / 2f);

        canvas.drawText(titleText, titleX, top + 100f, titleTextPaint);
        canvas.drawText(scoreText, scoreX, top + 400f, scoreTextPaint);
    }

    private float mirrorX(float x) {
        return getWidth() - x;
    }
}
