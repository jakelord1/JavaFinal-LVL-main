package com.lvl.homecookai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lvl.homecookai.database.Recipe;

import java.util.ArrayList;
import java.util.List;

public class HomeRecipeAdapter extends RecyclerView.Adapter<HomeRecipeAdapter.HomeRecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> items = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public HomeRecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Recipe> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_recipe, parent, false);
        return new HomeRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecipeViewHolder holder, int position) {
        Recipe recipe = items.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HomeRecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImage;
        private final TextView recipeTitle;

        HomeRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.home_recipe_image);
            recipeTitle = itemView.findViewById(R.id.home_recipe_title);
        }

        void bind(Recipe recipe, OnRecipeClickListener listener) {
            recipeTitle.setText(recipe.getDish_name());
            Glide.with(itemView.getContext())
                    .load(recipe.getImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(recipeImage);

            itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
        }
    }
}
