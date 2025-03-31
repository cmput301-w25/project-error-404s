package com.example.uiapp.ui.bottomsheet;

import android.graphics.Color;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uiapp.R;
import com.example.uiapp.adapter.FilterEmojiAdapter;
import com.example.uiapp.adapter.OnEmojiClickListener;
import com.example.uiapp.databinding.FragmentMoodFilterBinding;
import com.example.uiapp.model.EmojiModel;
import com.example.uiapp.ui.history.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class MoodFilter extends BottomSheetDialogFragment implements OnEmojiClickListener {
    private FragmentMoodFilterBinding binding;
    List<EmojiModel> emojiList;
    FilterEmojiAdapter emojiAdapter;
    private Chip[] DateChips = new Chip[2];
    //private Chip[] chipIds;
    private Chip[] DisplayChips = new Chip[2];
    private HomeViewModel homeViewModel;
    private int selectedChipIndex = -1;
    private String selectedMood = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMoodFilterBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        emojiList = new ArrayList<>();
        setEmojiAdapter();
        DateChips[0] = binding.datechip1;
        DateChips[1] = binding.datechip2;
//        handleChipSelection(0);

        binding.datechip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(0);
            }
        });
        binding.datechip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(1);
            }
        });

        binding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyMoodFilters();
                dismiss();
            }
        });
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setFilters("", "");
                dismiss();
            }
        });

        return binding.getRoot();
    }

    private void applyMoodFilters(){
        //Apply date filter
        String dateFilter = "";
        if (selectedChipIndex == 0){
            dateFilter = "24h";
        }else if(selectedChipIndex == 1){
            dateFilter = "7d";
        }
        //Pass to homeViewModel
        homeViewModel.setFilters(selectedMood, dateFilter);
    }
    private void setEmojiAdapter() {
        emojiList.add(new EmojiModel(R.drawable.happy, R.drawable.happy_gray,"Happy", getResources().getColor(R.color.happy)));
        emojiList.add(new EmojiModel(R.drawable.sad_emoji, R.drawable.sad_gray, "Sad", getResources().getColor(R.color.sad)));
        emojiList.add(new EmojiModel(R.drawable.fear, R.drawable.fear_gray, "Fear", getResources().getColor(R.color.fear)));
        emojiList.add(new EmojiModel(R.drawable.disgust, R.drawable.disgust_gray, "Disgust", getResources().getColor(R.color.disgust)));
        emojiList.add(new EmojiModel(R.drawable.anger, R.drawable.anger_gray,"Anger", getResources().getColor(R.color.angry)));
        emojiList.add(new EmojiModel(R.drawable.confused, R.drawable.confused_gray,"Confused", getResources().getColor(R.color.confused)));
        emojiList.add(new EmojiModel(R.drawable.shame, R.drawable.shame_gray,"Shame", getResources().getColor(R.color.shame)));
        emojiList.add(new EmojiModel(R.drawable.surprised, R.drawable.surprised_gray,"Surprised", getResources().getColor(R.color.surprized)));
        emojiList.add(new EmojiModel(R.drawable.tired, R.drawable.tired_gray, "Tired", getResources().getColor(R.color.tired)));
        emojiList.add(new EmojiModel(R.drawable.anxious, R.drawable.anxious_gray,"Anxious", getResources().getColor(R.color.anxious)));
        emojiList.add(new EmojiModel(R.drawable.proud, R.drawable.proud_gray,"Proud", getResources().getColor(R.color.proud)));
        emojiList.add(new EmojiModel(R.drawable.bored, R.drawable.bored_gray,"Bored", getResources().getColor(R.color.bored)));
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        //emojiAdapter = new FilterEmojiAdapter( emojiList,this::onEmojiClick);
        emojiAdapter = new FilterEmojiAdapter( emojiList,this);
        binding.recyclerView.setAdapter(emojiAdapter);
    }

    @Override
    public void onEmojiClick(int position) {
        Log.d("TAG", "onEmojiClick: "+position);
        //clear previous selection
        for (int i = 0; i < emojiList.size(); i++){
            emojiList.get(i).setSelected(i == position);
        }
        selectedMood = emojiList.get(position).getName();
        emojiAdapter.notifyDataSetChanged();
    }

    //handleChipSelection() is for controlling UI visual effects
    private void handleChipSelection(int selectedChip) {
        selectedChipIndex = selectedChip;//
        for (int i = 0; i < DateChips.length; i++) {
            if (i == selectedChip) {
                // Selected chip styling
                DateChips[i].setChipBackgroundColorResource(R.color.purple_primary);
//                chips[i].setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                DateChips[i].setTextColor(Color.WHITE);
            } else {
                // Reset other chips
                DateChips[i].setChipBackgroundColorResource(R.color.gray_primary);
//                chips[i].setChipIconTint(ColorStateList.valueOf(Color.BLACK));
                DateChips[i].setTextColor(Color.BLACK);
            }
        }
    }
}