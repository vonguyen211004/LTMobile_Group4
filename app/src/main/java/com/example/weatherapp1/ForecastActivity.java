package com.example.weatherapp1;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weatherapp1.adapters.DailyForecastAdapter;
import com.example.weatherapp1.adapters.HourlyForecastAdapter;
import com.example.weatherapp1.databinding.ActivityForecastBinding;
import com.example.weatherapp1.models.Forecast;
import com.example.weatherapp1.utils.DrawableUtils;
import com.example.weatherapp1.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

        // Get intent extras
        useLocation = getIntent().getBooleanExtra("USE_LOCATION", false);
        cityName = getIntent().getStringExtra("CITY_NAME") != null ?
                getIntent().getStringExtra("CITY_NAME") : "";
        latitude = getIntent().getDoubleExtra("LATITUDE", 0.0);
        longitude = getIntent().getDoubleExtra("LONGITUDE", 0.0);

        // Đặt hình ảnh trực tiếp cho ImageButton từ tài nguyên drawable
        binding.btnBack.setImageResource(R.drawable.ic_back);
        // Loại bỏ tint để hiển thị màu gốc của PNG
        binding.btnBack.setColorFilter(null);
        // Đảm bảo ImageButton có thể nhìn thấy
        binding.btnBack.setVisibility(View.VISIBLE);

        // Điều chỉnh kích thước biểu tượng
        ImageButton backButton = binding.btnBack;
        DrawableUtils.resizeImageButtonDrawable(backButton, 24, 24);

        // Thay đổi tiêu đề thành chữ in hoa
        binding.textViewTitle.setText(binding.textViewTitle.getText().toString().toUpperCase(Locale.forLanguageTag("vi")));
        binding.textViewHourlyTitle.setText(binding.textViewHourlyTitle.getText().toString().toUpperCase(Locale.forLanguageTag("vi")));
        binding.textViewDailyTitle.setText(binding.textViewDailyTitle.getText().toString().toUpperCase(Locale.forLanguageTag("vi")));

        setupUI();
        setupRecyclerViews();
        loadForecastData();

        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setupUI() {
        // Set current date
        binding.textViewDate.setText("Hôm nay");
    }

    private void setupRecyclerViews() {
        // Setup hourly forecast recycler view
        hourlyAdapter = new HourlyForecastAdapter();
        binding.recyclerViewHourlyForecast.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.recyclerViewHourlyForecast.setAdapter(hourlyAdapter);

        // Setup daily forecast recycler view
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
                    // Update hourly forecast
                    List<Forecast.HourlyForecast> hourlyList = forecast.getHourly();
                    hourlyAdapter.submitList(hourlyList.subList(0, Math.min(24, hourlyList.size())));

                    // Update daily forecast
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
