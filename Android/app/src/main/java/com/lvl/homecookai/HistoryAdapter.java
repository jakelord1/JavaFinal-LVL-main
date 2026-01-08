package com.lvl.homecookai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.bumptech.glide.Glide;
import com.lvl.homecookai.database.Recipe;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> items = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public HistoryAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void addItems(List<Recipe> newItems) {
        int start = items.size();
        items.addAll(newItems);
        notifyItemRangeInserted(start, newItems.size());
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_recipe, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Recipe recipe = items.get(position);
        holder.bind(recipe, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final ImageView recipeImage;
        private final TextView recipeName;
        private final TextView recipeTime;
        private final MaterialButton viewButton;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeTime = itemView.findViewById(R.id.recipe_time);
            viewButton = itemView.findViewById(R.id.view_button);
        }

        void bind(Recipe recipe, OnRecipeClickListener listener) {
            recipeName.setText(recipe.getDish_name());
            recipeTime.setText(recipe.getCook_time() + " мин");
            Glide.with(itemView.getContext())
                    .load(recipe.getImage())
                    .placeholder(R.drawable.ic_camera)
                    .into(recipeImage);

            View.OnClickListener clickListener = v -> listener.onRecipeClick(recipe);
            itemView.setOnClickListener(clickListener);
            viewButton.setOnClickListener(clickListener);
        }
    }
}
