package com.example.neuma;

import android.content.Context;
import ai.onnxruntime.*;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Map;

public class OnnxHelper {

    private OrtEnvironment env;
    private OrtSession session;
    private String[] labels;
    public String lastError = null;

    public OnnxHelper(Context context) {
        try {
            env = OrtEnvironment.getEnvironment();

            byte[] modelBytes = readAssetBytes(context, "bisindo_model.onnx");
            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            session = env.createSession(modelBytes, options);

            labels = loadLabels(context, "labels.json");

        } catch (Exception e) {
            e.printStackTrace();
            lastError = "Init: " + e.getMessage();
        }
    }

    private byte[] readAssetBytes(Context context, String filename) throws Exception {
        InputStream is = context.getAssets().open(filename);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        is.close();
        return buffer.toByteArray();
    }

    private String[] loadLabels(Context context, String filename) throws Exception {
        InputStream is = context.getAssets().open(filename);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        is.close();

        String jsonString = buffer.toString("UTF-8");
        jsonString = jsonString.trim();
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        String[] rawLabels = jsonString.split(",");
        String[] cleanLabels = new String[rawLabels.length];
        for (int i = 0; i < rawLabels.length; i++) {
            cleanLabels[i] = rawLabels[i].replace("\"", "").trim();
        }
        return cleanLabels;
    }

    public static class PredictionResult {
        public String label;
        public float confidence;

        public PredictionResult(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }
    }

    public PredictionResult predict(float[] features156) {
        if (session == null || env == null) return new PredictionResult(lastError != null ? lastError : "Session/Env Null", 0f);
        try {
            long[] shape = {1, 156};
            FloatBuffer buffer = FloatBuffer.wrap(features156);

            OnnxTensor inputTensor = OnnxTensor.createTensor(env, buffer, shape);

            Map<String, OnnxTensor> inputs = Collections.singletonMap(
                    session.getInputNames().iterator().next(), inputTensor
            );

            OrtSession.Result result = session.run(inputs);

            long[] predictedIndices = (long[]) result.get(0).getValue();
            int predictedIndex = (int) predictedIndices[0];

            float[][] probabilities = (float[][]) result.get(1).getValue();
            float confidence = probabilities[0][predictedIndex];

            String label = (labels != null && predictedIndex < labels.length)
                    ? labels[predictedIndex] : String.valueOf(predictedIndex);

            inputTensor.close();
            result.close();

            return new PredictionResult(label, confidence * 100f);

        } catch (Exception e) {
            e.printStackTrace();
            return new PredictionResult("Err: " + e.getMessage(), 0f);
        }
    }

    public void close() {
        try {
            if (session != null) session.close();
            if (env != null) env.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
