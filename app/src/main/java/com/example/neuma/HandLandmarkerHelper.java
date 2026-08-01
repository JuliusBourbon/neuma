package com.example.neuma;

import android.content.Context;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
import com.google.mediapipe.framework.image.MPImage;

public class HandLandmarkerHelper {

    public interface LandmarkerListener {
        void onResults(HandLandmarkerResult result);
        void onError(String error);
    }

    private HandLandmarker handLandmarker;
    private final LandmarkerListener listener;

    public HandLandmarkerHelper(Context context, LandmarkerListener listener) {
        this.listener = listener;
        setupHandLandmarker(context);
    }

    private void setupHandLandmarker(Context context) {
        BaseOptions baseOptions = BaseOptions.builder()
                .setModelAssetPath("hand_landmarker.task")
                .build();

        HandLandmarker.HandLandmarkerOptions options =
                HandLandmarker.HandLandmarkerOptions.builder()
                        .setBaseOptions(baseOptions)
                        .setRunningMode(RunningMode.LIVE_STREAM)
                        .setNumHands(2)
                        .setMinHandDetectionConfidence(0.5f)
                        .setMinTrackingConfidence(0.5f)
                        .setMinHandPresenceConfidence(0.5f)
                        .setResultListener(this::onResult)
                        .setErrorListener(this::onError)
                        .build();

        handLandmarker = HandLandmarker.createFromOptions(context, options);
    }

    public void detectAsync(MPImage mpImage, long timestampMs) {
        if (handLandmarker != null) {
            handLandmarker.detectAsync(mpImage, timestampMs);
        }
    }

    public void close() {
        if (handLandmarker != null) {
            handLandmarker.close();
            handLandmarker = null;
        }
    }

    private void onResult(HandLandmarkerResult result, MPImage inputImage) {
        listener.onResults(result);
    }

    private void onError(RuntimeException error) {
        listener.onError(error.getMessage());
    }
}
