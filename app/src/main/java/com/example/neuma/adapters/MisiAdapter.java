package com.example.neuma.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.neuma.R;
import com.example.neuma.models.Achievement;
import java.util.List;

public class MisiAdapter extends RecyclerView.Adapter<MisiAdapter.MisiViewHolder> {

    private List<Achievement> misiList;

    public MisiAdapter(List<Achievement> misiList) {
        this.misiList = misiList;
    }

    @NonNull
    @Override
    public MisiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_misi, parent, false);
        return new MisiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MisiViewHolder holder, int position) {
        Achievement misi = misiList.get(position);

        holder.tvTitle.setText(misi.getTitle());
        holder.pbMisi.setMax(misi.getTarget() > 0 ? misi.getTarget() : 1);
        holder.pbMisi.setProgress(misi.getProgress());
        holder.tvProgressText.setText(misi.getProgress() + "/" + misi.getTarget());

        // Mengatur status gambar Chest (terbuka/aktif vs terkunci)
        if (misi.isUnlocked()) {
            holder.ivChest.setImageResource(R.drawable.ic_chest_active); // Aset chest aktif
            holder.ivChest.setAlpha(1.0f);
        } else {
            holder.ivChest.setImageResource(R.drawable.ic_chest_locked); // Aset chest terkunci
            holder.ivChest.setAlpha(0.5f);
        }

        // Tampilkan reward ID atau "No Reward"
        if (misi.getRewardAvatarId() != null && !misi.getRewardAvatarId().isEmpty()) {
            holder.tvRewardId.setText("Avatar: " + misi.getRewardAvatarSeed());
            holder.tvRewardId.setVisibility(View.VISIBLE);
        } else {
            holder.tvRewardId.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return misiList != null ? misiList.size() : 0;
    }

    static class MisiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvProgressText, tvRewardId;
        ProgressBar pbMisi;
        ImageView ivChest;

        public MisiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_misi_title);
            tvProgressText = itemView.findViewById(R.id.tv_misi_progress_text);
            tvRewardId = itemView.findViewById(R.id.tv_reward_id);
            pbMisi = itemView.findViewById(R.id.pb_misi);
            ivChest = itemView.findViewById(R.id.iv_chest);
        }
    }
}
