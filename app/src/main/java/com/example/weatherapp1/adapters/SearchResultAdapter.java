package com.example.weatherapp1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp1.databinding.ItemSearchResultBinding;
import com.example.weatherapp1.models.SearchResult;
import com.example.weatherapp1.utils.DrawableUtils;

public class SearchResultAdapter extends ListAdapter<SearchResult, SearchResultAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchResult item);
    }

    public SearchResultAdapter(OnItemClickListener listener) {
        super(new SearchResultDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchResultBinding binding = ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult item = getItem(position);
        holder.bind(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSearchResultBinding binding;

        public ViewHolder(ItemSearchResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Điều chỉnh kích thước biểu tượng vị trí
            ImageView locationIcon = binding.imageViewLocationIcon;
            DrawableUtils.resizeImageViewDrawable(locationIcon, 24, 24);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }

        public void bind(SearchResult item) {
            binding.textViewCityName.setText(item.getName());
            binding.textViewCountry.setText(item.getCountry());
        }
    }

    private static class SearchResultDiffCallback extends DiffUtil.ItemCallback<SearchResult> {
        @Override
        public boolean areItemsTheSame(@NonNull SearchResult oldItem, @NonNull SearchResult newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getLat() == newItem.getLat() &&
                    oldItem.getLon() == newItem.getLon();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchResult oldItem, @NonNull SearchResult newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getCountry().equals(newItem.getCountry()) &&
                    oldItem.getLat() == newItem.getLat() &&
                    oldItem.getLon() == newItem.getLon();
        }
    }
}
