package com.example.uiapp.ui.edit;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import com.example.uiapp.R;
import com.example.uiapp.adapter.EmojiAdapter;
import com.example.uiapp.adapter.OnEmojiClickListener;
import com.example.uiapp.databinding.FragmentEditModeBinding;
import com.example.uiapp.model.EmojiModel;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.ui.home.MoodViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditModeFragment extends Fragment implements OnEmojiClickListener {
    public FragmentEditModeBinding binding;
    List<EmojiModel> emojiList;
    EmojiAdapter emojiAdapter;
    RecyclerView recyclerViewEmojis;
    private boolean isExpanded = false;
    private boolean isExpandedPhoto = false;
    private boolean isExpandedPeople = false;
    private boolean isExpandedLocation = false;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Chip[] chips = new Chip[4];
    private int[] chipIds = {R.id.chip1, R.id.chip2, R.id.chip3, R.id.chip4};
    
    // Firebase instance
    private FirebaseFirestore db;
    
    // Variables to store edit state
    private MoodEntry moodEntryToEdit;
    private String selectedMood;
    private int selectedMoodIcon;
    private String selectedPeople;
    private Uri selectedImageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        binding = FragmentEditModeBinding.inflate(inflater, container, false);
        
        // Get the MoodEntry passed from HistoryFragment
        if (getArguments() != null && getArguments().containsKey("moodEntry")) {
            moodEntryToEdit = (MoodEntry) getArguments().getSerializable("moodEntry");
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        String currentTime = time.format(new Date().getTime());
        ChipGroup chipGroup = binding.expandablePeople.chipGroup;
        
        // Initialize chips
        binding.expandablePeople.chip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(0);
                selectedPeople = "Alone";
            }
        });
        binding.expandablePeople.chip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(1);
                selectedPeople = "With 1+ person";
            }
        });
        binding.expandablePeople.chip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(2);
                selectedPeople = "With 2+ people";
            }
        });
        binding.expandablePeople.chip4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(3);
                selectedPeople = "With family";
            }
        });
        chips[0] = binding.expandablePeople.chip1;
        chips[1] = binding.expandablePeople.chip2;
        chips[2] = binding.expandablePeople.chip3;
        chips[3] = binding.expandablePeople.chip4;

        binding.date.setText("Today, "+currentDateTime);
        binding.tvTime.setText(currentTime);
        recyclerViewEmojis = binding.recyclerView;
        emojiList = new ArrayList<>();
        setEmojiAdapter();
        setExpandedLayoutSection();
        
        // Fill UI with existing mood data if editing
        if (moodEntryToEdit != null) {
            populateUIWithMoodData();
        }

        // Example: When user clicks the "Save" (btnAdd) button, update the event.
        binding.btnAdd.setOnClickListener(v -> {
            if (moodEntryToEdit != null) {
                // Get updated values from UI
                String updatedNote = binding.expandableNote.editText.getText().toString();
                String updatedLocation = binding.expandableLocation.txtLocation.getText().toString();
                
                // Update the mood entry with new values
                if (selectedMood != null) {
                    moodEntryToEdit.setMood(selectedMood);
                }
                if (selectedMoodIcon != 0) {
                    moodEntryToEdit.setMoodIcon(selectedMoodIcon);
                }
                if (!TextUtils.isEmpty(updatedNote)) {
                    moodEntryToEdit.setNote(updatedNote);
                }
                if (selectedPeople != null) {
                    moodEntryToEdit.setPeople(selectedPeople);
                }
                if (!TextUtils.isEmpty(updatedLocation)) {
                    moodEntryToEdit.setLocation(updatedLocation);
                }
                if (selectedImageUri != null) {
                    moodEntryToEdit.setImageUrl(selectedImageUri.toString());
                }
                
                // If the mood has a Firestore ID, update it in Firebase
                if (moodEntryToEdit.getFirestoreId() != null) {
                    db.collection("MoodEvents")
                        .document(moodEntryToEdit.getFirestoreId())
                        .update(
                            "mood", moodEntryToEdit.getMood(),
                            "note", moodEntryToEdit.getNote(),
                            "people", moodEntryToEdit.getPeople(),
                            "location", moodEntryToEdit.getLocation(),
                            "moodIcon", moodEntryToEdit.getMoodIcon(),
                            "imageUri", moodEntryToEdit.getImageUrl()
                        )
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Mood updated successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(binding.getRoot()).navigateUp();
                        })
                        .addOnFailureListener(e -> {
                            // Even if Firebase update fails, we can still update local data
                            Toast.makeText(getContext(), "Cloud update failed, but local data updated", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(binding.getRoot()).navigateUp();
                        });
                } else {
                    // Just local update if no Firestore ID
                    Toast.makeText(getContext(), "Mood updated successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                }
            } else {
                Toast.makeText(getContext(), "No mood entry to update", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigateUp();
        });

        return binding.getRoot();
    }
    
    // Fill UI with existing mood data
    private void populateUIWithMoodData() {
        if (moodEntryToEdit == null) return;
        
        // Set title to indicate edit mode
        TextView titleTextView = binding.getRoot().findViewById(R.id.tvTitle);
        if (titleTextView != null) {
            titleTextView.setText("Edit Mood");
        }
        
        // Pre-fill date and time
        if (moodEntryToEdit.getDateTime() != null) {
            // If the date has format like "Today, Mar 23, 2024 | 12:45"
            if (moodEntryToEdit.getDateTime().contains("|")) {
                String[] parts = moodEntryToEdit.getDateTime().split("\\|");
                binding.date.setText(parts[0].trim());
                binding.tvTime.setText(parts[1].trim());
            } else {
                binding.date.setText(moodEntryToEdit.getDateTime());
            }
        }
        
        // Pre-select the emoji/mood
        if (moodEntryToEdit.getMood() != null) {
            selectedMood = moodEntryToEdit.getMood();
            selectedMoodIcon = moodEntryToEdit.getMoodIcon();
            
            // Highlight the emoji in the grid (if we had a way to find it)
            // This would require an update to the EmojiAdapter to support selection
        }
        
        // Pre-fill note
        if (moodEntryToEdit.getNote() != null) {
            binding.expandableNote.editText.setText(moodEntryToEdit.getNote());
            binding.expandableNote.contentLayout.setVisibility(View.VISIBLE);
            isExpanded = true;
            binding.expandableNote.arrowIcon.setRotation(180);
        }
        
        // Pre-fill image if any
        if (moodEntryToEdit.getImageUrl() != null && !moodEntryToEdit.getImageUrl().isEmpty()) {
            try {
                selectedImageUri = Uri.parse(moodEntryToEdit.getImageUrl());
                binding.expandablePhoto.imgSelected.setImageURI(selectedImageUri);
                binding.expandablePhoto.contentLayout.setVisibility(View.VISIBLE);
                isExpandedPhoto = true;
                binding.expandablePhoto.arrowIcon.setRotation(180);
            } catch (Exception e) {
                // Handle image loading error
            }
        }
        
        // Pre-select people chip
        if (moodEntryToEdit.getPeople() != null) {
            selectedPeople = moodEntryToEdit.getPeople();
            binding.expandablePeople.contentLayout.setVisibility(View.VISIBLE);
            isExpandedPeople = true;
            binding.expandablePeople.arrowIcon.setRotation(180);
            
            // Select the appropriate chip
            if (selectedPeople.equals("Alone")) {
                handleChipSelection(0);
            } else if (selectedPeople.equals("With 1+ person")) {
                handleChipSelection(1);
            } else if (selectedPeople.equals("With 2+ people")) {
                handleChipSelection(2);
            } else if (selectedPeople.equals("With family")) {
                handleChipSelection(3);
            }
        }
        
        // Pre-fill location
        if (moodEntryToEdit.getLocation() != null) {
            binding.expandableLocation.txtLocation.setText(moodEntryToEdit.getLocation());
            binding.expandableLocation.contentLayout.setVisibility(View.VISIBLE);
            isExpandedLocation = true;
            binding.expandableLocation.arrowIcon.setRotation(180);
        }
        
        // Enable the save button
        binding.btnAdd.setEnabled(true);
        binding.btnAdd.setBackgroundColor(getResources().getColor(R.color.purple_primary));
    }

    private void setExpandedLayoutSection() {
        binding.expandableNote.headerLayout.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            binding.expandableNote.contentLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            binding.expandableNote. arrowIcon.setRotation(isExpanded ? 180 : 0);
        });
        binding.expandablePhoto.headerLayout.setOnClickListener(v -> {
            isExpandedPhoto = !isExpandedPhoto;
            binding.expandablePhoto.contentLayout.setVisibility(isExpandedPhoto ? View.VISIBLE : View.GONE);
            binding.expandablePhoto. arrowIcon.setRotation(isExpandedPhoto ? 180 : 0);
            binding.expandablePhoto.imageButton.setOnClickListener(v1 -> openGallery());
        });
        binding.expandablePeople.headerLayout.setOnClickListener(v -> {
            isExpandedPeople = !isExpandedPeople;
            binding.expandablePeople.contentLayout.setVisibility(isExpandedPeople ? View.VISIBLE : View.GONE);
            binding.expandablePeople. arrowIcon.setRotation(isExpandedPeople ? 180 : 0);
        });
        binding.expandableLocation.headerLayout.setOnClickListener(v -> {
            isExpandedLocation = !isExpandedLocation;
            binding.expandableLocation.contentLayout.setVisibility(isExpandedLocation ? View.VISIBLE : View.GONE);
            binding.expandableLocation. arrowIcon.setRotation(isExpandedLocation ? 180 : 0);
        });
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
        recyclerViewEmojis.setLayoutManager(new GridLayoutManager(getContext(), 4));
        emojiAdapter = new EmojiAdapter(emojiList, this);
        recyclerViewEmojis.setAdapter(emojiAdapter);
    }

    private void handleChipSelection(int selectedChip) {
        for (int i = 0; i < chips.length; i++) {
            if (i == selectedChip) {
                // Selected chip styling
                chips[i].setChipBackgroundColorResource(R.color.purple_primary);
                chips[i].setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                chips[i].setTextColor(Color.WHITE);
            } else {
                // Reset other chips
                chips[i].setChipBackgroundColorResource(R.color.gray_primary);
                chips[i].setChipIconTint(ColorStateList.valueOf(Color.BLACK));
                chips[i].setTextColor(Color.BLACK);
            }
        }
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
                binding.expandablePhoto.imgSelected.setImageBitmap(bitmap);  // Set image to ImageView inside CardView
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                binding.expandablePhoto.imgSelected.setImageBitmap(bitmap);  // Set image to ImageView inside CardView
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    @Override
    public void onEmojiClick(int position) {
        // Enable save button and change its color
        binding.btnAdd.setEnabled(true);
        binding.btnAdd.setBackgroundColor(getResources().getColor(R.color.purple_primary));
        
        // Store selected emoji data
        EmojiModel selectedEmoji = emojiList.get(position);
        selectedMood = selectedEmoji.getName();
        selectedMoodIcon = selectedEmoji.getEmojiPath();
    }
}