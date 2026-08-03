package com.example.neuma.adapters;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
        holder.tvDescription.setText(misi.getDescription() != null ? misi.getDescription() : "");
        holder.pbMisi.setMax(misi.getTarget() > 0 ? misi.getTarget() : 1);
        holder.pbMisi.setProgress(misi.getProgress());
        holder.tvProgressText.setText(misi.getProgress() + "/" + misi.getTarget());

        // Tampilkan reward avatar dari Dicebear
        if (misi.getRewardAvatarId() != null && !misi.getRewardAvatarId().isEmpty()) {
            String style = misi.getRewardAvatarStyle() != null ? misi.getRewardAvatarStyle() : "adventurer";
            String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + misi.getRewardAvatarSeed();

            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(avatarUrl)
                .into(holder.ivChest);
        } else {
            // Default chest kalau gak ada reward avatar
            if (misi.isUnlocked()) {
                holder.ivChest.setImageResource(R.drawable.ic_chest_active);
            } else {
                holder.ivChest.setImageResource(R.drawable.ic_chest_locked);
            }
        }

        // Terapkan grayscale untuk avatar yang belum terbuka,
        // tampilan warna penuh untuk yang sudah terbuka
        if (misi.isUnlocked()) {
            holder.ivChest.setAlpha(1.0f);
            holder.ivChest.clearColorFilter();
        } else {
            holder.ivChest.setAlpha(0.85f);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0f); // 0 = grayscale penuh
            holder.ivChest.setColorFilter(new ColorMatrixColorFilter(matrix));
        }
    }

    @Override
    public int getItemCount() {
        return misiList != null ? misiList.size() : 0;
    }

    static class MisiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvProgressText;
        ProgressBar pbMisi;
        ImageView ivChest;

        public MisiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_misi_title);
            tvDescription = itemView.findViewById(R.id.tv_misi_description);
            tvProgressText = itemView.findViewById(R.id.tv_misi_progress_text);
            pbMisi = itemView.findViewById(R.id.pb_misi);
            ivChest = itemView.findViewById(R.id.iv_chest);
        }
    }
}
