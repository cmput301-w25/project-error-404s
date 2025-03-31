package com.example.uiapp.adapter;

import static com.example.uiapp.MainActivity.createInitialsBitmap;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.utils.HelperClass;
import com.example.uiapp.utils.OnItemNotificationClick;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.UserViewHolder> {
    private final Context context;
    private List<String> requestList;
    OnItemNotificationClick onItemNotificationClick;

    public NotificationAdapter(Context context, List<String> requestList, OnItemNotificationClick onItemNotificationClick) {
        this.context = context;
        this.requestList = requestList;
        this.onItemNotificationClick = onItemNotificationClick;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String username = requestList.get(position);
        holder.textUsername.setText(username);

        Bitmap initialsBitmap = createInitialsBitmap(username, 200);
        holder.imgUser.setImageBitmap(initialsBitmap);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(username)
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

        holder.btnAccept.setOnClickListener(v -> {
            String followerUsername = requestList.get(position);
            onItemNotificationClick.onItemClick("accept", followerUsername);
        });

        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String followerUsername = requestList.get(position);
                onItemNotificationClick.onItemClick("decline", followerUsername);
            }
        });
    }

    public void updateList(List<String> newList) {
        requestList.clear();
        requestList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername;
        ImageView imgUser;
        Button btnAccept, btnDecline;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            imgUser = itemView.findViewById(R.id.imgUser);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
        }
    }

}
