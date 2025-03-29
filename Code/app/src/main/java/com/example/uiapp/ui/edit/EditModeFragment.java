package com.example.uiapp.ui.edit;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.adapter.EmojiAdapter;
import com.example.uiapp.adapter.OnEmojiClickListener;
import com.example.uiapp.databinding.FragmentEditModeBinding;
import com.example.uiapp.model.EmojiModel;
import com.example.uiapp.model.MoodEntry;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
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
    private Chip[] chips = new Chip[4];
    private int[] chipIds = {R.id.chip1, R.id.chip2, R.id.chip3, R.id.chip4};
    
    // Added for image handling
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private boolean hasSelectedImage = false;
    
    // Firebase instance
    private FirebaseFirestore db;
    private CollectionReference moodEventsRef;
    
    // Current MoodEntry being edited
    private MoodEntry currentMoodEntry;
    
    // Current mood for pre-selection
    private int preSelectedEmojiPosition = RecyclerView.NO_POSITION;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the activity result launcher for image picking
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri url = result.getData().getData();
                        if (url != null) {
                            try {
                                requireContext().getContentResolver().takePersistableUriPermission(
                                        url,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );

                                binding.expandablePhoto.imgSelected.setVisibility(View.VISIBLE);
                                binding.expandablePhoto.uploadPlaceholder.setVisibility(View.GONE);
                                Glide.with(requireContext())
                                        .load(url)
                                        .centerCrop()
                                        .into(binding.expandablePhoto.imgSelected);
                                selectedImageUri = url;
                                hasSelectedImage = true;

                            } catch (SecurityException e) {
                                Toast.makeText(requireContext(), "Permission error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        moodEventsRef = db.collection("MoodEvents");

        // Inflate the layout for this fragment
        binding = FragmentEditModeBinding.inflate(inflater, container, false);
        
        // Get the MoodEntry object passed from HistoryFragment
        if (getArguments() != null) {
            currentMoodEntry = (MoodEntry) getArguments().getSerializable("moodEntry");
            if (currentMoodEntry == null) {
                Log.e(TAG, "No mood entry data received");
                Toast.makeText(getContext(), "Error: No mood data available", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_bottom_nav).navigateUp();
                return binding.getRoot();
            }
        } else {
            Log.e(TAG, "Arguments bundle is null");
            Toast.makeText(getContext(), "Error: No mood data available", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_bottom_nav).navigateUp();
            return binding.getRoot();
        }
        
        // Set up date and time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        String currentTime = time.format(new Date().getTime());
        
        // Initialize chips
        binding.expandablePeople.chip1.setOnClickListener(v -> handleChipSelection(0));
        binding.expandablePeople.chip2.setOnClickListener(v -> handleChipSelection(1));
        binding.expandablePeople.chip3.setOnClickListener(v -> handleChipSelection(2));
        binding.expandablePeople.chip4.setOnClickListener(v -> handleChipSelection(3));
        
        chips[0] = binding.expandablePeople.chip1;
        chips[1] = binding.expandablePeople.chip2;
        chips[2] = binding.expandablePeople.chip3;
        chips[3] = binding.expandablePeople.chip4;

        // Fill in existing data
        binding.date.setText(currentMoodEntry.getDateTime().split("\\|")[0].trim());
        binding.tvTime.setText(currentMoodEntry.getDateTime().split("\\|")[1].trim());
        
        // Set up note if available
        if (currentMoodEntry.getNote() != null && !currentMoodEntry.getNote().isEmpty()) {
            binding.expandableNote.editText.setText(currentMoodEntry.getNote());
            binding.expandableNote.contentLayout.setVisibility(View.VISIBLE);
            isExpanded = true;
            binding.expandableNote.arrowIcon.setRotation(180);
        }
        
        // Set up photo if available
        if (currentMoodEntry.getImageUrl() != null && !currentMoodEntry.getImageUrl().isEmpty()) {
            selectedImageUri = Uri.parse(currentMoodEntry.getImageUrl());
            binding.expandablePhoto.imgSelected.setVisibility(View.VISIBLE);
            binding.expandablePhoto.uploadPlaceholder.setVisibility(View.GONE);
            Glide.with(requireContext())
                    .load(selectedImageUri)
                    .centerCrop()
                    .into(binding.expandablePhoto.imgSelected);
            hasSelectedImage = true;
        }
        
        // Set up people selection if available
        String people = currentMoodEntry.getPeople();
        if (people != null && !people.isEmpty()) {
            for (int i = 0; i < chips.length; i++) {
                if (chips[i].getText().toString().equals(people)) {
                    handleChipSelection(i);
                    binding.expandablePeople.contentLayout.setVisibility(View.VISIBLE);
                    isExpandedPeople = true;
                    binding.expandablePeople.arrowIcon.setRotation(180);
                    break;
                }
            }
        }
        
        // Set up location if available
        if (currentMoodEntry.getLocation() != null && !currentMoodEntry.getLocation().isEmpty()) {
            binding.expandableLocation.contentLayout.setVisibility(View.VISIBLE);
            isExpandedLocation = true;
            binding.expandableLocation.arrowIcon.setRotation(180);
        }

        // Find the position of the current mood in our emoji list before creating adapter
        recyclerViewEmojis = binding.recyclerView;
        emojiList = new ArrayList<>();
        setupEmojiList();
        
        // If we have a current mood, find its position
        if (currentMoodEntry != null && currentMoodEntry.getMood() != null) {
            String currentMood = currentMoodEntry.getMood();
            for (int i = 0; i < emojiList.size(); i++) {
                if (emojiList.get(i).getName().equals(currentMood)) {
                    preSelectedEmojiPosition = i;
                    break;
                }
            }
        }
        
        setEmojiAdapter();
        setExpandedLayoutSection();
        
        // Set up save button
        binding.btnAdd.setText(R.string.edit); // Make sure the button says "Edit"
        binding.btnAdd.setOnClickListener(v -> updateMoodEvent());

        // Set up back button
        binding.btnBack.setOnClickListener(v -> 
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_bottom_nav).navigateUp()
        );

        return binding.getRoot();
    }

    private void updateMoodEvent() {
        try {
            if (currentMoodEntry == null || currentMoodEntry.getFirestoreId() == null) {
                Toast.makeText(getContext(), "Cannot update event: missing document ID.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get data from UI fields
            String dateTime = binding.date.getText().toString() + " | " + binding.tvTime.getText().toString();
            
            // Get mood from selected emoji (if any was selected, otherwise keep original)
            String mood = currentMoodEntry.getMood();
            int moodIcon = currentMoodEntry.getMoodIcon();
            if (emojiAdapter.getSelectedEmoji() != null) {
                mood = emojiAdapter.getSelectedEmoji().getName();
                moodIcon = emojiAdapter.getSelectedEmoji().getEmojiPath();
            }
            
            // Get note text
            String note = "";
            TextInputEditText editText = binding.expandableNote.editText;
            if (editText != null && editText.getText() != null) {
                note = editText.getText().toString();
            }
            
            // Get selected people
            String people = getSelectedChipText();
            
            // Get image URI
            String imageUri = (selectedImageUri != null) ? selectedImageUri.toString() : "";
            
            // Get location (keeping original if not changed)
            String location = currentMoodEntry.getLocation();
            
            // Update the event in Firestore
            moodEventsRef.document(currentMoodEntry.getFirestoreId())
                    .update(
                            "dateTime", dateTime,
                            "mood", mood,
                            "note", note,
                            "people", people,
                            "location", location,
                            "moodIcon", moodIcon,
                            "imageUrl", imageUri
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Mood event updated successfully!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_bottom_nav).navigateUp();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error updating event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating mood event", e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in updateMoodEvent", e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedChipText() {
        for (int i = 0; i < chips.length; i++) {
            if (chips[i].getChipBackgroundColor() != null && 
                chips[i].getChipBackgroundColor().getDefaultColor() == getResources().getColor(R.color.purple_primary)) {
                return chips[i].getText().toString();
            }
        }
        return "";
    }

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

    // Separated emoji list setup for clarity
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
    }

    private void setEmojiAdapter() {
        recyclerViewEmojis.setLayoutManager(new GridLayoutManager(getContext(), 4));
        emojiAdapter = new EmojiAdapter(emojiList, this);
        recyclerViewEmojis.setAdapter(emojiAdapter);
        
        // Set the pre-selected emoji if available
        if (preSelectedEmojiPosition != RecyclerView.NO_POSITION) {
            // Use the adapter's method to set the selection
            emojiAdapter.setSelectedPosition(preSelectedEmojiPosition);
            
            // Post a delayed action to scroll to the selected emoji
            recyclerViewEmojis.post(() -> {
                if (preSelectedEmojiPosition < emojiAdapter.getItemCount()) {
                    recyclerViewEmojis.scrollToPosition(preSelectedEmojiPosition);
                }
            });
            
            // Enable the save button since we have a selected emoji
            binding.btnAdd.setEnabled(true);
            binding.btnAdd.setBackgroundColor(getResources().getColor(R.color.purple_primary));
        }
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Allow any image type
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Verify at least one app can handle this
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            imagePickerLauncher.launch(intent);
        } else {
            Toast.makeText(requireContext(), "No file picker available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEmojiClick(int position) {
        // When user clicks an emoji, update the adapter's selection
        if (emojiAdapter != null) {
            emojiAdapter.setSelectedPosition(position);
        }
        
        // Enable the save button
        binding.btnAdd.setEnabled(true);
        binding.btnAdd.setBackgroundColor(getResources().getColor(R.color.purple_primary));
    }
}