package com.example.uiapp.adapter;

import static com.example.uiapp.MainActivity.createInitialsBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.model.EmojiHelper;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.model.UserModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FollowingMoodAdapter extends RecyclerView.Adapter<FollowingMoodAdapter.MoodViewHolder> {
    private final Context context;
    private List<MoodEntry> moodList = new ArrayList<>();
    private OnItemEditClickListener onItemEditClickListener;

    public FollowingMoodAdapter(Context context, List<MoodEntry> moodList, OnItemEditClickListener onItemEditClickListener) {
        this.context = context;
        this.moodList = moodList;
        this.onItemEditClickListener = onItemEditClickListener;
    }

    public List<MoodEntry> getMoodList() {
        return moodList;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mood_following_item, parent, false);
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
        holder.txtUsername.setText(entry.getUsername());

        Bitmap initialsBitmap = createInitialsBitmap(entry.getUsername(), 200);
        holder.imgUser.setImageBitmap(initialsBitmap);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(entry.getUsername())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        if (user != null) {
                            if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
                                Glide.with(context)
                                        .load(user.getUserImage())
                                        .centerCrop()
                                        .into(holder.imgUser);
                            }
                        }
                    }
                });

        holder.imgMood.setImageResource(entry.getMoodIcon());
        if (entry.getNote().isEmpty()) {
            holder.llNote.setVisibility(View.GONE);
        } else {
            holder.llNote.setVisibility(View.VISIBLE);
        }
        if (entry.getPeople().isEmpty()) {
            holder.llPeople.setVisibility(View.GONE);
        } else {
            holder.llPeople.setVisibility(View.VISIBLE);
        }
        if (entry.getLocation().isEmpty()) {
            holder.llLocation.setVisibility(View.GONE);
        } else {
            holder.llLocation.setVisibility(View.VISIBLE);
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
                onItemEditClickListener.onClickEdit(position);
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

    /**
     * This activity holds various parameter input information for filling out the mood.
     * Holds time and date of the mood creation, mood, note, people info, location and
     * the user that is writing the mood
     */
    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView txtDateTime, txtMood, txtNote, txtPeople, txtLocation, txtUsername;
        ImageView imgMood, imgMoodEntry, imgUser;
        MaterialCardView centerImg;
        LinearLayout llNote, llPeople, llLocation;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateTime = itemView.findViewById(R.id.txtDateTime);
            txtMood = itemView.findViewById(R.id.txtMood);
            txtNote = itemView.findViewById(R.id.txtNote);
            txtPeople = itemView.findViewById(R.id.txtPeople);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            imgMood = itemView.findViewById(R.id.imgMood);
            imgMoodEntry = itemView.findViewById(R.id.imgMoodEntry);
            imgUser = itemView.findViewById(R.id.imgUser);
            centerImg = itemView.findViewById(R.id.centerImg);
            llNote = itemView.findViewById(R.id.llNote);
            llPeople = itemView.findViewById(R.id.llPeople);
            llLocation = itemView.findViewById(R.id.llLocation);
        }
    }
}
