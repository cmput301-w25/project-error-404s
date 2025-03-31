package com.example.uiapp.ui.moodfollowing;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.uiapp.R;
import com.example.uiapp.adapter.FollowingMoodAdapter;
import com.example.uiapp.adapter.OnItemDeleteClickListener;
import com.example.uiapp.adapter.OnItemEditClickListener;
import com.example.uiapp.databinding.FragmentMoodFollowingBinding;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.ui.history.HomeViewModel;
import com.example.uiapp.utils.HelperClass;

import java.util.ArrayList;
import java.util.List;

public class MoodFollowingFragment extends Fragment implements OnItemDeleteClickListener, OnItemEditClickListener {
    private FragmentMoodFollowingBinding binding;
    private FollowingMoodAdapter moodAdapter;
    private HomeViewModel homeViewModel;
    ProgressDialog progressDialog;
    private List<MoodEntry> moodList = new ArrayList<>();
    private List<MoodEntry> filteredMoodList = new ArrayList<>();

    public MoodFollowingFragment() {
        // Required empty public constructor
    }

    public List<MoodEntry> getMoodList() {
        return moodList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMoodFollowingBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);

        binding.rvFollowingMoods.setLayoutManager(new LinearLayoutManager(getContext()));
        moodAdapter = new FollowingMoodAdapter(getContext(), new ArrayList<>(), new OnItemEditClickListener() {
            @Override
            public void onClickEdit(int position) {
                MoodEntry entry = moodAdapter.getMoodList().get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", entry);
                Navigation.findNavController(view).navigate(R.id.action_navigation_mood_followings_to_modeDetailsFragment, bundle);
            }
        });
        binding.rvFollowingMoods.setAdapter(moodAdapter);

        homeViewModel.setCurrentUsername(HelperClass.users.getUsername());

        homeViewModel.getFilteredMoodEntries().observe(getViewLifecycleOwner(), moodEntries -> {
            ArrayList<MoodEntry> listOfMoods = new ArrayList<>();
            for (MoodEntry moodEntry : moodEntries) {
                if (moodEntry.getStatus().equals("Public")) {
                    listOfMoods.add(moodEntry);
                }
            }
            if (listOfMoods.isEmpty()) {
                binding.tvNoMoodFound.setVisibility(View.VISIBLE);
                binding.rvFollowingMoods.setVisibility(View.GONE);
            } else {
                binding.tvNoMoodFound.setVisibility(View.GONE);
                binding.rvFollowingMoods.setVisibility(View.VISIBLE);
            }
            moodAdapter.updateList(listOfMoods);
            hideProgressDialog();
        });

        binding.searchLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                homeViewModel.setSearchQuery(s.toString());
            }
        });

        binding.filterButton.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.filtterBottomSheetFragment)
        );

    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.clearMoodEntries();
        if (HelperClass.users != null) {
            showProgressDialog();
            homeViewModel.fetchMoodEntries("following");
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
    public void onClickDelete(int position) {
        showDeleteDialog(position);
    }

    @Override
    public void onClickEdit(int position) {
        if (position >= 0 && position < filteredMoodList.size()) {
            MoodEntry entryToEdit = filteredMoodList.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("mood", entryToEdit);
            Navigation.findNavController(this.getView()).navigate(R.id.action_navigation_home_to_editModeFragment, bundle);
        }
    }

    private void showDeleteDialog(int position) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_delete_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Get references to the buttons
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnDelete = dialog.findViewById(R.id.btn_delete);

        // Cancel button click listener
        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnDelete.setOnClickListener(view -> {
            if (position >= 0 && position < filteredMoodList.size()) {
                MoodEntry entryToDelete = filteredMoodList.get(position);
                showProgressDialog();
                homeViewModel.deleteMoodEntry(entryToDelete);
            }
            dialog.dismiss();
        });


        dialog.show();
    }


}