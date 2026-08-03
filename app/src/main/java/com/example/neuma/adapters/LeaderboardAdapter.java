package com.example.neuma.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neuma.R;
import com.example.neuma.models.LeaderboardEntry;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<LeaderboardEntry> entries;

    public LeaderboardAdapter(List<LeaderboardEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardEntry entry = entries.get(position);

        // Peringkat dimulai dari angka 4
        holder.tvRank.setText(String.valueOf(position + 4));
        holder.tvName.setText(entry.getName());
        
        if (entry.getName().equals("-")) {
            holder.tvScore.setText("-");
            holder.ivAvatar.setImageDrawable(null);
        } else {
            holder.tvScore.setText(String.valueOf(entry.getScore()));
            String style = entry.getAvatarStyle() != null ? entry.getAvatarStyle() : "adventurer";
            String seed = entry.getAvatarSeed() != null ? entry.getAvatarSeed() : "Felix";
            String url = "https://api.dicebear.com/10.x/" + style + "/png?seed=" + seed;
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(url)
                .into(holder.ivAvatar);

            holder.ivAvatar.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(holder.itemView.getContext(), com.example.neuma.OtherProfileActivity.class);
                intent.putExtra("USER_ID", entry.getUserId());
                holder.itemView.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvScore;
        de.hdodenhof.circleimageview.CircleImageView ivAvatar;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvName = itemView.findViewById(R.id.tv_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}
