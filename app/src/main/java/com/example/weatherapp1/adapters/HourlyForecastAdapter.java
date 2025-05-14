package com.example.weatherapp1.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp1.databinding.ItemHourlyForecastBinding;
import com.example.weatherapp1.models.Forecast;
import com.example.weatherapp1.utils.WeatherUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HourlyForecastAdapter extends ListAdapter<Forecast.HourlyForecast, HourlyForecastAdapter.ViewHolder> {

    public HourlyForecastAdapter() {
        super(new HourlyForecastDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHourlyForecastBinding binding = ItemHourlyForecastBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Forecast.HourlyForecast item = getItem(position);
        holder.bind(item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHourlyForecastBinding binding;

        public ViewHolder(ItemHourlyForecastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Forecast.HourlyForecast item) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("vi"));
            binding.textViewTime.setText(timeFormat.format(new Date(item.getDt() * 1000)));

            binding.textViewTemperature.setText((int) item.getTemp() + "°");

            int iconResourceId = WeatherUtils.getWeatherIconResource(item.getWeather().getIcon());
            Drawable resizedIcon = WeatherUtils.resizeDrawable(
                    binding.getRoot().getContext(),
                    iconResourceId,
                    40,
                    40
            );
            binding.imageViewWeatherIcon.setImageDrawable(resizedIcon);
        }
    }

    private static class HourlyForecastDiffCallback extends DiffUtil.ItemCallback<Forecast.HourlyForecast> {
        @Override
        public boolean areItemsTheSame(@NonNull Forecast.HourlyForecast oldItem, @NonNull Forecast.HourlyForecast newItem) {
            return oldItem.getDt() == newItem.getDt();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Forecast.HourlyForecast oldItem, @NonNull Forecast.HourlyForecast newItem) {
            return oldItem.getDt() == newItem.getDt() &&
                    oldItem.getTemp() == newItem.getTemp() &&
                    oldItem.getWeather().getIcon().equals(newItem.getWeather().getIcon());
        }
    }
}
