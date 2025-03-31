package com.example.uiapp.ui.bottomsheet;

import android.graphics.Color;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uiapp.MainActivity;
import com.example.uiapp.R;
import com.example.uiapp.adapter.FilterEmojiAdapter;
import com.example.uiapp.adapter.OnEmojiClickListener;
import com.example.uiapp.databinding.FragmentMapFilterBinding;
import com.example.uiapp.model.EmojiModel;
import com.example.uiapp.ui.map.MapViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class MapFilter extends BottomSheetDialogFragment implements OnEmojiClickListener {
    private FragmentMapFilterBinding binding;
    List<EmojiModel> emojiList;
    FilterEmojiAdapter emojiAdapter;
    private Chip[] DateChips = new Chip[2];
    //private Chip[] chipIds;
    private Chip[] DisplayChips = new Chip[2];
    private MapViewModel mapViewModel;
    private int selectedChipIndex = -1;
    private int selectedDisplayChipIndex = -1;
    private int selectedDistance = 5;
    private String selectedMood = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMapFilterBinding.inflate(inflater, container, false);
        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
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

        DisplayChips[0] = binding.displaychip1;
        DisplayChips[1] = binding.displaychip2;

        binding.displaychip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {handleDisplayChipSelection(0);}
        });

        binding.displaychip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {handleDisplayChipSelection(1);}
        });

        binding.distanceSlider.addOnChangeListener((slider, value, fromUser) -> {
            selectedDistance = (int) value;
            binding.distanceText.setText(selectedDistance + " km");
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
                mapViewModel.clearFilter();
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
        String displayFilter = "";
        if (selectedDisplayChipIndex == 0) {
            displayFilter = "mine";
        } else if (selectedDisplayChipIndex == 1) {
            displayFilter = "following";
        }

        Log.d("MapFilter", "Mood: " + selectedMood + ", Date: " + dateFilter + ", Display: " + displayFilter + ", Distance: " + selectedDistance + "km");
        MainActivity.moodDistance = selectedDistance;
        //Pass to mapViewModel
        mapViewModel.setFilters(selectedMood, dateFilter, displayFilter);
    }
    private void setEmojiAdapter() {
        emojiList.add(new EmojiModel(R.drawable.happy, "Happy", getResources().getColor(R.color.happy)));
        emojiList.add(new EmojiModel(R.drawable.sad_emoji, "Sad", getResources().getColor(R.color.sad)));
        emojiList.add(new EmojiModel(R.drawable.disgust_emoji, "Fear", getResources().getColor(R.color.fear)));
        emojiList.add(new EmojiModel(R.drawable.image_4, "Disgust", getResources().getColor(R.color.disgust)));
        emojiList.add(new EmojiModel(R.drawable.image_5, "Anger", getResources().getColor(R.color.angry)));
        emojiList.add(new EmojiModel(R.drawable.image_6, "Confused", getResources().getColor(R.color.confused)));
        emojiList.add(new EmojiModel(R.drawable.image_7, "Shame", getResources().getColor(R.color.shame)));
        emojiList.add(new EmojiModel(R.drawable.image_10, "Surprised", getResources().getColor(R.color.surprized)));
        emojiList.add(new EmojiModel(R.drawable.image_11, "Tired", getResources().getColor(R.color.tired)));
        emojiList.add(new EmojiModel(R.drawable.image_12, "Anxious", getResources().getColor(R.color.anxious)));
        emojiList.add(new EmojiModel(R.drawable.image_9, "Proud", getResources().getColor(R.color.proud)));
        emojiList.add(new EmojiModel(R.drawable.image_3, "Bored", getResources().getColor(R.color.bored)));
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
    private void handleDisplayChipSelection(int selectedChip) {
        selectedDisplayChipIndex = selectedChip;
        for (int i = 0; i < DisplayChips.length; i++) {
            if (i == selectedChip) {
                DisplayChips[i].setChipBackgroundColorResource(R.color.purple_primary);
                DisplayChips[i].setTextColor(getResources().getColor(R.color.white));
            } else {
                DisplayChips[i].setChipBackgroundColorResource(R.color.gray_primary);
                DisplayChips[i].setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
}