package com.lvl.homecookai;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lvl.homecookai.database.AppDatabase;
import com.lvl.homecookai.database.Recipe;
import com.lvl.homecookai.database.RecipeDao;

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecipeDao recipeDao;
    private HistoryAdapter adapter;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 5;
    private boolean hasMore = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        recipeDao = database.recipeDao();

        View profileIcon = view.findViewById(R.id.profile_icon);
        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showProfile();
                }
            });
        }

        RecyclerView recyclerView = view.findViewById(R.id.history_recycler_view);
        View emptyState = view.findViewById(R.id.empty_state);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new HistoryAdapter(this::openRecipeDetail);
        recyclerView.setAdapter(adapter);

        MaterialButton loadMoreButton = view.findViewById(R.id.load_more_button);
        loadMoreButton.setOnClickListener(v -> loadNextPage(loadMoreButton, recyclerView, emptyState));

        loadNextPage(loadMoreButton, recyclerView, emptyState);
    }

    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
//        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_NAME, recipe.name);
//        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_INGREDIENTS, recipe.ingredients);
//        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_TIME, recipe.time);
//        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_INSTRUCTIONS, recipe.instructions);
//        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_IMAGE, recipe.imageResId);
        startActivity(intent);
    }

    private void loadNextPage(MaterialButton loadMoreButton, RecyclerView recyclerView, View emptyState) {
        if (!hasMore) {
            loadMoreButton.setEnabled(false);
            return;
        }

        int offset = currentPage * PAGE_SIZE;
        List<Recipe> page = recipeDao.getRecipesPaged(PAGE_SIZE, offset);

        if (page.isEmpty()) {
            hasMore = false;
            loadMoreButton.setEnabled(false);
            loadMoreButton.setVisibility(View.GONE);
            if (adapter.getItemCount() == 0 && emptyState != null) {
                emptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            return;
        }

        adapter.addItems(page);
        currentPage++;

        if (emptyState != null) {
            emptyState.setVisibility(View.GONE);
        }
        recyclerView.setVisibility(View.VISIBLE);

        if (page.size() < PAGE_SIZE) {
            hasMore = false;
            loadMoreButton.setEnabled(false);
            loadMoreButton.setVisibility(View.GONE);
        }
    }
}
