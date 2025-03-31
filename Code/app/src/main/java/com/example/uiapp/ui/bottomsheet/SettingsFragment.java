package com.example.uiapp.ui.bottomsheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.uiapp.R;
import com.example.uiapp.ui.profile.SignupActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SettingsFragment extends BottomSheetDialogFragment {

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the settings layout (make sure fragment_settings.xml exists in your layout folder)
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Cancel button dismisses the bottom sheet.
        TextView cancelButton = view.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(v -> dismiss());

        // Logout button: clears session and redirects to the signup/login screen.
        Button logoutButton = view.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSharedPreferences("MoodPulsePrefs", getActivity().MODE_PRIVATE)
                        .edit().remove("USERNAME").apply();
                Intent intent = new Intent(getActivity(), SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}
