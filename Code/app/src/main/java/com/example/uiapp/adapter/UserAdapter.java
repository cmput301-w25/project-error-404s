package com.example.uiapp.adapter;

import static com.example.uiapp.MainActivity.createInitialsBitmap;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.utils.HelperClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final Context context;
    private List<UserModel> userList;
    ProgressDialog progressDialog;
    private String currentUserName;

    public UserAdapter(Context context, List<UserModel> userList, ProgressDialog progressDialog) {
        this.context = context;
        this.userList = userList;
        this.progressDialog = progressDialog;
        this.currentUserName = HelperClass.users.getUsername();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserModel user = userList.get(position);
        holder.textUsername.setText(user.getUsername());

        if (!user.getUserImage().isEmpty()) {
            Glide.with(context)
                    .load(user.getUserImage())
                    .centerCrop()
                    .into(holder.imgUser);
        } else {
            Bitmap initialsBitmap = createInitialsBitmap(user.getUsername(), 200);
            holder.imgUser.setImageBitmap(initialsBitmap);
        }

        if (user.getFollowers().contains(currentUserName)) {
            holder.btnFollow.setText("Followed");
            holder.btnFollow.setBackgroundColor(context.getColor(R.color.gray_dark));
        } else if (user.getPendingRequests().contains(currentUserName)) {
            holder.btnFollow.setText("Requested");
            holder.btnFollow.setBackgroundColor(context.getColor(R.color.orange));
        } else {
            holder.btnFollow.setText("Follow");
            holder.btnFollow.setBackgroundColor(context.getColor(R.color.purple_primary));
        }

        holder.btnFollow.setOnClickListener(v -> {
            if (holder.btnFollow.getText().toString().equals("Follow") && !user.getFollowers().contains(currentUserName)) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_delete_confirmation);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Get references to the buttons
                TextView tvMessage = dialog.findViewById(R.id.tv_message);
                TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
                TextView btnDelete = dialog.findViewById(R.id.btn_delete);

                tvMessage.setText("Do you want to follow this user?");
                btnDelete.setText("Follow");

                // Cancel button click listener
                btnCancel.setOnClickListener(view -> dialog.dismiss());

                // Follow button click
                btnDelete.setOnClickListener(view -> {
                    sendFollowRequest(user.getUsername(), holder.btnFollow);
                    dialog.dismiss();
                });


                dialog.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("from", "otherUserProfile");
                bundle.putSerializable("user", user);
                Navigation.findNavController(v).navigate(R.id.action_navigation_search_to_profileFragment, bundle);
            }
        });

    }

    private void sendFollowRequest(String username, Button btnFollow) {
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(username)
                .update("pendingRequests", FieldValue.arrayUnion(currentUserName))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btnFollow.setText("Requested");
                        btnFollow.setBackgroundColor(context.getColor(R.color.orange));
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to send follow request", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void updateList(List<UserModel> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername;
        ImageView imgUser;
        Button btnFollow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            imgUser = itemView.findViewById(R.id.imgUser);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}
