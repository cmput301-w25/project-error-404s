package com.example.mood_pulse.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.mood_pulse.AddMood;
import com.example.mood_pulse.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Start AddMood Activity when the dashboard is opened
        Intent intent = new Intent(getActivity(), AddMood.class);
        startActivity(intent);

        // Finish the fragment's view to prevent going back to it
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}