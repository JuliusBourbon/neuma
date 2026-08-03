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
        holder.tvDescription.setText(a.getDescription());

        if (a.getRewardAvatarId() != null && !a.getRewardAvatarId().isEmpty()) {
            String style = a.getRewardAvatarStyle() != null ? a.getRewardAvatarStyle() : "adventurer";
            String avatarUrl = "https://api.dicebear.com/10.x/" + style + "/png?seed=" + a.getRewardAvatarSeed();
            Glide.with(holder.itemView.getContext())
                .load(avatarUrl)
                .into(holder.ivBadge);
        } else {
            if (a.isUnlocked()) {
                // If there's no avatar reward, you can just leave it as is or set a default unlocked badge
                // holder.ivBadge.setImageResource(R.drawable.ic_badge_active);
            } else {
                holder.ivBadge.setImageResource(R.drawable.ic_badge_locked);
            }
        }

        if (a.isUnlocked()) {
            holder.ivBadge.setAlpha(1.0f);
        } else {
            holder.ivBadge.setAlpha(0.35f);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBadge;
        TextView tvTitle, tvDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBadge = itemView.findViewById(R.id.ivBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
