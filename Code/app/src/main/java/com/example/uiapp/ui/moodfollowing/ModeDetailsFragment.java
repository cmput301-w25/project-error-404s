package com.example.uiapp.ui.moodfollowing;

import static com.example.uiapp.MainActivity.createInitialsBitmap;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.uiapp.adapter.CommentsAdapter;
import com.example.uiapp.databinding.FragmentModeDetailsBinding;
import com.example.uiapp.model.CommentsModel;
import com.example.uiapp.model.EmojiHelper;
import com.example.uiapp.model.MoodEntry;
import com.example.uiapp.model.UserModel;
import com.example.uiapp.utils.HelperClass;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class ModeDetailsFragment extends Fragment {
    private FragmentModeDetailsBinding binding;
    MoodEntry entry;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference moodEventsRef = db.collection("MoodEvents");
    private CommentsAdapter adapter;
    List<CommentsModel> list = new ArrayList<>();

    public ModeDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entry = (MoodEntry) getArguments().getSerializable("data");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentModeDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * This function sets up the fragments to view the mood details, including the date and time, mood, note,
     * people status, username, and location
     * -----------------------------------------------------------------------------------------------------
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.txtDateTime.setText(entry.getDateTime());
        binding.txtMood.setText(entry.getMood());
        binding.txtNote.setText(entry.getNote());
        binding.txtPeople.setText(entry.getPeople());
        binding.txtUsername.setText(entry.getUsername());
        binding.txtLocation.setText(entry.getLocation());
        binding.imgMood.setImageResource(entry.getMoodIcon());;

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        if (entry.getNote().isEmpty()) {
            binding.llNote.setVisibility(View.GONE);
        } else {
            binding.llNote.setVisibility(View.VISIBLE);
        }
        if (entry.getPeople().isEmpty()) {
            binding.llPeople.setVisibility(View.GONE);
        } else {
            binding.llPeople.setVisibility(View.VISIBLE);
        }
        if (entry.getLocation().isEmpty()) {
            binding.llLocation.setVisibility(View.GONE);
        } else {
            binding.llLocation.setVisibility(View.VISIBLE);
        }
        if (!entry.getImageUrl().isEmpty()) {
            binding.centerImg.setVisibility(View.VISIBLE);
            Glide.with(requireActivity())
                    .load(entry.getImageUrl())
                    .centerCrop()
                    .into(binding.imgMoodEntry);
        } else {
            binding.centerImg.setVisibility(View.GONE);
        }

        Bitmap initialsBitmap = createInitialsBitmap(entry.getUsername(), 200);
        binding.imgUser.setImageBitmap(initialsBitmap);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(entry.getUsername())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        if (user != null) {
                            if (user.getUserImage() != null && !user.getUserImage().isEmpty()) {
                                Glide.with(requireContext())
                                        .load(user.getUserImage())
                                        .centerCrop()
                                        .into(binding.imgUser);
                            }
                        }
                    }
                });

        getComments();

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
    }

    private void addComment() {
        String text = binding.editTextUsername.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String commentId = moodEventsRef.document(entry.getMoodId())
                .collection("comments").document().getId(); // Auto-generate ID

        CommentsModel comment = new CommentsModel(
                commentId,
                HelperClass.users.getUserImage(),
                HelperClass.users.getUsername(),
                "",
                text,
                entry.getMoodId()
        );
        moodEventsRef.document(entry.getMoodId()).collection("comments").document(commentId).set(comment)
                .addOnSuccessListener(aVoid -> binding.editTextUsername.setText(""))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding comment", e));
    }

    private void getComments() {
        adapter = new CommentsAdapter(requireContext(), list);
        binding.commentsRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.commentsRv.setAdapter(adapter);
        moodEventsRef.document(entry.getMoodId()).collection("comments")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error fetching comments", e);
                        return;
                    }
                    list.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        CommentsModel comment = doc.toObject(CommentsModel.class);
                        list.add(comment);
                    }
                    binding.txtNotificationCount.setText(""+list.size());
                    if (!list.isEmpty()){
                        binding.commentsRv.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}