package com.example.uiapp.ui.home;

import static com.example.uiapp.MainActivity.createInitialsBitmap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;

import com.bumptech.glide.Glide;
import com.example.uiapp.MainActivity;
import com.example.uiapp.R;
import com.example.uiapp.adapter.MoodAdapter;
import com.example.uiapp.databinding.FragmentHomeModeBinding;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.ui.profile.LoginActivity;
import com.example.uiapp.ui.profile.SignupActivity;
import com.example.uiapp.utils.HelperClass;
import com.example.uiapp.utils.OnItemEntryClick;
import com.example.uiapp.utils.SharedPrefHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeModeFragment extends Fragment implements OnItemEntryClick {
    private MoodViewModel moodViewModel;
    private FragmentHomeModeBinding binding;
    private MoodAdapter myMoodsAdapter;
    private MoodAdapter followingMoodsAdapter;
    ProgressDialog progressDialog;
    ArrayList<MoodEntry> listOfMyMoods = new ArrayList<>();
    ArrayList<MoodEntry> listOfOtherMoods = new ArrayList<>();
    LottieAnimationView petAnimationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeModeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);

        moodViewModel = new ViewModelProvider(requireActivity()).get(MoodViewModel.class);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        binding.currentDate.setText(sdf.format(new Date()));

        binding.rvMyMoods.setLayoutManager(new LinearLayoutManager(getContext()));
        myMoodsAdapter = new MoodAdapter("home", getContext(), new ArrayList<>(), null, null, this);
        binding.rvMyMoods.setAdapter(myMoodsAdapter);

        binding.rvFollowingMoods.setLayoutManager(new LinearLayoutManager(getContext()));
        followingMoodsAdapter = new MoodAdapter("home", getContext(), new ArrayList<>(), null, null, this);
        binding.rvFollowingMoods.setAdapter(followingMoodsAdapter);

        observeViewModel();

        binding.imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeModeFragment_to_navigation_notifications);
            }
        });

        binding.profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("from", "myUserProfile");
                bundle.putSerializable("user", HelperClass.users);
                Navigation.findNavController(v).navigate(R.id.action_homeModeFragment_to_profileFragment, bundle);
            }
        });

        binding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeModeFragment_to_navigation_search);
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();

        HelperClass.users = SharedPrefHelper.getUser(requireContext());
        if (HelperClass.users != null) {
            binding.tvWelcome.setText("Welcome, " + HelperClass.users.getUsername());
            showProgressDialog();
            if (!HelperClass.users.getUserImage().isEmpty()) {
                Glide.with(requireContext())
                        .load(HelperClass.users.getUserImage())
                        .centerCrop()
                        .into(binding.imgUser);
            } else {
                Bitmap initialsBitmap = createInitialsBitmap(HelperClass.users.getUsername(), 200);
                binding.imgUser.setImageBitmap(initialsBitmap);
            }
            moodViewModel.fetchMoodEvents();
        }
    }

    private void observeViewModel() {
        moodViewModel.getMoodEntries().observe(getViewLifecycleOwner(), moodEntries -> {
            listOfMyMoods.clear();
            listOfMyMoods.addAll(moodEntries);
            //
            List<MoodEntry> singleMoodEntry = new ArrayList<>();


            if (!moodEntries.isEmpty()) {
                MoodEntry latestMood = moodEntries.get(0); // Get the latest mood entry
                singleMoodEntry.add(latestMood);

                // Update octopus animation based on the latest mood
                if (latestMood.getMood().equalsIgnoreCase("happy")) {
                    binding.petAnimationView.setAnimation("happy_octopus.json");
                } else if (latestMood.getMood().equalsIgnoreCase("sad")) {
                    binding.petAnimationView.setAnimation("sad_octopus.json");
                } else if (latestMood.getMood().equalsIgnoreCase("angry")) {
                    binding.petAnimationView.setAnimation("angry_octopus.json");
                } else {
                    binding.petAnimationView.setAnimation("basic_octopus.json");
                }

                binding.petAnimationView.playAnimation();
            }






            myMoodsAdapter.updateList(singleMoodEntry);
            toggleVisibility(moodEntries, binding.rvMyMoods, binding.tvNoMyDataFound);
            hideProgressDialog();
        });
    }

    private void toggleVisibility(java.util.List<?> list, View recyclerView, View noDataView) {
        if (list == null || list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noDataView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noDataView.setVisibility(View.GONE);
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
    public void onItemClick(int position) {
        if (position >= 0 && position < listOfMyMoods.size()) {
            MoodEntry entry = listOfMyMoods.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", entry);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeModeFragment_to_modeDetailsFragment, bundle);
        }else if (position >= 0 && position < listOfOtherMoods.size()) {
            MoodEntry entry = listOfMyMoods.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", entry);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeModeFragment_to_modeDetailsFragment, bundle);
        }
    }
}
