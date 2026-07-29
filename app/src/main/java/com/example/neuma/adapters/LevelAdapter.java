package com.example.neuma.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.R;
import com.example.neuma.models.Level;

import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    private List<Level> levelList;
    private OnItemClickListener listener; // Menambahkan variabel untuk listener

    // Membuat Interface untuk menangkap aksi klik
    public interface OnItemClickListener {
        void onItemClick(Level level);
    }

    // Constructor sekarang menerima 2 parameter (sesuai dengan yang dikirim AdminActivity)
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
        boolean isTrophyLevel = (position == 4);

        if (position % 2 == 0) {
            // Jika urutan genap geser sedikit ke kanan
            holder.itemView.setTranslationX(60f);
        } else {
            // Jika urutan ganjil geser sedikit ke kiri
            holder.itemView.setTranslationX(-60f);
        }

        // Tooltip START HANYA muncul di item pertama (position 0)
        if (position == 0) {
            holder.ivTooltipStart.setVisibility(View.VISIBLE);
        } else {
            holder.ivTooltipStart.setVisibility(View.GONE);
        }

        boolean isActive = "ACTIVE".equalsIgnoreCase(currentLevel.getStatus())
                || "UNLOCKED".equalsIgnoreCase(currentLevel.getStatus())
                || "COMPLETED".equalsIgnoreCase(currentLevel.getStatus());

        // Pengaturan Icon Level / Trophy
        if (isActive) {
            if (isTrophyLevel) {
                holder.ivLevelIcon.setImageResource(R.drawable.ic_trophy_active);
            } else {
                holder.ivLevelIcon.setImageResource(R.drawable.ic_level_active);
            }
        } else {
            if (isTrophyLevel) {
                holder.ivLevelIcon.setImageResource(R.drawable.ic_trophy_locked);
            } else {
                holder.ivLevelIcon.setImageResource(R.drawable.ic_level_locked);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentLevel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return levelList.size();
    }

    public static class LevelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLevelIcon, ivTooltipStart;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLevelIcon = itemView.findViewById(R.id.ivLevelIcon);
            ivTooltipStart = itemView.findViewById(R.id.ivTooltipStart);
        }
    }
}
