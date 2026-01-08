package com.lvl.homecookai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lvl.homecookai.database.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private final List<Ingredient> items = new ArrayList<>();
    private final Map<Integer, Integer> quantities = new HashMap<>();
    private final Map<Integer, Ingredient> ingredientsById = new HashMap<>();
    private final OnIngredientSelectionListener listener;

    public interface OnIngredientSelectionListener {
        void onSelectionChanged(Map<Ingredient, Integer> selected);
    }

    public IngredientAdapter(OnIngredientSelectionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Ingredient> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setAllIngredients(List<Ingredient> allItems) {
        ingredientsById.clear();
        if (allItems != null) {
            for (Ingredient ingredient : allItems) {
                ingredientsById.put(ingredient.getId(), ingredient);
            }
        }
    }

    public Map<Ingredient, Integer> getSelectedIngredients() {
        Map<Ingredient, Integer> selected = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            int quantity = entry.getValue();
            if (quantity > 0) {
                Ingredient ingredient = ingredientsById.get(entry.getKey());
                if (ingredient != null) {
                    selected.put(ingredient, quantity);
                }
            }
        }
        return selected;
    }

    public Set<Integer> getSelectedIngredientIds() {
        return new HashSet<>(quantities.keySet());
    }

    public void removeIngredient(Ingredient ingredient) {
        quantities.remove(ingredient.getId());
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(getSelectedIngredients());
        }
    }

    public void setQuantity(Ingredient ingredient, int quantity) {
        if (ingredient == null) {
            return;
        }
        if (quantity > 0) {
            quantities.put(ingredient.getId(), quantity);
        } else {
            quantities.remove(ingredient.getId());
        }
        int position = findPositionById(ingredient.getId());
        if (position >= 0) {
            notifyItemChanged(position);
        } else {
            notifyDataSetChanged();
        }
        if (listener != null) {
            listener.onSelectionChanged(getSelectedIngredients());
        }
    }

    public void incrementIngredient(Ingredient ingredient) {
        int current = quantities.getOrDefault(ingredient.getId(), 0);
        setQuantity(ingredient, current + 1);
    }

    public void decrementIngredient(Ingredient ingredient) {
        int current = quantities.getOrDefault(ingredient.getId(), 0);
        setQuantity(ingredient, Math.max(0, current - 1));
    }

    public void clearSelection() {
        quantities.clear();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = items.get(position);
        int quantity = quantities.getOrDefault(ingredient.getId(), 0);
        boolean isAdded = quantity > 0;
        
        holder.bind(ingredient, quantity, isAdded, (newQuantity) -> {
            if (newQuantity > 0) {
                quantities.put(ingredient.getId(), newQuantity);
            } else {
                quantities.remove(ingredient.getId());
            }
            notifyItemChanged(position);
            if (listener != null) {
                listener.onSelectionChanged(getSelectedIngredients());
            }
        }, () -> {
            quantities.put(ingredient.getId(), 1);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onSelectionChanged(getSelectedIngredients());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int findPositionById(int ingredientId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == ingredientId) {
                return i;
            }
        }
        return -1;
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView ingredientName;
        private final TextView ingredientCategory;
        private final TextView ingredientQuantity;
        private final ImageButton minusButton;
        private final ImageButton plusButton;
        private final MaterialButton addButton;
        private final LinearLayout addButtonContainer;
        private final LinearLayout quantityContainer;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredient_name);
            ingredientCategory = itemView.findViewById(R.id.ingredient_category);
            ingredientQuantity = itemView.findViewById(R.id.ingredient_quantity);
            minusButton = itemView.findViewById(R.id.ingredient_minus_button);
            plusButton = itemView.findViewById(R.id.ingredient_plus_button);
            addButton = itemView.findViewById(R.id.btn_add);
            addButtonContainer = itemView.findViewById(R.id.add_button_container);
            quantityContainer = itemView.findViewById(R.id.quantity_container);
        }

        void bind(Ingredient ingredient, int quantity, boolean isAdded, 
                  OnQuantityChangeListener quantityListener, OnAddClickListener addListener) {
            ingredientName.setText(ingredient.getName());
            ingredientCategory.setText(ingredient.getCategory() != null ? ingredient.getCategory() : "");
            
            // Show/hide buttons based on whether ingredient is added
            if (isAdded) {
                addButtonContainer.setVisibility(View.GONE);
                quantityContainer.setVisibility(View.VISIBLE);
                ingredientQuantity.setText(String.valueOf(quantity));
                
                minusButton.setOnClickListener(v -> {
                    int newQuantity = Math.max(0, quantity - 1);
                    quantityListener.onQuantityChanged(newQuantity);
                });
                
                plusButton.setOnClickListener(v -> {
                    int newQuantity = quantity + 1;
                    quantityListener.onQuantityChanged(newQuantity);
                });
            } else {
                addButtonContainer.setVisibility(View.VISIBLE);
                quantityContainer.setVisibility(View.GONE);
                
                addButton.setOnClickListener(v -> addListener.onAddClicked());
            }
        }
    }

    interface OnQuantityChangeListener {
        void onQuantityChanged(int newQuantity);
    }

    interface OnAddClickListener {
        void onAddClicked();
    }
}
