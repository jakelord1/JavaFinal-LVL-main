package com.lvl.homecookai;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lvl.homecookai.database.AppDatabase;
import com.lvl.homecookai.database.ProfileDao;
import com.lvl.homecookai.database.ProfileDatabase;
import com.lvl.homecookai.database.ProfileEntity;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private ProfileDao dao;
    private ProfileEntity profile;
    private EditText name;
    private EditText preferences;
    private View editButton;
    private TextView saved;
    private TextView scans;
    private TextView streak;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = ProfileDatabase.getDatabase(requireContext()).profileDao();
        profile = dao.getProfile();
        if (profile == null) {
            profile = new ProfileEntity(
                    "Alex Morgan",
                    "Home cook, healthy recipes",
                    "",
                    "",
                    24,
                    18,
                    7
            );
            dao.upsert(profile);
        }

        name = view.findViewById(R.id.profile_name);
        preferences = view.findViewById(R.id.profile_pref_value_edit);
        saved = view.findViewById(R.id.profile_saved_count);
        scans = view.findViewById(R.id.profile_scans_count);
        streak = view.findViewById(R.id.profile_streak_count);
        editButton = view.findViewById(R.id.profile_edit_button);

        name.setText(profile.getName());
        preferences.setText(profile.getHeadline());
        refreshMetrics();

        name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveName();
            }
        });
        preferences.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                savePreferences();
            }
        });
        if (editButton != null) {
            editButton.setOnClickListener(v -> startEditing());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveName();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMetrics();
    }

    private void saveName() {
        if (dao == null || profile == null || name == null) {
            return;
        }
        String newName = name.getText().toString().trim();
        if (!newName.equals(profile.getName())) {
            profile.setName(newName);
            dao.upsert(profile);
        }
        stopEditing();
    }

    private void savePreferences() {
        if (dao == null || profile == null || preferences == null) {
            return;
        }
        String newPrefs = preferences.getText().toString().trim();
        if (!newPrefs.equals(profile.getHeadline())) {
            profile.setHeadline(newPrefs);
            dao.upsert(profile);
        }
    }

    private void refreshMetrics() {
        if (getContext() == null) {
            return;
        }
        new Thread(() -> {
            AppDatabase appDb = AppDatabase.getDatabase(requireContext());
            int savedCount = appDb.recipeDao().getAllRecipes().size();
            int scanCount = appDb.recentScanDao().getScanCount();
            int streakDays = calculateScanStreak(appDb.recentScanDao().getAllScanTimestamps());
            if (profile != null) {
                profile.setSavedCount(savedCount);
                profile.setScansCount(scanCount);
                profile.setStreakDays(streakDays);
                dao.upsert(profile);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (saved != null) {
                        saved.setText(getString(R.string.profile_saved_format, savedCount));
                    }
                    if (scans != null) {
                        scans.setText(getString(R.string.profile_scans_format, scanCount));
                    }
                    if (streak != null) {
                        streak.setText(getString(R.string.profile_streak_format, streakDays));
                    }
                });
            }
        }).start();
    }

    private int calculateScanStreak(List<Long> timestamps) {
        if (timestamps == null || timestamps.isEmpty()) {
            return 0;
        }
        Set<Long> days = new HashSet<>();
        Calendar calendar = Calendar.getInstance();
        for (Long ts : timestamps) {
            if (ts == null) {
                continue;
            }
            calendar.setTimeInMillis(ts);
            int year = calendar.get(Calendar.YEAR);
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            days.add(((long) year << 16) + dayOfYear);
        }
        calendar.setTimeInMillis(timestamps.get(0));
        int year = calendar.get(Calendar.YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        long currentKey = ((long) year << 16) + dayOfYear;
        int streakCount = 0;
        while (days.contains(currentKey)) {
            streakCount++;
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            year = calendar.get(Calendar.YEAR);
            dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            currentKey = ((long) year << 16) + dayOfYear;
        }
        return streakCount;
    }

    private void startEditing() {
        if (name == null) {
            return;
        }
        name.setEnabled(true);
        name.setFocusableInTouchMode(true);
        name.setCursorVisible(true);
        name.requestFocus();
        InputMethodManager imm =
                (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void stopEditing() {
        if (name == null) {
            return;
        }
        name.setEnabled(false);
        name.setFocusable(false);
        name.setCursorVisible(false);
        InputMethodManager imm =
                (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
        }
    }
}
