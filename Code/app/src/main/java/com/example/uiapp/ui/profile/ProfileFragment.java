package com.example.uiapp.ui.profile;

import static android.app.Activity.RESULT_OK;
import static com.example.uiapp.MainActivity.createInitialsBitmap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uiapp.R;
import com.example.uiapp.adapter.MoodAdapter;
import com.example.uiapp.databinding.FragmentNotificationsBinding;
import com.example.uiapp.databinding.FragmentProfileBinding;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.ui.home.MoodViewModel;
import com.example.uiapp.utils.HelperClass;
import com.example.uiapp.utils.OnItemEntryClick;
import com.example.uiapp.utils.SharedPrefHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements OnItemEntryClick {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private MoodAdapter myMoodsAdapter;
    String from = "";
    UserModel userModel;
    ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    StorageReference storageReference;
    ArrayList<MoodEntry> listOfEntries = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            from = getArguments().getString("from");
            userModel = (UserModel) getArguments().getSerializable("user");
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
                                putImageIntoFirebaseStorage(url);
                            } catch (SecurityException e) {
                                Toast.makeText(requireContext(), "Permission error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void putImageIntoFirebaseStorage(Uri uri) {
        showProgressDialog();
        Glide.with(requireContext())
                .load(uri)
                .centerCrop()
                .into(binding.imgUser);
        StorageReference imageRef = storageReference.child(uri.getLastPathSegment());
        imageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri1 -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(userModel.getUsername())
                                    .update("userImage", uri1.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(requireContext(), "Profile image updated!", Toast.LENGTH_SHORT).show();
                                        userModel.setUserImage(uri1.toString());
                                        HelperClass.users.setUserImage(uri1.toString());
                                        SharedPrefHelper.saveUser(requireContext(), HelperClass.users);
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(requireContext(), "Failed to update Firestore: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    });
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);

        storageReference = FirebaseStorage.getInstance().getReference("Pictures");
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileViewModel.getMoodEntries().observe(getViewLifecycleOwner(), moodEntries -> {
            listOfEntries.clear();
            listOfEntries.addAll(moodEntries);
            toggleVisibility(moodEntries, binding.rvMoods, binding.tvNoMyDataFound);
            binding.txtNotificationCount.setText(String.valueOf(moodEntries.size()));
            if (myMoodsAdapter != null) {
                myMoodsAdapter.updateList(moodEntries);
            }
            binding.tvPosts.setText(String.valueOf(moodEntries.size()));
            hideProgressDialog();
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        binding.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!from.equals("otherUserProfile")) {
                    openGallery();
                }
            }
        });

        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefHelper.clearUser(requireContext());
                HelperClass.users = null;
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        if (from.equals("myUserProfile")) {
            showProgressDialog();
            handleUserData();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        if (from.equals("otherUserProfile")) {
            showProgressDialog();
            handleUserData();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleUserData() {
        binding.rvMoods.setLayoutManager(new LinearLayoutManager(getContext()));
        if (from.equals("otherUserProfile")) {
            binding.tvLogout.setVisibility(View.GONE);
            myMoodsAdapter = new MoodAdapter("home", getContext(), new ArrayList<>(), null, null, this);
            binding.rvMoods.setAdapter(myMoodsAdapter);
            if (HelperClass.users.getFollowing().contains(userModel.getUsername())) {
                binding.cvMyPosts.setVisibility(View.VISIBLE);
                binding.cvOtherPosts.setVisibility(View.GONE);
            } else {
                binding.cvOtherPosts.setVisibility(View.VISIBLE);
                binding.cvMyPosts.setVisibility(View.GONE);
            }
            binding.tvLabel.setText(userModel.getUsername());
            setUserDetails(userModel);
        } else {
            binding.tvLogout.setVisibility(View.VISIBLE);
            myMoodsAdapter = new MoodAdapter("history", getContext(), new ArrayList<>(), null, null, this);
            myMoodsAdapter.updateList(new ArrayList<>());
            binding.rvMoods.setAdapter(myMoodsAdapter);
            binding.cvMyPosts.setVisibility(View.VISIBLE);
            binding.cvOtherPosts.setVisibility(View.GONE);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(userModel.getUsername())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            UserModel user = documentSnapshot.toObject(UserModel.class);
                            if (user != null) {
                                setUserDetails(userModel);
                            }
                        }
                    });
        }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUserDetails(UserModel userModel) {
        binding.tvUsername.setText(userModel.getUsername());
        binding.tvFollowers.setText(String.valueOf(userModel.getFollowers().size()));
        binding.tvFollowings.setText(String.valueOf(userModel.getFollowing().size()));
        if (userModel.getUserImage() != null && !userModel.getUserImage().isEmpty()) {
            Glide.with(requireActivity())
                    .load(userModel.getUserImage())
                    .centerCrop()
                    .into(binding.imgUser);
        } else {
            Bitmap initialsBitmap = createInitialsBitmap(userModel.getUsername(), 200);
            binding.imgUser.setImageBitmap(initialsBitmap);
        }
        profileViewModel.fetchMoodEvents(from, userModel.getUsername());
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
    public void onItemClick(int position) {
        if (position >= 0 && position < listOfEntries.size()) {
            MoodEntry entry = listOfEntries.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", entry);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_profileFragment_to_modeDetailsFragment, bundle);
        }
    }
}