package com.example.neuma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.neuma.models.Achievement;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private List<Achievement> list;

    public AchievementAdapter(List<Achievement> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement a = list.get(position);
        holder.tvTitle.setText(a.getTitle());
        holder.tvDesc.setText(a.getDescription());

        if (a.isUnlocked()) {
            holder.tvStatus.setText("TERBUKA");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.primary));
            holder.ivAvatar.setAlpha(1.0f);
            
            if (a.getRewardAvatarSeed() != null && a.getRewardAvatarStyle() != null) {
                String url = "https://api.dicebear.com/9.x/" + a.getRewardAvatarStyle() + "/png?seed=" + a.getRewardAvatarSeed();
                Glide.with(holder.itemView.getContext()).load(url).into(holder.ivAvatar);
            }
        } else {
            holder.tvStatus.setText("TERKUNCI");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.text_hint));
            holder.ivAvatar.setAlpha(0.3f);
            holder.ivAvatar.setImageResource(R.drawable.ic_launcher_foreground); // Placeholder
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvTitle, tvDesc, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_achievement_avatar);
            tvTitle = itemView.findViewById(R.id.tv_achievement_title);
            tvDesc = itemView.findViewById(R.id.tv_achievement_desc);
            tvStatus = itemView.findViewById(R.id.tv_achievement_status);
        }
    }
}
