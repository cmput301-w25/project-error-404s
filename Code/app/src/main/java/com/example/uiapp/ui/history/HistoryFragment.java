package com.example.uiapp.ui.history;

import static java.util.Locale.filter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiapp.R;
import com.example.uiapp.adapter.MoodAdapter;
import com.example.uiapp.adapter.OnItemDeleteClickListener;
import com.example.uiapp.adapter.OnItemEditClickListener;
import com.example.uiapp.databinding.FragmentHomeBinding;
import com.example.uiapp.model.MoodEntry;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment responsible for displaying the mood history list.
 * Implements delete and edit functionality for mood entries.
 */
public class HistoryFragment extends Fragment implements OnItemDeleteClickListener, OnItemEditClickListener {
    private static final String TAG = "HistoryFragment";

    // For direct link from maps
    private String scrollToDocumentId;
    //
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private MoodAdapter moodAdapter;
    private List<MoodEntry> moodList;
    private List<MoodEntry> filteredMoodList;
    private HomeViewModel homeViewModel;
    private String currentSearchText = "";

    // Firebase reference
    private FirebaseFirestore db;
    private CollectionReference moodEventsRef;
    /**
     * Creates and initializes the history view.
     * Sets up UI components, search functionality, and data observers.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        recyclerView = binding.historyRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        moodEventsRef = db.collection("MoodEvents");

        if (getArguments() != null) {
            scrollToDocumentId = getArguments().getString("DOCUMENT_ID");
        }


        // Set up search functionality
        EditText searchInput = binding.searchLayout.getEditText();
        if (searchInput != null) {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //filter(s.toString());
                    currentSearchText = s.toString();
                    applySearchAndFilters();//
                }
            });
        }

        binding.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_filtterBottomSheetFragment);
            }
        });
        // Initialize lists
        moodList = new ArrayList<>();
        filteredMoodList = new ArrayList<>();

        // Sample data for testing - can remove later
//        moodList.add(new MoodEntry("Yesterday, Feb 13, 2025 | 22:10", "Bored", "Trip, Calgary", "With 1+ person", "Calgary, Alberta", R.drawable.sad_emoji, 0));
//        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
//        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
//        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, 0));
//        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));

        // Initialize adapter with proper listeners
        moodAdapter = new MoodAdapter(getContext(), filteredMoodList, this, this);
        recyclerView.setAdapter(moodAdapter);

        // Load data from Firebase
        loadMoodEventsFromFirebase();

        //Observe filter changes
        homeViewModel.getFiltersApplied().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                applySearchAndFilters();
            }
        });
        View root = binding.getRoot();
//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void loadMoodEventsFromFirebase() {
        moodEventsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    moodList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // Get data from document
                            String dateTime = document.getString("dateTime");
                            String mood = document.getString("mood");
                            String note = document.getString("note");
                            String people = document.getString("people");
                            String location = document.getString("location");
                            Long moodIconLong = document.getLong("moodIcon");
                            String imageUrl = document.getString("imageUrl");
                            Boolean isHome = document.getBoolean("isHome");

                            // Create MoodEntry with retrieved data
                            if (dateTime != null && mood != null) {
                                int moodIcon = moodIconLong != null ? moodIconLong.intValue() : 0;
                                MoodEntry entry = new MoodEntry(dateTime, mood, note, people, location, moodIcon, imageUrl, isHome);
                                entry.setFirestoreId(document.getId());
                                // Force isHome to null to ensure buttons are visible
                                entry.setIsHome(null);
                                moodList.add(entry);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing document: " + e.getMessage());
                        }
                    }

                    // Apply filters and update UI
                    applySearchAndFilters();

                    // Show empty state or content
                    updateEmptyState();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting documents: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to load mood history", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEmptyState() {
        // Show empty state if no data
        if (moodList.isEmpty()) {
            // If you have an empty state view, show it here
            // binding.emptyStateView.setVisibility(View.VISIBLE);
            // binding.recyclerView.setVisibility(View.GONE);
        } else {
            // binding.emptyStateView.setVisibility(View.GONE);
            // binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }
    /**
     * Applies both search text and filter criteria to the mood list.
     * Handles both filtering by mood/date from ViewModel and text searching.
     * Includes safety checks for null values to prevent crashes.
     */
    private void applySearchAndFilters() {
        try {
            // Check if lists are initialized
            if (moodList == null || filteredMoodList == null) {
                return;
            }

            // First apply the mood and date filters from ViewModel
            List<MoodEntry> filteredByMoodAndDate = homeViewModel.applyFilters(moodList);

            // Then apply the search text filter
            filteredMoodList.clear();

            if (filteredByMoodAndDate == null) {
                // If the ViewModel returns null, use the original list
                filteredByMoodAndDate = new ArrayList<>(moodList);
            }

            if (currentSearchText.isEmpty()) {
                filteredMoodList.addAll(filteredByMoodAndDate);
            } else {
                String searchText = currentSearchText.toLowerCase();
                // Iterate through each entry of filteredByMoodAndDate
                for (MoodEntry entry : filteredByMoodAndDate) {
                    // Check for null values in entry fields
                    String mood = entry.getMood() != null ? entry.getMood().toLowerCase() : "";
                    String note = entry.getNote() != null ? entry.getNote().toLowerCase() : "";
                    String location = entry.getLocation() != null ? entry.getLocation().toLowerCase() : "";
                    String people = entry.getPeople() != null ? entry.getPeople().toLowerCase() : "";

                    // Check if the search text exists in any of the entry's text fields:
                    if (mood.contains(searchText) ||           // 1. Mood, ("Happy", "Sad")
                            note.contains(searchText) ||           // 2. Mood's reason "Note"
                            location.contains(searchText) ||       // 3. GeoLoaction (Not used in the current version)
                            people.contains(searchText)) {         // 4. People
                        // If any of the fields contain the search text, add this entry to the filtered list
                        filteredMoodList.add(entry);
                    }
                }

            }

            // Notify adapter only if it's initialized
            if (moodAdapter != null) {
                moodAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "Filters error: " + e.getMessage());
        }
    }
    /**
     *  Helper method to filter by text and trigger search/filter application.
     */
    private void filter(String text){
        currentSearchText = text;
        applySearchAndFilters();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Implementation of OnItemDeleteClickListener interface.
     * Shows a confirmation dialog when delete is requested.
     */
    @Override
    public void onClickDelete(int position) {
        showDeleteDialog(position);
    }

    /**
     * Displays a confirmation dialog for deleting a mood entry.
     * Handles both UI updates and data model changes on confirmation.
     */
    private void showDeleteDialog(int position) {
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
            try {
                // Model/Data Layer: Update the data
                MoodEntry entryToDelete = filteredMoodList.get(position);

                // Delete from Firestore if it has an ID
                if (entryToDelete.getFirestoreId() != null && !entryToDelete.getFirestoreId().isEmpty()) {
                    moodEventsRef.document(entryToDelete.getFirestoreId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Mood entry deleted from Firestore");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error deleting from Firestore: " + e.getMessage());
                            });
                }

                // Remove from local lists
                moodList.remove(entryToDelete);
                filteredMoodList.remove(position);

                // View/UI Layer: Update the view
                moodAdapter.notifyItemRemoved(position);
                moodAdapter.notifyItemRangeChanged(position, filteredMoodList.size());

                // Update empty state
                updateEmptyState();
            } catch (Exception e) {
                Log.e(TAG, "Error during delete: " + e.getMessage());
            }

            // Close the confirmation dialog
            dialog.dismiss();
        });


        dialog.show();
    }

    /**
     * Implementation of OnItemEditClickListener interface.
     * Navigates to the edit fragment when edit is requested.
     */
    @Override
    public void onClickEdit(int position) {
        try {
            if (position >= 0 && position < filteredMoodList.size()) {
                // Get the selected mood entry by position
                MoodEntry selectedEntry = filteredMoodList.get(position);

                // Create a bundle to pass data to EditModeFragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("moodEntry", selectedEntry);

                // Navigate to edit fragment with the selected mood entry data
                Navigation.findNavController(requireView()).navigate(
                        R.id.action_navigation_home_to_editModeFragment,
                        bundle
                );
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to edit: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to open edit screen", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            scrollToDocumentId = getArguments().getString("DOCUMENT_ID");
        }
    }

    private void setupRecyclerView() {
        // After loading data
        if (scrollToDocumentId != null) {
            int position = findPositionById(scrollToDocumentId);
            if (position != -1) {
                recyclerView.scrollToPosition(position);
            }
        }
    }

    private int findPositionById(String documentId) {
        for (int i = 0; i < moodList.size(); i++) {
            if (moodList.get(i).getDocumentId().equals(documentId)) {
                return i;
            }
        }
        return -1;
    }



}