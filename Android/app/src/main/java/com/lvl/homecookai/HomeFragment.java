package com.lvl.homecookai;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lvl.homecookai.ApiSetup.ApiAccess;
import com.lvl.homecookai.ApiSetup.MethodsToApi;
import com.lvl.homecookai.database.AppDatabase;
import com.lvl.homecookai.database.Recipe;
import com.lvl.homecookai.database.RecentScan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private MethodsToApi api;
    private HomeRecipeAdapter recommendedAdapter;
    private RecentScansAdapter recentScansAdapter;
    private RecyclerView recentScansList;
    private View recentScansEmpty;
    private View recentScansEmptyCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View confirmIngredientsButton = view.findViewById(R.id.confirm_ingredients_button);
        View profileIcon = view.findViewById(R.id.profile_icon);
        RecyclerView recommendedList = view.findViewById(R.id.recommended_list);
        recentScansList = view.findViewById(R.id.recent_scans_list);
        recentScansEmpty = view.findViewById(R.id.recent_scans_empty);
        recentScansEmptyCard = view.findViewById(R.id.recent_scans_empty_card);

        if (confirmIngredientsButton != null) {
            confirmIngredientsButton.setOnClickListener(v ->
                    startActivity(new Intent(requireContext(), IngredientConfirmActivity.class)));
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showProfile();
                }
            });
        }

        if (recommendedList != null) {
            recommendedList.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            recommendedAdapter = new HomeRecipeAdapter(this::openRecipeDetail);
            recommendedList.setAdapter(recommendedAdapter);
            loadRecommendedFromApi();
        }

        if (recentScansList != null) {
            recentScansList.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            recentScansAdapter = new RecentScansAdapter(scan -> deleteRecentScan(scan.getId()));
            recentScansList.setAdapter(recentScansAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentScans();
    }

    private void submitSearch(String query) {
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        
        
        
        
        
        
        
        
        
        
        
    }

    private void loadRecommendedFromApi() {
        api = ApiAccess.getClient().create(MethodsToApi.class);
        api.getAllRecipes("all", 0).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Log.d(TAG, "Recommended response code: " + response.code());
                if (response.isSuccessful() && response.body() != null && recommendedAdapter != null) {
                    List<Recipe> recipes = response.body();
                    Log.d(TAG, "Recommended recipes count: " + recipes.size());
                    recommendedAdapter.setItems(recipes);
                    if (recipes.isEmpty() && getContext() != null) {
                        Toast.makeText(getContext(), "Рецепты не найдены", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Recommended response body is null or not successful");
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Технические неполадки", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.e(TAG, "Recommended request failed", t);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Технические неполадки", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openRecipeDetail(Recipe recipe) {
        if (recipe == null || getActivity() == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra("recipe_id", recipe.getId());
        startActivity(intent);
    }

    private void loadRecentScans() {
        if (recentScansAdapter == null || getContext() == null) {
            return;
        }
        android.content.Context context = getContext();
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(context);
            List<RecentScan> scans = db.recentScanDao().getRecent(10);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    recentScansAdapter.setItems(scans);
                    boolean isEmpty = scans == null || scans.isEmpty();
                    if (recentScansEmpty != null) {
                        recentScansEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    }
                    if (recentScansEmptyCard != null) {
                        recentScansEmptyCard.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    }
                    if (recentScansList != null) {
                        recentScansList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    private void deleteRecentScan(int scanId) {
        if (getContext() == null) {
            return;
        }
        android.content.Context context = getContext();
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(context);
            db.recentScanDao().deleteById(scanId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::loadRecentScans);
            }
        }).start();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}

