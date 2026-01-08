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
import com.lvl.homecookai.database.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private MethodsToApi api;
    private HomeRecipeAdapter recommendedAdapter;

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
    }

    private void submitSearch(String query) {
        //Метод для поиска рецептов по имени(для поиска)
        //api = ApiAccess.getClient().create(MethodsToApi.class);
        //api.getRecipeByName("name", searchName).enqueue(new Callback<List<Recipe>>() {
        //    @Override
        //    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        //        if (response.isSuccessful() && response.body() != null) {
        //            List<Recipe> recipes = response.body();
        //
        //            if (recipes.isEmpty()) {
        //                Toast.makeText(SearchActivity.this,
        //                        "Рецептов не найдено", Toast.LENGTH_SHORT).show();
        //            } else {
        //                // например, передаем список в RecyclerView
        //                recipesAdapter.setRecipes(recipes);
        //            }
        //        }
        //    }
        //
        //    @Override
        //    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        //        Toast.makeText(SearchActivity.this,
        //                "Ошибка поиска рецептов", Toast.LENGTH_SHORT).show();
        //        Log.e("API_ERROR", "Ошибка при поиске: " + t.getMessage(), t);
        //    }
        //});

        //        if (ingredients.isEmpty()) {
        //            Toast.makeText(requireContext(), getString(R.string.enter_ingredients_first),
        //                    Toast.LENGTH_SHORT).show();
        //            return;
        //        }
        //
        //        Intent intent = new Intent(requireContext(), MatchResultsActivity.class);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //        intent.putStringArrayListExtra(MatchResultsActivity.EXTRA_INGREDIENTS,
        //                new ArrayList<>(ingredients));
        //        startActivity(intent);
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
    //Метод для получения всех рецептов(для показа элементов на глав. странице)
    //api.getAllRecipes("all", 1).enqueue(new Callback<List<Recipe>>() {
    //        @Override
    //        public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
    //            if (response.isSuccessful() && response.body() != null) {
    //                List<Recipe> recipes = response.body();
    //
    //                // например, выводим названия в лог
    //                for (Recipe recipe : recipes) {
    //                    Log.d("API", "Рецепт: " + recipe.getDish_name());
    //                }
    //
    //                // здесь можно передать список в RecyclerView адаптер
    //                recipesAdapter.setRecipes(recipes);
    //            }
    //        }
    //
    //        @Override
    //        public void onFailure(Call<List<Recipe>> call, Throwable t) {
    //            Toast.makeText(MainActivity.this,
    //                    "Ошибка загрузки списка рецептов", Toast.LENGTH_SHORT).show();
    //            Log.e("API_ERROR", "Ошибка при запросе списка: " + t.getMessage(), t);
    //        }
    //    });

}

