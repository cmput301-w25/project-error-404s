package com.example.uiapp.adapter;

import android.content.Context;
<<<<<<< HEAD
=======
import android.net.Uri;
>>>>>>> parent of 1424aef (Aakarsh_Reorganize)
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.model.MoodEntry;
<<<<<<< HEAD
import com.example.uiapp.utils.OnItemEntryClick;
=======
>>>>>>> parent of 1424aef (Aakarsh_Reorganize)
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MoodViewHolder> {


    private final Context context;
    private List<MoodEntry> moodList = new ArrayList<>();
    private final OnItemDeleteClickListener onItemDeleteClickListener;
    private final OnItemEditClickListener onItemEditClickListener;

    public MoodAdapter(Context context, List<MoodEntry> moodList, OnItemDeleteClickListener onItemDeleteClickListener, OnItemEditClickListener onItemEditClickListener) {
        this.context = context;
        this.moodList = moodList;
        this.onItemDeleteClickListener = onItemDeleteClickListener;
        this.onItemEditClickListener = onItemEditClickListener;
    }
    public MoodAdapter(Context context, List<MoodEntry> moodList) {
        this.context = context;
        this.moodList = moodList;
        this.onItemDeleteClickListener = null;
        this.onItemEditClickListener = null;
    }
    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        MoodEntry entry = moodList.get(position);

        holder.txtDateTime.setText(entry.getDateTime());
        holder.txtMood.setText(entry.getMood());
        holder.txtNote.setText( entry.getNote());
        holder.txtPeople.setText(entry.getPeople());
        holder.txtLocation.setText(entry.getLocation());
        holder.imgMood.setImageResource(entry.getMoodIcon());

        // Check if delete listener is null to prevent NullPointerException
        if (onItemDeleteClickListener != null) {
            holder.btnDelete.setOnClickListener(v -> onItemDeleteClickListener.onClickDelete(position));
        } else {
            holder.btnDelete.setOnClickListener(null);
        }
        
        // Check if edit listener is null to prevent NullPointerException
        if (onItemEditClickListener != null) {
            holder.btnEdit.setOnClickListener(v -> onItemEditClickListener.onClickEdit(position));
        } else {
            holder.btnEdit.setOnClickListener(null);
        }

        if (entry.getIsHome() == null) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        if (!entry.getImageUrl().isEmpty()) {
            holder.centerImg.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(Uri.parse(entry.getImageUrl()))
                    .centerCrop()
                    .into(holder.imgMoodEntry);
        } else {
            holder.centerImg.setVisibility(View.GONE);
        }


    }

    public void updateList(List<MoodEntry> newList) {
        moodList.clear();
        moodList.addAll(newList);
        notifyDataSetChanged(); // Refresh the entire list
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView txtDateTime, txtMood, txtNote, txtPeople, txtLocation;
        ImageView imgMood, imgMoodEntry;
        MaterialCardView centerImg;
        ImageButton btnDelete, btnEdit;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtMood = itemView.findViewById(R.id.txtMood);
            txtNote = itemView.findViewById(R.id.txtNote);
            txtPeople = itemView.findViewById(R.id.txtPeople);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            imgMood = itemView.findViewById(R.id.imgMood);
            imgMoodEntry = itemView.findViewById(R.id.imgMoodEntry);
            centerImg = itemView.findViewById(R.id.centerImg);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
