package com.example.uiapp.ui.history;

import static java.util.Locale.filter;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import androidx.annotation.NonNull;
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

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment responsible for displaying the mood history list.
 * Implements delete and edit functionality for mood entries.
 */
public class HistoryFragment extends Fragment implements OnItemDeleteClickListener, OnItemEditClickListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private MoodAdapter moodAdapter;
    private List<MoodEntry> moodList;
    private List<MoodEntry> filteredMoodList;
    private HomeViewModel homeViewModel;
    private String currentSearchText = "";

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

        // Initialize lists
        moodList = new ArrayList<>();
        filteredMoodList = new ArrayList<>();

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

        // Sample data for testing - can remove later
//        moodList.add(new MoodEntry("Yesterday, Feb 13, 2025 | 22:10", "Bored", "Trip, Calgary", "With 1+ person", "Calgary, Alberta", R.drawable.sad_emoji, 0));
//        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
//        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
//        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, 0));
//        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));

        // Initialize adapter with proper listeners
        moodAdapter = new MoodAdapter(getContext(), filteredMoodList, this, this);
        recyclerView.setAdapter(moodAdapter);

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

    /**
     * Applies both search text and filter criteria to the mood list.
     * Handles both filtering by mood/date from ViewModel and text searching.
     * Includes safety checks for null values to prevent crashes.
     */
    private void applySearchAndFilters() {
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
            // Model/Data Layer: Update the data
            MoodEntry entryToDelete = filteredMoodList.get(position);               // Referring the mood gonna be deleted by (int position)
            moodList.remove(entryToDelete);                                         // Remove from the original list to delete
            filteredMoodList.remove(position);                                      // Remove from the filtered list
            // View/UI Layer: Update the view
            moodAdapter.notifyItemRemoved(position);                                // Notify the item removed to adapter
            moodAdapter.notifyItemRangeChanged(position, filteredMoodList.size());  // Notify data changed to adpter
            // View/UI Layer: Close the confirmation dialog
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
        Navigation.findNavController(this.getView()).navigate(R.id.action_navigation_home_to_editModeFragment);
    }
}