package com.example.weatherapp1.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp1.databinding.ItemDailyForecastBinding;
import com.example.weatherapp1.models.Forecast;
import com.example.weatherapp1.utils.WeatherUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyForecastAdapter extends ListAdapter<Forecast.DailyForecast, DailyForecastAdapter.ViewHolder> {

    public DailyForecastAdapter() {
        super(new DailyForecastDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDailyForecastBinding binding = ItemDailyForecastBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Forecast.DailyForecast item = getItem(position);
        holder.bind(item, position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDailyForecastBinding binding;

        public ViewHolder(ItemDailyForecastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Forecast.DailyForecast item, int position) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", new Locale("vi"));
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("vi"));
            Date date = new Date(item.getDt() * 1000);


            if (position == 0) {
                binding.textViewDay.setText("HÔM NAY");
            } else {
                String dayName = dayFormat.format(date);
                binding.textViewDay.setText(dayName.substring(0, 1).toUpperCase() + dayName.substring(1).toUpperCase());
            }


            binding.textViewDate.setText(dateFormat.format(date));
            binding.textViewTemperature.setText((int) item.getTemp().getDay() + "°");

            int iconResourceId = WeatherUtils.getWeatherIconResource(item.getWeather().getIcon());
            Drawable resizedIcon = WeatherUtils.resizeDrawable(
                    binding.getRoot().getContext(),
                    iconResourceId,
                    32,
                    32
            );
            binding.imageViewWeatherIcon.setImageDrawable(resizedIcon);
        }
    }

    private static class DailyForecastDiffCallback extends DiffUtil.ItemCallback<Forecast.DailyForecast> {
        @Override
        public boolean areItemsTheSame(@NonNull Forecast.DailyForecast oldItem, @NonNull Forecast.DailyForecast newItem) {
            return oldItem.getDt() == newItem.getDt();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Forecast.DailyForecast oldItem, @NonNull Forecast.DailyForecast newItem) {
            return oldItem.getDt() == newItem.getDt() &&
                    oldItem.getTemp().getDay() == newItem.getTemp().getDay() &&
                    oldItem.getWeather().getIcon().equals(newItem.getWeather().getIcon());
        }
    }
}
