package com.example.weatherapp1;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.weatherapp1.adapters.DailyForecastAdapter;
import com.example.weatherapp1.adapters.HourlyForecastAdapter;
import com.example.weatherapp1.databinding.ActivityForecastBinding;
import com.example.weatherapp1.models.Forecast;
import com.example.weatherapp1.utils.NetworkUtils;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ForecastActivity extends AppCompatActivity {

    private ActivityForecastBinding binding;
    private HourlyForecastAdapter hourlyAdapter;
    private DailyForecastAdapter dailyAdapter;
    private boolean useLocation = false;
    private String cityName = "";
    private double latitude = 0.0;
    private double longitude = 0.0;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForecastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        useLocation = getIntent().getBooleanExtra("USE_LOCATION", false);
        cityName = getIntent().getStringExtra("CITY_NAME") != null ?
                getIntent().getStringExtra("CITY_NAME") : "";
        latitude = getIntent().getDoubleExtra("LATITUDE", 0.0);
        longitude = getIntent().getDoubleExtra("LONGITUDE", 0.0);


        setupUI();
        setupRecyclerViews();
        loadForecastData();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupUI() {

        binding.textViewDate.setText("Hôm nay");
    }

    private void setupRecyclerViews() {

        hourlyAdapter = new HourlyForecastAdapter();
        binding.recyclerViewHourlyForecast.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.recyclerViewHourlyForecast.setAdapter(hourlyAdapter);


        dailyAdapter = new DailyForecastAdapter();
        binding.recyclerViewDailyForecast.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewDailyForecast.setAdapter(dailyAdapter);
    }

    private void loadForecastData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                Forecast forecast;
                if (useLocation || (latitude != 0.0 && longitude != 0.0)) {
                    forecast = NetworkUtils.getForecastByLocation(latitude, longitude);
                } else {
                    forecast = NetworkUtils.getForecastByCity(cityName);
                }

                runOnUiThread(() -> {

                    List<Forecast.HourlyForecast> hourlyList = forecast.getHourly();
                    hourlyAdapter.submitList(hourlyList.subList(0, Math.min(24, hourlyList.size())));


                    List<Forecast.DailyForecast> dailyList = forecast.getDaily();
                    dailyAdapter.submitList(dailyList.subList(0, Math.min(7, dailyList.size())));

                    binding.progressBar.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(
                            ForecastActivity.this,
                            "Error loading forecast: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                    binding.progressBar.setVisibility(View.GONE);
                });
            }
        });
    }
}