package com.example.neuma;

import java.util.ArrayList;
import java.util.List;

public class FeatureExtractor {

    public static float[] normalizeLandmarks(float[] coords) {
        boolean allZero = true;
        for (float v : coords) {
            if (v != 0.0f) {
                allZero = false;
                break;
            }
        }
        if (allZero) return coords;

        float wristX = coords[0];
        float wristY = coords[1];
        float wristZ = coords[2];

        float[] normalized = new float[coords.length];
        for (int i = 0; i < coords.length; i += 3) {
            normalized[i]   = coords[i]   - wristX;
            normalized[i+1] = coords[i+1] - wristY;
            normalized[i+2] = coords[i+2] - wristZ;
        }

        float maxVal = 0f;
        for (float v : normalized) {
            maxVal = Math.max(maxVal, Math.abs(v));
        }

        if (maxVal > 0) {
            for (int i = 0; i < normalized.length; i++) {
                normalized[i] = normalized[i] / maxVal;
            }
        }

        return normalized;
    }

    public static float[] computeDerivedFeatures(float[] coords) {
        boolean allZero = true;
        for (float v : coords) {
            if (v != 0.0f) {
                allZero = false;
                break;
            }
        }
        if (allZero) return new float[15];

        List<float[]> points = new ArrayList<>();
        for (int i = 0; i < coords.length; i += 3) {
            points.add(new float[]{coords[i], coords[i+1], coords[i+2]});
        }

        int[] tipIndices = {4, 8, 12, 16, 20};
        float[][] fingertips = new float[5][];
        for (int i = 0; i < 5; i++) {
            fingertips[i] = points.get(tipIndices[i]);
        }
        float[] wrist = points.get(0);

        List<Float> features = new ArrayList<>();

        for (float[] tip : fingertips) {
            features.add(distance(tip, wrist));
        }

        for (int i = 0; i < fingertips.length - 1; i++) {
            features.add(distance(fingertips[i], fingertips[i+1]));
        }

        for (int i = 1; i < fingertips.length; i++) {
            features.add(distance(fingertips[0], fingertips[i]));
        }

        features.add(distance(fingertips[2], fingertips[4]));
        features.add(distance(fingertips[1], fingertips[4]));

        float[] result = new float[features.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = features.get(i);
        }
        return result;
    }

    private static float distance(float[] a, float[] b) {
        float sum = 0f;
        for (int i = 0; i < 3; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return (float) Math.sqrt(sum);
    }

    public static float[] extractFullFeatures(float[] leftHand, float[] rightHand) {
        float[] leftNorm = normalizeLandmarks(leftHand);
        float[] leftDerived = computeDerivedFeatures(leftHand);
        float[] rightNorm = normalizeLandmarks(rightHand);
        float[] rightDerived = computeDerivedFeatures(rightHand);

        float[] result = new float[156];
        int pos = 0;

        System.arraycopy(leftNorm, 0, result, pos, leftNorm.length); pos += leftNorm.length;
        System.arraycopy(leftDerived, 0, result, pos, leftDerived.length); pos += leftDerived.length;
        System.arraycopy(rightNorm, 0, result, pos, rightNorm.length); pos += rightNorm.length;
        System.arraycopy(rightDerived, 0, result, pos, rightDerived.length); pos += rightDerived.length;

        return result;
    }
}
