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
    private final OnRemoveClickListener listener;

    public interface OnRemoveClickListener {
        void onRemoveClicked(Ingredient ingredient);
    }

    public static class SelectedIngredientItem {
        public final Ingredient ingredient;
        public final int quantity;

        public SelectedIngredientItem(Ingredient ingredient, int quantity) {
            this.ingredient = ingredient;
            this.quantity = quantity;
        }
    }

    public SelectedIngredientAdapter(OnRemoveClickListener listener) {
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
        holder.bind(item, () -> {
            if (listener != null) {
                listener.onRemoveClicked(item.ingredient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SelectedIngredientViewHolder extends RecyclerView.ViewHolder {
        private final TextView chipText;
        private final View removeButton;

        SelectedIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            chipText = itemView.findViewById(R.id.chip_text);
            removeButton = itemView.findViewById(R.id.chip_remove);
        }

        void bind(SelectedIngredientItem item, OnChipRemoveListener listener) {
            chipText.setText(item.ingredient.getName() + " x" + item.quantity);
            removeButton.setOnClickListener(v -> listener.onRemove());
        }
    }

    interface OnChipRemoveListener {
        void onRemove();
    }
}
