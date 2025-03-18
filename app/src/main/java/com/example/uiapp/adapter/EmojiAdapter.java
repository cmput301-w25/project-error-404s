package com.example.uiapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiapp.R;
import com.example.uiapp.databinding.EmojiLayoutBinding;
import com.example.uiapp.model.EmojiModel;

import java.util.List;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {
    private List<EmojiModel> emojiList;
    private EmojiLayoutBinding binding;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnEmojiClickListener onEmojiClickListener;


    public EmojiAdapter(List<EmojiModel> emojiList, OnEmojiClickListener onEmojiClickListener) {
        this.emojiList = emojiList;
        this.onEmojiClickListener = onEmojiClickListener;
    }

    @NonNull
    @Override
    public EmojiAdapter.EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = EmojiLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EmojiViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiAdapter.EmojiViewHolder holder, int position) {
        EmojiModel emojiModel = emojiList.get(position);
        holder.binding.tvEmoji.setText(emojiModel.getName());


        if (position == selectedPosition) {
            holder.binding.cardEmoji.setCardBackgroundColor(emojiModel.getColor());

            holder.binding.ivEmoji.setImageResource(emojiModel.getEmojiPath());
        } else {
            holder.binding.cardEmoji.setCardBackgroundColor(Color.WHITE); // Default color
//            holder.binding.tvEmoji.setText("Default"); // Reset text
            holder. binding.ivEmoji.setImageResource(R.drawable._emoji__smiling_face_with_smiling_eyes_);
        }

        holder.binding.cardEmoji.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            onEmojiClickListener.onEmojiClick(selectedPosition);

            notifyItemChanged(previousPosition); // Reset old selection
            notifyItemChanged(selectedPosition); // Update new selection
        });


    }

    @Override
    public int getItemCount() {
        return emojiList.size();
    }

    public static class EmojiViewHolder extends RecyclerView.ViewHolder {
        EmojiLayoutBinding binding;

        public EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = EmojiLayoutBinding.bind(itemView);
        }
    }
}
