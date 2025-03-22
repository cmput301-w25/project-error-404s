package com.example.uiapp.ui.home;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.uiapp.R;
import com.example.uiapp.adapter.MoodAdapter;
import com.example.uiapp.adapter.OnItemDeleteClickListener;
import com.example.uiapp.adapter.OnItemEditClickListener;
import com.example.uiapp.databinding.FragmentHomeBinding;
import com.example.uiapp.databinding.FragmentHomeModeBinding;
import com.example.uiapp.model.MoodEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeModeFragment extends Fragment {

    private com.example.uiapp.ui.home.MoodViewModel moodViewModel;
    private FragmentHomeModeBinding binding;
    private RecyclerView recyclerView;
    private MoodAdapter moodAdapter;
    private List<MoodEntry> moodList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeModeBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        // Initialize ViewModel
        moodViewModel = new ViewModelProvider(requireActivity()).get(com.example.uiapp.ui.home.MoodViewModel.class);

        // Initialize adapter with empty list
        moodAdapter = new MoodAdapter(getContext(), new ArrayList<>(), null, null);
        recyclerView.setAdapter(moodAdapter);

        // Observe changes in the mood list
        moodViewModel.getMoodEntries().observe(getViewLifecycleOwner(), moodEntries -> {
            moodAdapter.updateList(moodEntries); // Update the adapter's data
        });

        return binding.getRoot();
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