package com.example.neuma.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.neuma.R;
import com.example.neuma.models.AvatarItem;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarSelectionAdapter extends RecyclerView.Adapter<AvatarSelectionAdapter.ViewHolder> {

    private List<AvatarItem> avatars;
    private OnAvatarSelectedListener listener;
    private String currentSeed;

    public interface OnAvatarSelectedListener {
        void onAvatarSelected(AvatarItem avatar);
    }

    public AvatarSelectionAdapter(List<AvatarItem> avatars, String currentSeed, OnAvatarSelectedListener listener) {
        this.avatars = avatars;
        this.currentSeed = currentSeed;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_avatar_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AvatarItem item = avatars.get(position);
        holder.tvName.setText(item.getName());

        String style = item.getStyle() != null ? item.getStyle() : "adventurer";
        String seed = item.getSeed() != null ? item.getSeed() : "Felix";
        String avatarUrl = "https://api.dicebear.com/9.x/" + style + "/png?seed=" + seed;

        Glide.with(holder.itemView.getContext())
             .load(avatarUrl)
             .into(holder.ivAvatar);

        if (seed.equals(currentSeed)) {
            holder.ivAvatar.setBorderColor(Color.parseColor("#2EC4B6"));
            holder.ivAvatar.setBorderWidth(6);
        } else {
            holder.ivAvatar.setBorderColor(Color.TRANSPARENT);
            holder.ivAvatar.setBorderWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAvatarSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatars.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAvatar;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar_item);
            tvName = itemView.findViewById(R.id.tv_avatar_name);
        }
    }
}
