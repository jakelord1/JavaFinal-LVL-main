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
import com.lvl.homecookai.ApiSetup.ApiAccess;
import com.lvl.homecookai.ApiSetup.MethodsToApi;
import com.lvl.homecookai.database.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private MethodsToApi api;
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

