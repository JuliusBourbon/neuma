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
import com.example.neuma.models.Misi;
import java.util.List;

public class MisiAdapter extends RecyclerView.Adapter<MisiAdapter.MisiViewHolder> {

    private List<Misi> misiList;

    public MisiAdapter(List<Misi> misiList) {
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
        Misi misi = misiList.get(position);

        holder.tvTitle.setText(misi.getTitle());
        holder.pbMisi.setMax(misi.getMaxProgress());
        holder.pbMisi.setProgress(misi.getCurrentProgress());
        holder.tvProgressText.setText(misi.getCurrentProgress() + "/" + misi.getMaxProgress());

        // Mengatur status gambar Chest (terbuka/aktif vs terkunci)
        if (misi.isCompleted()) {
            holder.ivChest.setImageResource(R.drawable.ic_chest_active); // Aset chest aktif
        } else {
            holder.ivChest.setImageResource(R.drawable.ic_chest_locked); // Aset chest terkunci
        }
    }

    @Override
    public int getItemCount() {
        return misiList != null ? misiList.size() : 0;
    }

    static class MisiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvProgressText;
        ProgressBar pbMisi;
        ImageView ivChest;

        public MisiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_misi_title);
            tvProgressText = itemView.findViewById(R.id.tv_misi_progress_text);
            pbMisi = itemView.findViewById(R.id.pb_misi);
            ivChest = itemView.findViewById(R.id.iv_chest);
        }
    }
}
