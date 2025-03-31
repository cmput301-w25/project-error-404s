package com.example.uiapp.ui.notifications;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.uiapp.R;
import com.example.uiapp.adapter.NotificationAdapter;
import com.example.uiapp.databinding.FragmentNotificationsBinding;
import com.example.uiapp.utils.HelperClass;
import com.example.uiapp.utils.OnItemNotificationClick;
import com.example.uiapp.utils.SharedPrefHelper;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements OnItemNotificationClick {

    private FragmentNotificationsBinding binding;
    private NotificationAdapter notificationAdapter;
    private NotificationsViewModel notificationsViewModel;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationsViewModel = new ViewModelProvider(requireActivity()).get(NotificationsViewModel.class);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);

        binding.userRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(getContext(), new ArrayList<>(), this);
        binding.userRecycler.setAdapter(notificationAdapter);

        notificationsViewModel.getPendingRequests().observe(getViewLifecycleOwner(), requests -> {
            binding.txtNotificationCount.setText(String.valueOf(requests.size()));
            if (requests.size() > 1){
                binding.tvLabel.setText("Notifications");
            }else{
                binding.tvLabel.setText("Notification");
            }
            notificationAdapter.updateList(requests);
            hideProgressDialog();
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (HelperClass.users != null) {
            showProgressDialog();
            notificationsViewModel.fetchRequests();
        }
    }

    private void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(String from, String followerUsername) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_delete_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Get references to the buttons
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnDelete = dialog.findViewById(R.id.btn_delete);

        if (from.equals("accept")){
            tvMessage.setText("Will you accept this user?");
            btnDelete.setText("Accept");
        }else if (from.equals("decline")){
            tvMessage.setText("Will you decline this user?");
            btnDelete.setText("Decline");
        }

        // Cancel button click listener
        btnCancel.setOnClickListener(view -> dialog.dismiss());

        // Accept button click
        btnDelete.setOnClickListener(view -> {
            showProgressDialog();
            if (from.equals("accept")){
                acceptRequest(followerUsername);
            }else if (from.equals("decline")){
                declineRequest(followerUsername);
            }
            dialog.dismiss();
        });


        dialog.show();
    }

    private void acceptRequest(String followerUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUsername = HelperClass.users.getUsername();

        // Update the current user's followers list
        db.collection("Users").document(currentUsername)
                .update("followers", FieldValue.arrayUnion(followerUsername))
                .addOnSuccessListener(aVoid -> {
                    // Update the follower's following list
                    db.collection("Users").document(followerUsername)
                            .update("following", FieldValue.arrayUnion(currentUsername))
                            .addOnSuccessListener(aVoid1 -> {
                                // Remove from pendingRequests
                                db.collection("Users").document(currentUsername)
                                        .update("pendingRequests", FieldValue.arrayRemove(followerUsername))
                                        .addOnSuccessListener(aVoid2 -> {
                                            notificationsViewModel.fetchRequests();
                                        })
                                        .addOnFailureListener(e -> {
                                            hideProgressDialog();
                                            Toast.makeText(requireContext(), "Failed to update pendingRequests", Toast.LENGTH_SHORT).show();
                                        });

                            }).addOnFailureListener(e -> {
                                hideProgressDialog();
                                Toast.makeText(requireContext(), "Failed to update following", Toast.LENGTH_SHORT).show();
                            });

                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(requireContext(), "Failed to update followers", Toast.LENGTH_SHORT).show();
                });
    }

    private void declineRequest(String followerUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUsername = HelperClass.users.getUsername();

        // Remove from pendingRequests
        db.collection("Users").document(currentUsername)
                .update("pendingRequests", FieldValue.arrayRemove(followerUsername))
                .addOnSuccessListener(aVoid -> {
                    notificationsViewModel.fetchRequests();
                })
                .addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(requireContext(), "Failed to decline request", Toast.LENGTH_SHORT).show();
                });
    }

}