package com.example.uiapp.ui.home;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uiapp.R;
import com.example.uiapp.adapter.MoodAdapter;
import com.example.uiapp.adapter.OnItemDeleteClickListener;
import com.example.uiapp.adapter.OnItemEditClickListener;
import com.example.uiapp.databinding.FragmentHomeBinding;
import com.example.uiapp.databinding.FragmentHomeModeBinding;
import com.example.uiapp.model.MoodEntry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeModeFragment extends Fragment {
    private FragmentHomeModeBinding binding;
    private RecyclerView recyclerView;
    private MoodAdapter moodAdapter;
    private List<MoodEntry> moodList;

    // Firebase instance
    private FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeModeBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerView;

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch mood events from Firestore
        fetchMoodEvents();

        // Dummy data
        moodList = new ArrayList<>();
        moodList.add(new MoodEntry("Yesterday, Feb 13, 2025 | 22:10", "Bored", "Trip, Calgary", "With 1+ person", "Calgary, Alberta", R.drawable.sad_emoji, 0, false));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image, false));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image, false));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, 0, false));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image, false));
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMMM", Locale.getDefault());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        binding.currentDate.setText(currentDateTime);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        moodAdapter = new MoodAdapter(getContext(), moodList, null, null);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(moodAdapter);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return binding.getRoot();
    }

    private void fetchMoodEvents() {
        db.collection("MoodEvents").get()
                .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                    moodList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        MoodEntry event = doc.toObject(MoodEntry.class);
                        // Optionally set Firestore ID: event.setFirestoreId(doc.getId());
                        moodList.add(event);
                    }
                    moodAdapter = new MoodAdapter(getContext(), moodList, null, null);
                    recyclerView.setAdapter(moodAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching mood events", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void showDeleteDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_delete_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Get references to the buttons
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnDelete = dialog.findViewById(R.id.btn_delete);

        // Cancel button click listener
        btnCancel.setOnClickListener(view -> dialog.dismiss());

        // Delete button click listener
        btnDelete.setOnClickListener(view -> {
            // Perform delete action here
            dialog.dismiss();
        });

        dialog.show();
    }

}