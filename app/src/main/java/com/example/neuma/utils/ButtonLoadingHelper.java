package com.example.neuma.utils;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.Button;
import androidx.core.content.ContextCompat;

public class ButtonLoadingHelper {

    private final Button button;
    private final String defaultText;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int currentFrame = 0;
    private boolean isAnimating = false;
    private final AnimationDrawable loadingDrawable;

    private final Runnable animateRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAnimating && loadingDrawable != null) {
                int numFrames = loadingDrawable.getNumberOfFrames();
                if (numFrames > 0) {
                    int frameIndex = currentFrame % numFrames;
                    Drawable frame = loadingDrawable.getFrame(frameIndex);
                    
                    // Supaya ukurannya pas (bisa disesuaikan, misalnya 48x48)
                    int size = (int) (24 * button.getContext().getResources().getDisplayMetrics().density);
                    frame.setBounds(0, 0, size, size);
                    
                    ImageSpan span = new ImageSpan(frame, ImageSpan.ALIGN_CENTER);
                    SpannableString spannableString = new SpannableString(" ");
                    spannableString.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    
                    button.setText(spannableString);
                    
                    int duration = loadingDrawable.getDuration(frameIndex);
                    currentFrame++;
                    handler.postDelayed(this, duration);
                }
            }
        }
    };

    public ButtonLoadingHelper(Button button) {
        this.button = button;
        this.defaultText = button.getText().toString();
        // Load animasi yang sudah ada
        Drawable d = ContextCompat.getDrawable(button.getContext(), com.example.neuma.R.drawable.anim_loading_moods);
        if (d instanceof AnimationDrawable) {
            this.loadingDrawable = (AnimationDrawable) d;
        } else {
            this.loadingDrawable = null;
        }
    }

    public void startLoading() {
        if (!isAnimating) {
            isAnimating = true;
            button.setEnabled(false);
            currentFrame = 0;
            handler.post(animateRunnable);
        }
    }

    public void stopLoading() {
        if (isAnimating) {
            isAnimating = false;
            handler.removeCallbacks(animateRunnable);
            button.setText(defaultText);
            button.setEnabled(true);
        }
    }
}
