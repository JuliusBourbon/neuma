package com.example.neuma;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;

import java.util.List;

public class OverlayView extends View {

    // ─── Data ───────────────────────────────────────────────────────────────
    private HandLandmarkerResult results;
    private int imageWidth = 1;
    private int imageHeight = 1;

    private String predictedLabel = "";
    private float predictedConfidence = 0f;
    private boolean hasPrediction = false;

    private String targetAnswer = "";
    private float holdProgress = 0f; // 0–100

    // ─── MediaPipe hand skeleton connection indices ──────────────────────────
    private static final int[][] HAND_CONNECTIONS = {
            // Thumb
            {0, 1}, {1, 2}, {2, 3}, {3, 4},
            // Index
            {0, 5}, {5, 6}, {6, 7}, {7, 8},
            // Middle
            {0, 9}, {9, 10}, {10, 11}, {11, 12},
            // Ring
            {0, 13}, {13, 14}, {14, 15}, {15, 16},
            // Pinky
            {0, 17}, {17, 18}, {18, 19}, {19, 20},
            // Palm
            {5, 9}, {9, 13}, {13, 17}
    };

    // ─── Paints ─────────────────────────────────────────────────────────────
    private final Paint skeletonLinePaint = new Paint();
    private final Paint skeletonJointPaint = new Paint();

    private final Paint cardPaint = new Paint();
    // private final Paint cardBorderPaint = new Paint();

    private final Paint labelSmallPaint = new Paint();    // "TARGET" / "TERDETEKSI"
    private final Paint targetTextPaint = new Paint();    // huruf target besar
    private final Paint detectedTextPaint = new Paint();  // label + confidence
    private final Paint holdLabelPaint = new Paint();     // "Tahan 5 detik"

    private final Paint progressBgPaint = new Paint();    // progress bar bg
    private final Paint progressFillPaint = new Paint();  // progress bar fill

    // ─── Card geometry ──────────────────────────────────────────────────────
    private static final float CARD_WIDTH        = 480f;
    private static final float CARD_HEIGHT       = 240f;
    private static final float CARD_CORNER       = 18f;
    private static final float CARD_OFFSET_ABOVE = 36f;
    // private static final float CARD_BORDER_W     = 5f;

    // Primary brand green
//    private static final int COLOR_CARD_BG     = 0xEE0D0D0D;
    // private static final int COLOR_CARD_BORDER = 0xFF25DE1C;
//    private static final int COLOR_WHITE       = 0xFFFFFFFF;
    private static final int COLOR_GREY        = 0xFFFFFFFF;
//    private static final int COLOR_PROGRESS_BG = 0xFF2A2A2A;

    // ─── Constructor ────────────────────────────────────────────────────────
    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayView(Context context) {
        super(context);
        init();
    }

    private void init() {
        int colorPrimary = androidx.core.content.ContextCompat.getColor(getContext(), R.color.primary);
        int colorSecondary = androidx.core.content.ContextCompat.getColor(getContext(), R.color.secondary);
        int colorTertiary = androidx.core.content.ContextCompat.getColor(getContext(), R.color.tertiary);

        // Skeleton line
        skeletonLinePaint.setColor(colorSecondary);
        skeletonLinePaint.setStrokeWidth(6f);
        skeletonLinePaint.setStyle(Paint.Style.STROKE);
        skeletonLinePaint.setAntiAlias(true);
        skeletonLinePaint.setAlpha(200);

        // Skeleton joint dots
        skeletonJointPaint.setColor(colorPrimary);
        skeletonJointPaint.setStyle(Paint.Style.FILL);
        skeletonJointPaint.setAntiAlias(true);

        // Card background
        cardPaint.setColor(colorTertiary);
        cardPaint.setStyle(Paint.Style.FILL);
        cardPaint.setAntiAlias(true);

        // Card border (primary color, styled like bg_camera_progress_border)
        // cardBorderPaint.setColor(COLOR_CARD_BORDER);
        // cardBorderPaint.setStyle(Paint.Style.STROKE);
        // cardBorderPaint.setStrokeWidth(CARD_BORDER_W);
        // cardBorderPaint.setAntiAlias(true);

        // "TARGET" / "TERDETEKSI" small label
        labelSmallPaint.setColor(COLOR_GREY);
        labelSmallPaint.setTextSize(52f);
        labelSmallPaint.setAntiAlias(true);
        labelSmallPaint.setFakeBoldText(true);

        // Target letter (large)
        targetTextPaint.setColor(colorSecondary);
        targetTextPaint.setTextSize(140f);
        targetTextPaint.setAntiAlias(true);
        targetTextPaint.setFakeBoldText(true);

        // Detected label + confidence
        detectedTextPaint.setColor(colorPrimary);
        detectedTextPaint.setTextSize(100f);
        detectedTextPaint.setAntiAlias(true);
        detectedTextPaint.setFakeBoldText(true);

        // "Tahan 4 detik" label
        holdLabelPaint.setColor(colorPrimary);
        holdLabelPaint.setTextSize(52f);
        holdLabelPaint.setAntiAlias(true);

        // Progress bar background
        progressBgPaint.setColor(COLOR_GREY);
        progressBgPaint.setStyle(Paint.Style.FILL);
        progressBgPaint.setAntiAlias(true);

        // Progress bar fill
        progressFillPaint.setColor(colorSecondary);
        progressFillPaint.setStyle(Paint.Style.FILL);
        progressFillPaint.setAntiAlias(true);
    }

    // ─── Setters (called from LearnActivity) ─────────────────────────────────
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

    /** Set target answer to display inside the card. */
    public void setTargetAnswer(String answer) {
        this.targetAnswer = answer != null ? answer : "";
        invalidate();
    }

    /** Set hold progress 0–100 to render inside the card. */
    public void setHoldProgress(float progress) {
        this.holdProgress = progress;
        invalidate();
    }

    // ─── Draw ───────────────────────────────────────────────────────────────
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (results == null || results.landmarks().isEmpty()) return;

        float scaleX = (float) getWidth() / imageWidth;
        float scaleY = (float) getHeight() / imageHeight;

        List<List<NormalizedLandmark>> allLandmarks = results.landmarks();

        // ── Estimasi skala kartu dari ukuran tangan ──────────────────────────
        // Rata-rata scale semua tangan yang terdeteksi agar kartu konsisten
        float totalScale = 0f;
        for (List<NormalizedLandmark> hand : allLandmarks) {
            totalScale += computeHandScale(hand, scaleX, scaleY);
        }
        float cardScale = totalScale / allLandmarks.size();

        // ── Draw skeleton for each hand ──────────────────────────────────────
        for (List<NormalizedLandmark> handLandmarks : allLandmarks) {
            drawSkeleton(canvas, handLandmarks, scaleX, scaleY, cardScale);
        }

        // ── Compute card anchor position ──────────────────────────────────────
        float cardCenterX;
        float cardBottomY;
        float scaledCardWidth  = CARD_WIDTH  * cardScale;
        float scaledCardHeight = CARD_HEIGHT * cardScale;
        float scaledOffset     = CARD_OFFSET_ABOVE * cardScale;

        if (allLandmarks.size() >= 2) {
            // 2 tangan: kartu muncul di antara kedua tangan
            float[] hand1Center = getHandCenter(allLandmarks.get(0), scaleX, scaleY);
            float[] hand2Center = getHandCenter(allLandmarks.get(1), scaleX, scaleY);
            float rawCenterX = (hand1Center[0] + hand2Center[0]) / 2f;
            cardCenterX = mirrorX(rawCenterX);
            float minY1 = getHandMinY(allLandmarks.get(0), scaleY);
            float minY2 = getHandMinY(allLandmarks.get(1), scaleY);
            cardBottomY = Math.min(minY1, minY2) - scaledOffset;
        } else {
            // 1 tangan: kartu di atas tangan tersebut
            float[] center = getHandCenter(allLandmarks.get(0), scaleX, scaleY);
            cardCenterX = mirrorX(center[0]);
            cardBottomY = getHandMinY(allLandmarks.get(0), scaleY) - scaledOffset;
        }

        // Pastikan kartu tidak keluar layar
        float halfW = scaledCardWidth / 2f;
        cardCenterX = Math.max(halfW + 16f, Math.min(getWidth() - halfW - 16f, cardCenterX));
        cardBottomY = Math.max(scaledCardHeight + 16f, Math.min(getHeight() - 16f, cardBottomY));

        drawInfoCard(canvas, cardCenterX, cardBottomY, cardScale);
    }

    // ─── Skeleton Drawing ────────────────────────────────────────────────────
    private void drawSkeleton(Canvas canvas, List<NormalizedLandmark> landmarks,
                               float scaleX, float scaleY, float cardScale) {
        // Compute pixel coords (mirrored for front camera)
        float[] px = new float[landmarks.size()];
        float[] py = new float[landmarks.size()];
        for (int i = 0; i < landmarks.size(); i++) {
            NormalizedLandmark lm = landmarks.get(i);
            float rawX = lm.x() * imageWidth * scaleX;
            px[i] = mirrorX(rawX);
            py[i] = lm.y() * imageHeight * scaleY;
        }

        // Skeleton line thickness mengikuti skala tangan
        skeletonLinePaint.setStrokeWidth(6f * cardScale);

        // Draw connections
        for (int[] conn : HAND_CONNECTIONS) {
            int a = conn[0], b = conn[1];
            if (a < px.length && b < px.length) {
                canvas.drawLine(px[a], py[a], px[b], py[b], skeletonLinePaint);
            }
        }

        // Draw joint dots — radius juga mengikuti skala
        for (int i = 0; i < px.length; i++) {
            float radius = ((i == 0) ? 14f : 9f) * cardScale;
            canvas.drawCircle(px[i], py[i], radius, skeletonJointPaint);
        }
    }

    // ─── Info Card Drawing ────────────────────────────────────────────────────
    /**
     * Menggambar info card dengan dimensi dan ukuran teks yang disesuaikan
     * berdasarkan cardScale (proxy jarak tangan ke kamera).
     */
    private void drawInfoCard(Canvas canvas, float centerX, float bottomY, float cardScale) {
        // Semua dimensi kartu di-scale
        float cardW      = CARD_WIDTH  * cardScale;
        float cardH      = CARD_HEIGHT * cardScale;
        float corner     = CARD_CORNER * cardScale;
        float padding    = 36f * cardScale;

        float left   = centerX - cardW / 2f;
        float right  = centerX + cardW / 2f;
        float top    = bottomY - cardH;
        float bottom = bottomY;

        RectF cardRect = new RectF(left, top, right, bottom);

        // Background
        canvas.drawRoundRect(cardRect, corner, corner, cardPaint);

        // Border — stroke width juga mengikuti skala
        // cardBorderPaint.setStrokeWidth(CARD_BORDER_W * cardScale);
        // canvas.drawRoundRect(cardRect, corner, corner, cardBorderPaint);

        float innerLeft  = left + padding;
        float innerRight = right - padding;

        // ── Row 1: TARGET & TERDETEKSI ────────────────────────────────────────
        float dividerX   = centerX;
        float row1LabelY = top  + 46f  * cardScale;
        float row1ValueY = top  + 120f * cardScale;

        // Ukuran teks ikut skala
        labelSmallPaint.setTextSize(26f * cardScale);
        targetTextPaint.setTextSize(70f * cardScale);

        // Left half: TARGET
        canvas.drawText("TARGET", innerLeft, row1LabelY, labelSmallPaint);
        String targetDisplay = targetAnswer.isEmpty() ? "—" : targetAnswer.toUpperCase();
        canvas.drawText(targetDisplay, innerLeft, row1ValueY, targetTextPaint);

        // Vertical divider
        Paint divPaint = new Paint();
        divPaint.setColor(0xFF333333);
        divPaint.setStrokeWidth(1.5f * cardScale);
        canvas.drawLine(dividerX, top + 12f * cardScale, dividerX, top + 140f * cardScale, divPaint);

        // Right half: TERDETEKSI
        float rightPad = dividerX + 18f * cardScale;
        canvas.drawText("TERDETEKSI", rightPad, row1LabelY, labelSmallPaint);
        String detectedDisplay = hasPrediction
                ? String.format("%s  %.1f%%", predictedLabel, predictedConfidence)
                : "—";
        float detectedTextSize = (predictedLabel.length() > 3 ? 38f : 50f) * cardScale;
        detectedTextPaint.setTextSize(detectedTextSize);
        canvas.drawText(detectedDisplay, rightPad, row1ValueY, detectedTextPaint);

        // ── Divider horizontal ────────────────────────────────────────────────
        float hDivY = top + 150f * cardScale;
        canvas.drawLine(innerLeft, hDivY, innerRight, hDivY, divPaint);

        // ── Row 2: Hold progress bar + label ────────────────────────────────
        float barTop    = hDivY + 14f  * cardScale;
        float barBottom = barTop + 26f * cardScale;
        float barCorner = 6f * cardScale;

        // Background track
        RectF bgBar = new RectF(innerLeft, barTop, innerRight, barBottom);
        canvas.drawRoundRect(bgBar, barCorner, barCorner, progressBgPaint);

        // Fill based on holdProgress (0–100)
        if (holdProgress > 0f) {
            float fillRight = innerLeft + (innerRight - innerLeft) * (holdProgress / 100f);
            RectF fillBar = new RectF(innerLeft, barTop, fillRight, barBottom);
            canvas.drawRoundRect(fillBar, barCorner, barCorner, progressFillPaint);
        }

        // Hold label
        holdLabelPaint.setTextSize(26f * cardScale);
        float holdLabelY = barBottom + 28f * cardScale;
        String holdText = holdProgress > 0f
                ? String.format("Pertahankan isyarat... %.0f%%", holdProgress)
                : "Tahan posisi 4 detik untuk submit";
        float holdTextX = centerX - holdLabelPaint.measureText(holdText) / 2f;
        canvas.drawText(holdText, holdTextX, holdLabelY, holdLabelPaint);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Estimasi skala kartu berdasarkan jarak pixel wrist (landmark 0) → middle MCP (landmark 9).
     * Semakin dekat tangan ke kamera → wrist-MCP distance lebih besar → cardScale lebih besar.
     *
     * Referensi: jarak ~200px → scale 1.0 (ukuran kartu normal)
     * Clamp: [0.45, 1.8] agar tidak terlalu kecil/besar.
     */
    private float computeHandScale(List<NormalizedLandmark> landmarks, float scaleX, float scaleY) {
        if (landmarks.size() < 10) return 1.0f;

        NormalizedLandmark wrist = landmarks.get(0);
        NormalizedLandmark mcp   = landmarks.get(9); // Middle finger MCP

        float wristPxX = wrist.x() * imageWidth * scaleX;
        float wristPxY = wrist.y() * imageHeight * scaleY;
        float mcpPxX   = mcp.x()   * imageWidth * scaleX;
        float mcpPxY   = mcp.y()   * imageHeight * scaleY;

        float dx = mcpPxX - wristPxX;
        float dy = mcpPxY - wristPxY;
        float distancePx = (float) Math.sqrt(dx * dx + dy * dy);

        // Referensi jarak "normal" = 200px
        float scale = distancePx / 200f;

        // Clamp agar kartu tetap terbaca
        return Math.max(0.45f, Math.min(1.8f, scale));
    }

    /** Returns raw (non-mirrored) [sumX/n, sumY/n] center of a hand. */
    private float[] getHandCenter(List<NormalizedLandmark> landmarks, float scaleX, float scaleY) {
        float sumX = 0f, sumY = 0f;
        for (NormalizedLandmark lm : landmarks) {
            sumX += lm.x() * imageWidth * scaleX;
            sumY += lm.y() * imageHeight * scaleY;
        }
        return new float[]{sumX / landmarks.size(), sumY / landmarks.size()};
    }

    /** Returns the minimum (highest on screen) Y pixel value for a hand. */
    private float getHandMinY(List<NormalizedLandmark> landmarks, float scaleY) {
        float minY = Float.MAX_VALUE;
        for (NormalizedLandmark lm : landmarks) {
            float y = lm.y() * imageHeight * scaleY;
            if (y < minY) minY = y;
        }
        return minY;
    }

    /** Mirror X for front-camera display. */
    private float mirrorX(float x) {
        return getWidth() - x;
    }
}
