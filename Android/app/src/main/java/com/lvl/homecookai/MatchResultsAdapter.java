package com.lvl.homecookai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.lvl.homecookai.database.Recipe;

import java.util.ArrayList;
import java.util.List;

public class MatchResultsAdapter extends RecyclerView.Adapter<MatchResultsAdapter.MatchViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe match);
    }

    private final List<Recipe> items = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public MatchResultsAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Recipe> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match_recipe, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Recipe match = items.get(position);
        holder.bind(match, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImage;
        private final TextView recipeName;
        private final TextView recipeTime;
        private final TextView matchPercent;
        private final MaterialButton viewButton;

        MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeTime = itemView.findViewById(R.id.recipe_time);
            matchPercent = itemView.findViewById(R.id.recipe_match_percent);
            viewButton = itemView.findViewById(R.id.view_button);
        }

        void bind(Recipe match, OnRecipeClickListener listener) {
            recipeName.setText(match.getDish_name());
            recipeTime.setText(match.getCook_time() + " мин");

            // если image — это URL
            Glide.with(itemView.getContext())
                    .load(match.getImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(recipeImage);

            // если процент совпадения не используется — можно скрыть
            matchPercent.setVisibility(View.GONE);

            View.OnClickListener clickListener = v -> listener.onRecipeClick(match);
            itemView.setOnClickListener(clickListener);
            viewButton.setOnClickListener(clickListener);
        }
    }
}
