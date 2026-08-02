package com.example.neuma.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.neuma.R;
import com.example.neuma.models.Level;
import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {
    private List<Level> levelList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Level level);
    }

    public LevelAdapter(List<Level> levelList, OnItemClickListener listener) {
        this.levelList = levelList;
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
        Level currentLevel = levelList.get(position);

        // Pola Zigzag
        if (position % 2 == 0) {
            holder.itemView.setTranslationX(50f);
        } else {
            holder.itemView.setTranslationX(-50f);
        }

        // Cek status level dari API/Model
        boolean isUnlocked = "ACTIVE".equalsIgnoreCase(currentLevel.getStatus())
                || "UNLOCKED".equalsIgnoreCase(currentLevel.getStatus())
                || "COMPLETED".equalsIgnoreCase(currentLevel.getStatus());

        // Bind huruf, warna, dan status tombol
        bindLevelNode(holder.tvLevelLetter, holder.tvBadgeStart, position, isUnlocked, position == 0);

        View.OnClickListener clickAction = v -> {
            if (listener != null && isUnlocked) {
                listener.onItemClick(currentLevel);
            }
        };

        holder.tvLevelLetter.setOnClickListener(clickAction);
        holder.itemView.setOnClickListener(clickAction);
    }

    private String getLevelLetter(int position) {
        return String.valueOf((char) ('A' + (position % 26)));
    }

    private void bindLevelNode(
            TextView tvLetter,
            View tvBadge,
            int position,
            boolean isUnlocked,
            boolean isActive
    ) {
        if (tvLetter != null) {
            tvLetter.setText(getLevelLetter(position));

            if (isUnlocked) {
                // Pasang selector hijau 3D yang bisa ditekan
                tvLetter.setBackground(ContextCompat.getDrawable(tvLetter.getContext(), R.drawable.bg_level_active));
                tvLetter.setTextColor(Color.WHITE);
                tvLetter.setClickable(true);
                tvLetter.setFocusable(true);

                if (tvBadge != null) {
                    tvBadge.setVisibility(isActive ? View.VISIBLE : View.GONE);
                }
            } else {
                // Level Terkunci
                tvLetter.setBackground(ContextCompat.getDrawable(tvLetter.getContext(), R.drawable.bg_level_locked));
                tvLetter.setTextColor(Color.parseColor("#9E9E9E"));
                tvLetter.setClickable(false);
                tvLetter.setFocusable(false);

                if (tvBadge != null) {
                    tvBadge.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return levelList != null ? levelList.size() : 0;
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        TextView tvLevelLetter;
        View tvBadgeStart;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLevelLetter = itemView.findViewById(R.id.ivLevelIcon);
            tvBadgeStart = itemView.findViewById(R.id.ivTooltipStart);
        }
    }
}
