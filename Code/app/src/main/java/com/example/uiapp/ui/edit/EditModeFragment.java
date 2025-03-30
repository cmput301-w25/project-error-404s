package com.example.uiapp.ui.edit;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.uiapp.R;
import com.example.uiapp.adapter.EmojiAdapter;
import com.example.uiapp.adapter.OnEmojiClickListener;
import com.example.uiapp.databinding.FragmentEditModeBinding;
import com.example.uiapp.model.EmojiModel;
import com.example.uiapp.model.MoodEntry;
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
    private static final String TAG = "EditModeFragment";
    public FragmentEditModeBinding binding;
    List<EmojiModel> emojiList;
    EmojiAdapter emojiAdapter;
    RecyclerView recyclerViewEmojis;
    private boolean isExpanded = false;
    private boolean isExpandedPhoto = false;
    private boolean isExpandedPeople = false;
    private boolean isExpandedLocation = false;
    private static final int PICK_IMAGE_REQUEST = 1;
    private final Chip[] chips = new Chip[4];
    private final int[] chipIds = {R.id.chip1, R.id.chip2, R.id.chip3, R.id.chip4};
    // Firebase instance
    private FirebaseFirestore db;
    private String selectedMood = "";
    private Uri selectedImageUri;
    // For pre-selection of emoji
    private int preSelectedEmojiPosition = -1;
    // Current MoodEntry being edited
    private MoodEntry currentMoodEntry;

    /**
     * Creates and initializes the edit mode fragment view.
     * Loads the existing mood data if available and populates the UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        binding = FragmentEditModeBinding.inflate(inflater, container, false);
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
            }
        });
        binding.expandablePeople.chip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(1);
            }
        });
        binding.expandablePeople.chip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(2);
            }
        });
        binding.expandablePeople.chip4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChipSelection(3);
            }
        });
        chips[0] = binding.expandablePeople.chip1;
        chips[1] = binding.expandablePeople.chip2;
        chips[2] = binding.expandablePeople.chip3;
        chips[3] = binding.expandablePeople.chip4;

        // Get the MoodEntry from arguments
        if (getArguments() != null) {
            try{currentMoodEntry = (MoodEntry) getArguments().getSerializable("moodEntry");
            } catch (ClassCastException e) {
                Log.e(TAG, "Error casting MoodEntry");
                
            }
            // If we have a MoodEntry, pre-populate fields
            if (currentMoodEntry != null) {
                // Set date and time from current mood entry
                if (currentMoodEntry.getDateTime() != null) {
                    String[] dateTimeParts = currentMoodEntry.getDateTime().split("\\|");
                    if (dateTimeParts.length >= 2) {
                        binding.date.setText(dateTimeParts[0].trim());
                        binding.tvTime.setText(dateTimeParts[1].trim());
                    }
                }
                
                // Set note if available
                if (currentMoodEntry.getNote() != null) {
                    binding.expandableNote.editText.setText(currentMoodEntry.getNote());
                    isExpanded = true;
                    binding.expandableNote.contentLayout.setVisibility(View.VISIBLE);
                    binding.expandableNote.arrowIcon.setRotation(180);
                }
                
                // Pre-select mood if available
                if (currentMoodEntry.getMood() != null) {
                    selectedMood = currentMoodEntry.getMood();
                    // Will find position in setEmojiAdapter
                }
                
                // Set up people selection if available
                String people = currentMoodEntry.getPeople();
                if (people != null && !people.isEmpty()) {
                    Log.d(TAG, "People value from MoodEntry: " + people);
                    isExpandedPeople = true;
                    binding.expandablePeople.contentLayout.setVisibility(View.VISIBLE);
                    binding.expandablePeople.arrowIcon.setRotation(180);
                    
                    // Pre-select the chip based on saved people information
                    boolean chipFound = false;
                    for (int i = 0; i < chips.length; i++) {
                        String chipText = chips[i].getText().toString();
                        Log.d(TAG, "Chip " + i + " text: '" + chipText + "'");
                        if (chipText.equals(people)) {
                            Log.d(TAG, "Found matching chip at position: " + i);
                            handleChipSelection(i);
                            chipFound = true;
                            break;
                        }
                    }
                    
                    if (!chipFound) {
                        Log.d(TAG, "No matching chip found for people: " + people);
                        // Try case-insensitive match as fallback
                        for (int i = 0; i < chips.length; i++) {
                            String chipText = chips[i].getText().toString();
                            if (chipText.equalsIgnoreCase(people)) {
                                Log.d(TAG, "Found case-insensitive match at position: " + i);
                                handleChipSelection(i);
                                chipFound = true;
                                break;
                            }
                        }
                    }
                }
                
                // Set location if available
                if (currentMoodEntry.getLocation() != null && !currentMoodEntry.getLocation().isEmpty()) {
                    isExpandedLocation = true;
                    binding.expandableLocation.contentLayout.setVisibility(View.VISIBLE);
                    binding.expandableLocation.arrowIcon.setRotation(180);
                    binding.expandableLocation.txtLocation.setText(currentMoodEntry.getLocation());
                }
                
                // Set image if available
                if (currentMoodEntry.getImageUrl() != null && !currentMoodEntry.getImageUrl().isEmpty()) {
                    isExpandedPhoto = true;
                    binding.expandablePhoto.contentLayout.setVisibility(View.VISIBLE);
                    binding.expandablePhoto.arrowIcon.setRotation(180);
                    
                    // Try to load the image
                    try {
                        selectedImageUri = Uri.parse(currentMoodEntry.getImageUrl());
                        binding.expandablePhoto.imgSelected.setImageURI(selectedImageUri);
                        binding.expandablePhoto.imgSelected.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading image: " + e.getMessage());
                    }
                }
            } else {
                // If no mood entry data available, show default date/time
                binding.date.setText("Today, " + currentDateTime);
                binding.tvTime.setText(currentTime);
            }
        } else {
            // If no arguments provided, show default date/time
            binding.date.setText("Today, " + currentDateTime);
            binding.tvTime.setText(currentTime);
        }

        recyclerViewEmojis = binding.recyclerView;
        emojiList = new ArrayList<>();
        setupEmojiList();
        setEmojiAdapter();
        setExpandedLayoutSection();
        
        binding.btnAdd.setOnClickListener(v -> {
            // Check if a mood is selected
            if (selectedMood.isEmpty()) {
                Toast.makeText(getContext(), "Please select a mood", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get updated data from UI
            String updatedMood = selectedMood;
            String updatedNote = binding.expandableNote.editText.getText().toString();
            
            // Get people information
            String people = getSelectedChipText();
            
            // Get document ID from arguments
            String documentId = getArguments().getString("documentId");
            
            // Get current user ID
            String userId = requireContext().getSharedPreferences("MoodPulsePrefs", MODE_PRIVATE)
                    .getString("USERNAME", null);
            
            if (documentId != null && !documentId.isEmpty() && userId != null) {
                // Prepare update data
                db.collection("users").document(userId).collection("moods")
                        .document(documentId)
                        .update(
                            "mood", updatedMood, 
                            "note", updatedNote,
                            "people", people,
                            "location", binding.expandableLocation.txtLocation.getText().toString(),
                            "imageUrl", selectedImageUri != null ? selectedImageUri.toString() : "",
                            "moodIcon", getEmojiIconForMood(updatedMood)
                        )
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Mood event updated successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigateUp();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error updating mood event", e);
                        });
            } else {
                Toast.makeText(getContext(), "Cannot update event: missing document ID.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            Navigation.findNavController(this.getView()).navigateUp();
        });

        return binding.getRoot();
    }

    /**
     * Retrieves the text from the currently selected people chip.
     * @return String containing the selected chip text or empty string if no chip is selected
     */
    private String getSelectedChipText() {
        for (int i = 0; i < chips.length; i++) {
            if (chips[i].getChipBackgroundColor() != null && 
                chips[i].getChipBackgroundColor().getDefaultColor() == getResources().getColor(R.color.purple_primary)) {
                return chips[i].getText().toString();
            }
        }
        return "";
    }

    /**
     * Sets up the expanded layout sections for the edit mode fragment.
     * Adds click listeners to the header layouts to toggle the visibility of the corresponding content sections.
     */ 
    private void setExpandedLayoutSection() {
        binding.expandableNote.headerLayout.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            binding.expandableNote.contentLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            binding.expandableNote.arrowIcon.setRotation(isExpanded ? 180 : 0);
        });
        binding.expandablePhoto.headerLayout.setOnClickListener(v -> {
            isExpandedPhoto = !isExpandedPhoto;
            binding.expandablePhoto.contentLayout.setVisibility(isExpandedPhoto ? View.VISIBLE : View.GONE);
            binding.expandablePhoto.arrowIcon.setRotation(isExpandedPhoto ? 180 : 0);
            binding.expandablePhoto.imageButton.setOnClickListener(v1 -> openGallery());
        });
        binding.expandablePeople.headerLayout.setOnClickListener(v -> {
            isExpandedPeople = !isExpandedPeople;
            binding.expandablePeople.contentLayout.setVisibility(isExpandedPeople ? View.VISIBLE : View.GONE);
            binding.expandablePeople.arrowIcon.setRotation(isExpandedPeople ? 180 : 0);
        });
        binding.expandableLocation.headerLayout.setOnClickListener(v -> {
            isExpandedLocation = !isExpandedLocation;
            binding.expandableLocation.contentLayout.setVisibility(isExpandedLocation ? View.VISIBLE : View.GONE);
            binding.expandableLocation.arrowIcon.setRotation(isExpandedLocation ? 180 : 0);
        });
    }

    /**
     * Sets up the emoji list for the edit mode fragment.
     * Adds all possible emoji models to the list.
     */
    private void setupEmojiList() {
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
        
        // If we have a selected mood, find its position in the list
        if (!selectedMood.isEmpty()) {
            for (int i = 0; i < emojiList.size(); i++) {
                if (emojiList.get(i).getName().equals(selectedMood)) {
                    preSelectedEmojiPosition = i;
                    break;
                }
            }
        }
    }

    /**
     * Sets up the emoji adapter for the edit mode fragment.
     * Adds the emoji adapter to the recycler view.
     */ 
    private void setEmojiAdapter() {
        recyclerViewEmojis.setLayoutManager(new GridLayoutManager(getContext(), 4));
        emojiAdapter = new EmojiAdapter(emojiList, this);
        recyclerViewEmojis.setAdapter(emojiAdapter);
        
        // If we have a pre-selected emoji position, set it as selected
        if (preSelectedEmojiPosition != -1) {
            // Update the adapter to show the selected item
            if (emojiAdapter != null) {
                // For custom adapter implementation, you might need to add setSelectedPosition method
                emojiAdapter.setSelectedPosition(preSelectedEmojiPosition);
                
                // Either way, enable the save button since we have a pre-selected emoji
                binding.btnAdd.setEnabled(true);
                binding.btnAdd.setBackgroundColor(getResources().getColor(R.color.purple_primary));
                
                // Scroll to the selected emoji position
                recyclerViewEmojis.post(() -> {
                    if (preSelectedEmojiPosition < emojiAdapter.getItemCount()) {
                        recyclerViewEmojis.scrollToPosition(preSelectedEmojiPosition);
                    }
                });
            }
        }
    }

    /**
     * Handles the selection of a people chip.
     * Updates the selected chip and resets other chips.
     * @param selectedChip The index of the selected chip
     */ 
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

    /**
     * Opens the gallery to select an image.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Gets the file size in bytes for a given URI
     */
    private long getFileSize(Uri uri) {
        try {
            return requireContext().getContentResolver()
                    .openAssetFileDescriptor(uri, "r")
                    .getLength();
        } catch (Exception e) {
            Log.e(TAG, "Error getting file size: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Handles the result of the gallery activity.
     * Checks the file size and sets the selected image.
     */ 
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                // Check file size - limit to 64KB
                long fileSize = getFileSize(imageUri);
                if (fileSize > 65536) { // 64KB limit
                    Toast.makeText(requireContext(), 
                        "Image too large! Maximum size is 64KB. Current size: " + (fileSize / 1024) + "KB", 
                        Toast.LENGTH_LONG).show();
                    return;
                }
                
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                binding.expandablePhoto.imgSelected.setImageBitmap(bitmap);  // Set image to ImageView inside CardView
                binding.expandablePhoto.imgSelected.setVisibility(View.VISIBLE);
                selectedImageUri = imageUri;
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

    /**
     * Handles the selection of an emoji.
     * Updates the selected mood and enables the save button.
     * @param position The index of the selected emoji
     */
    @Override
    public void onEmojiClick(int position) {
        // Need to use getName() method instead of getEmojiName()
        selectedMood = emojiList.get(position).getName();
        binding.btnAdd.setEnabled(true);
        binding.btnAdd.setBackgroundColor(getResources().getColor(R.color.purple_primary));
    }
    /**
     * Gets the emoji icon for a given mood.
     * @param mood The mood to get the emoji icon for
     * @return The emoji icon path
     */
    private int getEmojiIconForMood(String mood) {
        // iterate through emojiList to find matching mood
        for (EmojiModel emoji : emojiList) {
            if (emoji.getName().equals(mood)) {
                return emoji.getEmojiPath();
            }
        }
        //  if no matching mood, return default emoji
        return R.drawable.happy; //  default emoji
    }
}