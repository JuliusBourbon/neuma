package com.example.neuma.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.R;
import com.example.neuma.models.Level;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private List<Level> levels;
    private OnLevelClickListener listener;

    public interface OnLevelClickListener {
        void onLevelClick(Level level);
    }

    public LevelAdapter(List<Level> levels, OnLevelClickListener listener) {
        this.levels = levels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        Level level = levels.get(position);
        holder.tvLetter.setText(level.getLetter());

        if ("LOCKED".equalsIgnoreCase(level.getStatus())) {
            holder.circleContainer.setBackgroundResource(R.drawable.circle_bg_disabled);
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(null);
            holder.itemView.setAlpha(0.6f);
        } else {
            holder.circleContainer.setBackgroundResource(R.drawable.circle_bg_primary);
            holder.ivLock.setVisibility(View.GONE);
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLevelClick(level);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return levels != null ? levels.size() : 0;
    }

    static class LevelViewHolder extends RecyclerView.ViewHolder {
        FrameLayout circleContainer;
        TextView tvLetter;
        ImageView ivLock;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            circleContainer = itemView.findViewById(R.id.circle_container);
            tvLetter = itemView.findViewById(R.id.tv_letter);
            ivLock = itemView.findViewById(R.id.iv_lock);
        }
    }
}
