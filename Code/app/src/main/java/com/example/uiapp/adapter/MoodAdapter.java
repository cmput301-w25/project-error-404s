package com.example.uiapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.utils.OnItemEntryClick;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MoodViewHolder> {
    private String from;
    private final Context context;
    private List<MoodEntry> moodList = new ArrayList<>();
    private final OnItemDeleteClickListener onItemDeleteClickListener;
    private final OnItemEditClickListener onItemEditClickListener;
    private final OnItemEntryClick onItemEntryClick;

    public MoodAdapter(String from, Context context, List<MoodEntry> moodList, OnItemDeleteClickListener onItemDeleteClickListener, OnItemEditClickListener onItemEditClickListener, OnItemEntryClick onItemEntryClick) {
        this.from = from;
        this.context = context;
        this.moodList = moodList;
        this.onItemDeleteClickListener = onItemDeleteClickListener;
        this.onItemEditClickListener = onItemEditClickListener;
        this.onItemEntryClick = onItemEntryClick;
    }

    public MoodAdapter(Context context, List<MoodEntry> moodList) {
        this.context = context;
        this.moodList = moodList;
        this.onItemDeleteClickListener = null;
        this.onItemEditClickListener = null;
        this.onItemEntryClick = null;
    }
    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MoodEntry entry = moodList.get(position);

        holder.txtDateTime.setText(entry.getDateTime());
        holder.txtMood.setText(entry.getMood());
        holder.txtNote.setText(entry.getNote());
        holder.txtPeople.setText(entry.getPeople());
        holder.txtLocation.setText(entry.getLocation());
        holder.imgMood.setImageResource(entry.getMoodIcon());
        if (entry.getNote().isEmpty()){
            holder.llNote.setVisibility(View.GONE);
        }else{
            holder.llNote.setVisibility(View.VISIBLE);
        }
        if (entry.getPeople().isEmpty()){
            holder.llPeople.setVisibility(View.GONE);
        }else{
            holder.llPeople.setVisibility(View.VISIBLE);
        }
        if (entry.getLocation().isEmpty()){
            holder.llLocation.setVisibility(View.GONE);
        }else{
            holder.llLocation.setVisibility(View.VISIBLE);
        }

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

        if (!from.equals("home")) {
            if (HelperClass.users.getUsername().equals(entry.getUsername())){
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        if (!entry.getImageUrl().isEmpty()) {
            holder.centerImg.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(entry.getImageUrl())
                    .centerCrop()
                    .into(holder.imgMoodEntry);
        } else {
            holder.centerImg.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemEntryClick.onItemClick(position);
            }
        });


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
        LinearLayout llNote, llPeople, llLocation;

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
            llNote = itemView.findViewById(R.id.llNote);
            llPeople = itemView.findViewById(R.id.llPeople);
            llLocation = itemView.findViewById(R.id.llLocation);
        }
    }
}
