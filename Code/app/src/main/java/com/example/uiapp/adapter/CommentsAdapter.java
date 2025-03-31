package com.example.uiapp.adapter;

import static com.example.uiapp.MainActivity.createInitialsBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.model.CommentsModel;
import com.example.uiapp.model.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.Vh> {
    private final Context context;
    private List<CommentsModel> requestList;

    public CommentsAdapter(Context context, List<CommentsModel> requestList) {
        this.context = context;
        this.requestList = requestList;
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new Vh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        CommentsModel model = requestList.get(position);
        Glide.with(holder.imgUser.getContext()).load(model.getUserImage()).error(R.drawable.iv_user_search).into(holder.imgUser);
        holder.textUsername.setText(model.getUserName());
        holder.tvComment.setText(model.getComments());

        Bitmap initialsBitmap = createInitialsBitmap(model.getUserName(), 200);
        holder.imgUser.setImageBitmap(initialsBitmap);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(model.getUserName())
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

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView textUsername, tvComment;
        ImageView imgUser;

        public Vh(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            imgUser = itemView.findViewById(R.id.imgUser);
        }
    }
}
