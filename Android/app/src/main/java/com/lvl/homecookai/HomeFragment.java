package com.lvl.homecookai;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText inputRecipe = view.findViewById(R.id.inputRecipe);
        View searchButton = view.findViewById(R.id.search_button);
        View profileIcon = view.findViewById(R.id.profile_icon);

        if (inputRecipe != null) {
            inputRecipe.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    submitSearch(inputRecipe.getText() == null ? "" : inputRecipe.getText().toString());
                    return true;
                }
                return false;
            });
        }

        if (searchButton != null) {
            searchButton.setOnClickListener(v ->
                    submitSearch(inputRecipe != null && inputRecipe.getText() != null
                            ? inputRecipe.getText().toString() : ""));
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showProfile();
                }
            });
        }
    }

    private void submitSearch(String query) {
        List<String> ingredients = parseIngredientsQuery(query);
        if (ingredients.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.enter_ingredients_first),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(requireContext(), MatchResultsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putStringArrayListExtra(MatchResultsActivity.EXTRA_INGREDIENTS,
                new ArrayList<>(ingredients));
        startActivity(intent);
    }

    private List<String> parseIngredientsQuery(String query) {
        List<String> result = new ArrayList<>();
        if (query == null) {
            return result;
        }
        String[] parts = query.split("[,\\n]+");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        if (result.isEmpty()) {
            String trimmed = query.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}

