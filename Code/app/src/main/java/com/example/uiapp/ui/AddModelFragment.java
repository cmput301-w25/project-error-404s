package com.example.uiapp.ui;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import android.Manifest;
import com.example.uiapp.adapter.EmojiAdapter;
import com.example.uiapp.adapter.OnEmojiClickListener;
import com.example.uiapp.model.EmojiModel;
import com.example.uiapp.model.MoodEntry;

import com.example.uiapp.ui.home.MoodViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static android.app.Activity.RESULT_OK;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.uiapp.R;
import com.example.uiapp.databinding.FragmentAddModelBinding;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddModelFragment extends Fragment implements OnEmojiClickListener {

    private MoodViewModel moodViewModel;
    FragmentAddModelBinding binding;
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

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private boolean hasSelectedImage = false;

    /// ///////////////////////////////
    private LocationHelper locationHelper;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String currentLocation = "";

    // Firebase instance and collection reference
    private FirebaseFirestore db;
    private CollectionReference moodEventsRef;

    @SuppressLint("SetTextI18n")

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddModelBinding.inflate(inflater, container, false);

        // Initialize Firebase Firestore and reference the "MoodEvents" collection
        db = FirebaseFirestore.getInstance();
        moodEventsRef = db.collection("MoodEvents");

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


        binding.date.setText("Today, " + currentDateTime);
        binding.tvTime.setText(currentTime);
        recyclerViewEmojis = binding.recyclerView;
        emojiList = new ArrayList<>();
        setEmojiAdapter();
        setExpandedLayoutSection();

        // Initialize ViewModel
        moodViewModel = new ViewModelProvider(requireActivity()).get(MoodViewModel.class);

        // ====================================================================================
        // ADDING MOOD
        binding.btnAdd.setOnClickListener(v -> {

            String dateTime = binding.date.getText().toString() + " | " + binding.tvTime.getText().toString();
            String mood = emojiAdapter.getSelectedEmoji().getName(); // Get selected emoji

            TextInputEditText editText = binding.expandableNote.editText;
            String note = editText.getText().toString();

            String people = getSelectedChipText(); // Getting text from 4 options
            int moodIcon = emojiAdapter.getSelectedEmoji().getEmojiPath();
            String imageUri = (selectedImageUri != null) ? selectedImageUri.toString() : "";

            // Creating a new MoodEntry object
            MoodEntry moodEntry = new MoodEntry(dateTime, mood, note, people, currentLocation, moodIcon, imageUri);

            moodViewModel.addMoodEntry(moodEntry);

            Navigation.findNavController(v).navigateUp();

            Toast.makeText(getContext(), "Mood Added", Toast.LENGTH_SHORT).show();
        });
        // add to firebase when the add button is clicked:
        binding.btnAdd.setOnClickListener(v -> {
            // Gather data from your UI elements
            String dateTime = binding.date.getText().toString();
            String mood = emojiAdapter.toString();
            // TODO: change this to the connecting variable
            String note = MoodEntry.getNote(); // Collect from your UI
            String people = MoodEntry.getPeople(); // Example value
            String location = MoodEntry.getLocation(); // Example value
            int moodIcon = R.drawable.happy; // Example drawable
            int imageUrl = 0; // or a valid image resource id
            MoodEntry newEvent = new MoodEntry(dateTime, mood, note, people, location, moodIcon, selectedImageUri.toString(), false);

            // Add the mood event to Firestore
            moodEventsRef.add(newEvent)
                    .addOnSuccessListener(documentReference -> {
                        //set the Firestore-generated ID in your model
                        newEvent.setFirestoreId(documentReference.getId());
                        Toast.makeText(getContext(), "Mood event added successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error adding mood event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        return binding.getRoot();
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

            binding.expandableLocation.btnGetLocation.setOnClickListener(v1 -> {
                if (checkLocationPermission()) {
                    getLocation();
                }
            });
        });
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return false;
        }
    }

    private void getLocation() {
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(String address) {
                currentLocation = address;
                binding.expandableLocation.txtLocation.setText(address);
                Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
            else {
                Toast.makeText(getContext(), "Not able to get location permission...", Toast.LENGTH_SHORT).show();
            }
        }
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

        recyclerViewEmojis.setLayoutManager(new GridLayoutManager(getContext(), 4)); // 4 columns

        emojiAdapter = new EmojiAdapter(emojiList, this::onEmojiClick);
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

    private String getSelectedChipText() {
        for (Chip chip : chips) {
            if (chip.isChecked()) {
                return chip.getText().toString();
            }
        }
        return "";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Register the activity result launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                       Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {

                                requireContext().getContentResolver().takePersistableUriPermission(
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );

                                binding.expandablePhoto.imgSelected.setVisibility(View.VISIBLE);
                                binding.expandablePhoto.uploadPlaceholder.setVisibility(View.GONE);
                                Glide.with(requireContext())
                                        .load(uri)
                                        .centerCrop()
                                        .into(binding.expandablePhoto.imgSelected);
                                selectedImageUri = uri;

                            } catch (SecurityException e) {
                                Toast.makeText(requireContext(), "Permission error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        locationHelper = new LocationHelper(requireContext());
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



//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            selectedImageUri = data.getData();
//            binding.expandablePhoto.imgSelected.setImageURI(selectedImageUri);
//        }
//
//
//    }


        @Override
        public void onEmojiClick ( int position){
            binding.btnAdd.setEnabled(true);
            binding.btnAdd.setBackgroundColor(getResources().getColor(R.color.purple_primary));

        }
    }
