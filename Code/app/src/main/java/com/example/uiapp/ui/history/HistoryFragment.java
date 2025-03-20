package com.example.uiapp.ui.history;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements OnItemDeleteClickListener, OnItemEditClickListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private MoodAdapter moodAdapter;
    private List<MoodEntry> moodList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        recyclerView = binding.historyRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        binding.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_filtterBottomSheetFragment);
            }
        });

        moodList = new ArrayList<>();
        moodList.add(new MoodEntry("Yesterday, Feb 13, 2025 | 22:10", "Bored", "Trip, Calgary", "With 1+ person", "Calgary, Alberta", R.drawable.sad_emoji, 0));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, 0));
        moodList.add(new MoodEntry("Sun, Feb 9, 2025 | 15:32", "Happy", "Family, trip, Banff", "With 2+ person", "Banff, Alberta", R.drawable.happy, R.drawable.example_image));
        moodAdapter = new MoodAdapter(getContext(), moodList, this,this);
        recyclerView.setAdapter(moodAdapter);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
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