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

public class HistoryFragment extends Fragment implements OnItemDeleteClickListener, OnItemEditClickListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private MoodAdapter moodAdapter;
    private List<MoodEntry> moodList;
    private List<MoodEntry> filteredMoodList;
    private HomeViewModel homeViewModel;
    private String currentSearchText = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        recyclerView = binding.historyRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

        moodList = new ArrayList<>();
        filteredMoodList = new ArrayList<>();
        moodList.add(new MoodEntry("Yesterday, Feb 13, 2025 | 22:10", "Bored", "Trip, Calgary", "With 1+ person", "Calgary, Alberta", R.drawable.sad_emoji, 0));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, 0));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
        moodList.add(new MoodEntry("Thu, Mar 20, 2025 | 15:32", "Tired", "Single, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.disgust_emoji, R.drawable.example_image));
        filteredMoodList.addAll(moodList);
        moodAdapter = new MoodAdapter(getContext(), filteredMoodList, this,this);
        recyclerView.setAdapter(moodAdapter);

        //Observe filter changges
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

    private void applySearchAndFilters() {
        // First apply the mood and date filters from ViewModel
        List<MoodEntry> filteredByMoodAndDate = homeViewModel.applyFilters(moodList);

        // Then apply the search text filter
        filteredMoodList.clear();
        if (currentSearchText.isEmpty()) {
            filteredMoodList.addAll(filteredByMoodAndDate);
        } else {
            String searchText = currentSearchText.toLowerCase();
            for (MoodEntry entry : filteredByMoodAndDate) {
                if (entry.getMood().toLowerCase().contains(searchText) ||
                entry.getNote().toLowerCase().contains(searchText) ||
                entry.getLocation().toLowerCase().contains(searchText) ||
                        entry.getPeople().toLowerCase().contains(searchText)) {
                    filteredMoodList.add(entry);
                }
            }
        }
        moodAdapter.notifyDataSetChanged();
    }

    private void filter(String text){
        currentSearchText = text;
        applySearchAndFilters();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClickDelete(int position) {
        showDeleteDialog();

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

    @Override
    public void onClickEdit(int position) {
        Navigation.findNavController(this.getView()).navigate(R.id.action_navigation_home_to_editModeFragment);
    }
}