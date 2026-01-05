package com.lvl.homecookai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lvl.homecookai.database.ProfileDao;
import com.lvl.homecookai.database.ProfileDatabase;
import com.lvl.homecookai.database.ProfileEntity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProfileDao dao = ProfileDatabase.getDatabase(requireContext()).profileDao();
        ProfileEntity profile = dao.getProfile();
        if (profile == null) {
            profile = new ProfileEntity(
                    "Alex Morgan",
                    "Home cook, healthy recipes",
                    "alex.morgan@example.com",
                    "Austin, TX",
                    24,
                    18,
                    7
            );
            dao.upsert(profile);
        }

        TextView name = view.findViewById(R.id.profile_name);
        TextView headline = view.findViewById(R.id.profile_headline);
        TextView email = view.findViewById(R.id.profile_email);
        TextView location = view.findViewById(R.id.profile_location);
        TextView saved = view.findViewById(R.id.profile_saved_count);
        TextView scans = view.findViewById(R.id.profile_scans_count);
        TextView streak = view.findViewById(R.id.profile_streak_count);

        name.setText(profile.getName());
        headline.setText(profile.getHeadline());
        email.setText(profile.getEmail());
        location.setText(profile.getLocation());
        saved.setText(getString(R.string.profile_saved_format, profile.getSavedCount()));
        scans.setText(getString(R.string.profile_scans_format, profile.getScansCount()));
        streak.setText(getString(R.string.profile_streak_format, profile.getStreakDays()));
    }
}
