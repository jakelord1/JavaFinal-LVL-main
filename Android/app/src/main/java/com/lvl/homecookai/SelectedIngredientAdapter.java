package com.lvl.homecookai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lvl.homecookai.database.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectedIngredientAdapter extends RecyclerView.Adapter<SelectedIngredientAdapter.SelectedIngredientViewHolder> {

    private final List<SelectedIngredientItem> items = new ArrayList<>();
    private final OnQuantityActionListener listener;

    public interface OnQuantityActionListener {
        void onRemoveClicked(Ingredient ingredient);
        void onIncrementClicked(Ingredient ingredient);
        void onDecrementClicked(Ingredient ingredient);
    }

    public static class SelectedIngredientItem {
        public final Ingredient ingredient;
        public final int quantity;

        public SelectedIngredientItem(Ingredient ingredient, int quantity) {
            this.ingredient = ingredient;
            this.quantity = quantity;
        }
    }

    public SelectedIngredientAdapter(OnQuantityActionListener listener) {
        this.listener = listener;
    }

    public void setItems(Map<Ingredient, Integer> selectedIngredients) {
        items.clear();
        if (selectedIngredients != null) {
            for (Map.Entry<Ingredient, Integer> entry : selectedIngredients.entrySet()) {
                items.add(new SelectedIngredientItem(entry.getKey(), entry.getValue()));
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectedIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_ingredient, parent, false);
        return new SelectedIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedIngredientViewHolder holder, int position) {
        SelectedIngredientItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SelectedIngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView chipText;
        private final TextView chipQuantity;
        private final View decrementButton;
        private final View incrementButton;
        private final View removeButton;

        SelectedIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            chipText = itemView.findViewById(R.id.chip_text);
            chipQuantity = itemView.findViewById(R.id.chip_quantity);
            decrementButton = itemView.findViewById(R.id.chip_decrement);
            incrementButton = itemView.findViewById(R.id.chip_increment);
            removeButton = itemView.findViewById(R.id.chip_remove);
        }

        void bind(SelectedIngredientItem item, OnQuantityActionListener listener) {
            chipText.setText(item.ingredient.getName());
            chipQuantity.setText(String.valueOf(item.quantity));
            removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClicked(item.ingredient);
                }
            });
            decrementButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDecrementClicked(item.ingredient);
                }
            });
            incrementButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIncrementClicked(item.ingredient);
                }
            });
        }
    }
}
