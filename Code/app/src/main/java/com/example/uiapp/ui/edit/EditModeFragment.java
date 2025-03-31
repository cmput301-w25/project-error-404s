package com.example.uiapp.ui.edit;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.adapter.EmojiAdapter;
import com.example.uiapp.adapter.OnEmojiClickListener;
import com.example.uiapp.databinding.FragmentAddModelBinding;
import com.example.uiapp.databinding.FragmentEditModeBinding;
import com.example.uiapp.model.EmojiModel;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.ui.LocationHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
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
    private boolean isExpandedStatus = false;
    private Chip[] chips = new Chip[4];
    private Chip[] chipsStatus = new Chip[2];
    private int[] chipIds = {R.id.chip1, R.id.chip2, R.id.chip3, R.id.chip4};
    ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private CollectionReference moodEventsRef;
    StorageReference storageReference;
    MoodEntry previousMood;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    private LocationHelper locationHelper;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String currentLocation = "";
    private String locationLat = "";
    private String locationLng = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            previousMood = (MoodEntry) getArguments().getSerializable("mood");
        }

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
                                checkImageSize(url);
                            } catch (SecurityException e) {
                                Toast.makeText(requireContext(), "Permission error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        locationHelper = new LocationHelper(requireContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEditModeBinding.inflate(inflater, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        moodEventsRef = db.collection("MoodEvents");
        storageReference = FirebaseStorage.getInstance().getReference("Pictures");
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Updating mood...");
        progressDialog.setCancelable(false);

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

        binding.expandableStatus.chip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStatusChipSelection(0);
            }
        });

        binding.expandableStatus.chip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStatusChipSelection(1);
            }
        });
        chipsStatus[0] = binding.expandableStatus.chip1;
        chipsStatus[1] = binding.expandableStatus.chip2;

        recyclerViewEmojis = binding.recyclerView;
        emojiList = new ArrayList<>();
        setEmojiAdapter();
        setExpandedLayoutSection();

        if (previousMood != null){
            prefillPreviousMood();
        }

        binding.btnAdd.setOnClickListener(v -> {
            try {
                // Validate emoji selection
                if (emojiAdapter.getSelectedEmoji() == null) {
                    Toast.makeText(getContext(), "Please select a mood first", Toast.LENGTH_SHORT).show();
                    return;
                }

                String status = getStatusSelectedChipText();
                if (status.isEmpty()) {
                    Toast.makeText(getContext(), "Please select status first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (String.valueOf(selectedImageUri).equals(previousMood.getImageUrl())) {
                    updateMoodIntoFireStore(previousMood.getImageUrl());
                } else {
                    putImageIntoFirebaseStorage();
                }


            } catch (Exception e) {
                Log.e("AddModelFragment", "Error adding mood: " + e.getMessage());
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            Navigation.findNavController(this.getView()).navigateUp();

        });

        return binding.getRoot();
    }

    private void putImageIntoFirebaseStorage() {
        progressDialog.show();
        StorageReference imageRef = storageReference.child(selectedImageUri.getLastPathSegment());
        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUri = uri.toString();
                            updateMoodIntoFireStore(downloadUri);
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateMoodIntoFireStore(String downloadUri) {

        previousMood.setImageUrl(downloadUri);
        previousMood.setMood(emojiAdapter.getSelectedEmoji().getName());
        int moodIcon = emojiAdapter.getSelectedEmoji().getEmojiPath();
        previousMood.setMoodIcon(moodIcon);
        previousMood.setStatus(getStatusSelectedChipText());
        previousMood.setPeople(getSelectedChipText());
        previousMood.setNote(binding.expandableNote.editText.getText().toString());
        previousMood.setLocation(currentLocation);
        previousMood.setLocationLat(locationLat);
        previousMood.setLocationLng(locationLng);

        moodEventsRef.document(previousMood.getMoodId())
                .set(previousMood)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Mood updated successfully", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void prefillPreviousMood() {
        if (previousMood == null) return;
        selectedImageUri = Uri.parse(previousMood.getImageUrl());
        currentLocation = previousMood.getLocation();
        locationLat = previousMood.getLocationLat();
        locationLng = previousMood.getLocationLng();
        binding.expandableNote.editText.setText(previousMood.getNote());
        binding.expandableLocation.txtLocation.setText(previousMood.getLocation());
        selectedImageUri = Uri.parse(previousMood.getImageUrl());
        String dateTime = previousMood.getDateTime();
        try {
            String[] parts = dateTime.split(" \\| "); // Split at " | " (with spaces)

            if (parts.length == 2) {
                String date = parts[0]; // "Mar 27, 2025"
                String time = parts[1]; // "09:45"

                binding.date.setText(date);
                binding.tvTime.setText(time);
            }
        }catch (Exception e){

        }

        if (selectedImageUri != null) {
            binding.expandablePhoto.imgSelected.setVisibility(View.VISIBLE);
            binding.expandablePhoto.uploadPlaceholder.setVisibility(View.GONE);
            Glide.with(requireContext()).load(selectedImageUri).centerCrop().into(binding.expandablePhoto.imgSelected);
        }

        for (int i = 0; i < emojiList.size(); i++) {
            if (emojiList.get(i).getName().equals(previousMood.getMood())) {
                emojiAdapter.setSelectedEmoji(i);
                break;
            }
        }

        for (int i = 0; i < binding.expandablePeople.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.expandablePeople.chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(previousMood.getPeople())) {
                chip.setChecked(true);
                handleChipSelection(i);
            }
        }

        for (int i = 0; i < binding.expandableStatus.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.expandableStatus.chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(previousMood.getStatus())) {
                chip.setChecked(true);
                handleStatusChipSelection(i);
            }
        }
    }

    private void checkImageSize(Uri imageUri) {
        try {
            // Open an InputStream to get the file size
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            int fileSize = inputStream.available();
            inputStream.close();

            if (fileSize > 65536) {
                // Show toast and do not display the image
                Toast.makeText(requireContext(), "Image should be under 65536 bytes.", Toast.LENGTH_SHORT).show();
            } else {
                Glide.with(requireContext())
                        .load(imageUri)
                        .centerCrop()
                        .into(binding.expandablePhoto.imgSelected);
                selectedImageUri = imageUri;
                binding.expandablePhoto.imgSelected.setVisibility(View.VISIBLE);
                binding.expandablePhoto.uploadPlaceholder.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to check image size.", Toast.LENGTH_SHORT).show();
        }
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
            binding.expandablePhoto.imgSelected.setOnClickListener(v1 -> openGallery());
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

            binding.expandableLocation.btnGetLocation.setOnClickListener(v1 -> {
                if (checkLocationPermission()) {
                    getLocation();
                }
            });
        });
        binding.expandableStatus.headerLayout.setOnClickListener(v -> {
            isExpandedStatus = !isExpandedStatus;
            binding.expandableStatus.contentLayout.setVisibility(isExpandedStatus ? View.VISIBLE : View.GONE);
            binding.expandableStatus.arrowIcon.setRotation(isExpandedStatus ? 180 : 0);
        });
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
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
            public void onLocationResult(String address, String latitude, String longitude) {
                currentLocation = address;
                locationLat = latitude;
                locationLng = longitude;
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
            } else {
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
        recyclerViewEmojis.setLayoutManager(new GridLayoutManager(getContext(), 4));
        emojiAdapter = new EmojiAdapter( emojiList,this::onEmojiClick);
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

    private void handleStatusChipSelection(int selectedChip) {
        for (int i = 0; i < chipsStatus.length; i++) {
            if (i == selectedChip) {
                // Selected chip styling
                chipsStatus[i].setChipBackgroundColorResource(R.color.purple_primary);
                chipsStatus[i].setChipIconTint(ColorStateList.valueOf(Color.WHITE));
                chipsStatus[i].setTextColor(Color.WHITE);
            } else {
                // Reset other chips
                chipsStatus[i].setChipBackgroundColorResource(R.color.gray_primary);
                chipsStatus[i].setChipIconTint(ColorStateList.valueOf(Color.BLACK));
                chipsStatus[i].setTextColor(Color.BLACK);
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

    private String getStatusSelectedChipText() {
        for (Chip chip : chipsStatus) {
            if (chip.isChecked()) {
                return chip.getText().toString();
            }
        }
        return "";
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
        binding.btnAdd.setEnabled(true);
        binding.btnAdd.setBackgroundColor(getResources().getColor(R.color.purple_primary));

    }
}