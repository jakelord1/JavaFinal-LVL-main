package com.lvl.homecookai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lvl.homecookai.database.RecentScan;

import java.util.ArrayList;
import java.util.List;

public class RecentScansAdapter extends RecyclerView.Adapter<RecentScansAdapter.RecentScanViewHolder> {

    private final List<RecentScan> items = new ArrayList<>();
    private int expandedId = -1;
    private final OnRecentScanActionListener listener;

    public interface OnRecentScanActionListener {
        void onDelete(RecentScan scan);
    }

    public RecentScansAdapter(OnRecentScanActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<RecentScan> scans) {
        items.clear();
        if (scans != null) {
            items.addAll(scans);
        }
        if (expandedId != -1) {
            boolean exists = false;
            for (RecentScan scan : items) {
                if (scan.getId() == expandedId) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                expandedId = -1;
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecentScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_scan, parent, false);
        return new RecentScanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentScanViewHolder holder, int position) {
        RecentScan scan = items.get(position);
        boolean isExpanded = scan.getId() == expandedId;
        holder.bind(scan, isExpanded, () -> {
            if (listener != null) {
                listener.onDelete(scan);
            }
        }, () -> {
            int previous = expandedId;
            expandedId = isExpanded ? -1 : scan.getId();
            if (previous != -1) {
                notifyItemChanged(findPositionById(previous));
            }
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RecentScanViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final View deleteButton;

        RecentScanViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recent_scan_image);
            textView = itemView.findViewById(R.id.recent_scan_text);
            deleteButton = itemView.findViewById(R.id.recent_scan_delete);
        }

        void bind(RecentScan scan, boolean isExpanded, Runnable onDelete, Runnable onToggle) {
            String summary = scan.getSummary();
            if (summary == null || summary.trim().isEmpty()) {
                summary = "Scan";
            }
            textView.setText(summary);
            Glide.with(itemView.getContext())
                    .load(scan.getImageUri())
                    .placeholder(R.drawable.ic_camera)
                    .error(R.drawable.ic_camera)
                    .into(imageView);
            deleteButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            itemView.setOnClickListener(v -> onToggle.run());
            deleteButton.setOnClickListener(v -> onDelete.run());
        }
    }

    private int findPositionById(int scanId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == scanId) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }
}
