package com.example.weatherapp1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherapp1.databinding.ActivityWeatherBinding;
import com.example.weatherapp1.models.CurrentWeather;
import com.example.weatherapp1.models.WeatherAlert;
import com.example.weatherapp1.utils.DrawableUtils;
import com.example.weatherapp1.utils.LocationUtils;
import com.example.weatherapp1.utils.NetworkUtils;
import com.example.weatherapp1.utils.WeatherUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";
    private ActivityWeatherBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean useLocation = false;
    private String cityName = "";
    private CurrentWeather currentWeather;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Đặt hình ảnh trực tiếp cho ImageButton từ tài nguyên drawable
        binding.btnBack.setImageResource(R.drawable.ic_back);
        binding.btnSettings.setImageResource(R.drawable.ic_settings);
        // Loại bỏ tint để hiển thị màu gốc của PNG
        binding.btnBack.setColorFilter(null);
        binding.btnSettings.setColorFilter(null);
        // Đảm bảo ImageButton có thể nhìn thấy
        binding.btnBack.setVisibility(View.VISIBLE);
        binding.btnSettings.setVisibility(View.VISIBLE);


        // Điều chỉnh kích thước biểu tượng
        ImageButton backButton = binding.btnBack;
        DrawableUtils.resizeImageButtonDrawable(backButton, 24, 24);

        ImageButton settingsButton = binding.btnSettings;
        DrawableUtils.resizeImageButtonDrawable(settingsButton, 24, 24);

        // Đảm bảo icon hiển thị đúng
        //binding.btnBack.setImageResource(R.drawable.ic_back);
        //binding.btnSettings.setImageResource(R.drawable.ic_settings);
        // Đặt tint color cho icon
        //binding.btnBack.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        //binding.btnSettings.setColorFilter(ContextCompat.getColor(this, android.R.color.white));

        // Điều chỉnh kích thước biểu tượng vị trí trong TextView
        TextView locationText = binding.textViewLocation;
        DrawableUtils.resizeTextViewDrawables(locationText, 16, 16);

        // Get intent extras
        useLocation = getIntent().getBooleanExtra("USE_LOCATION", false);
        cityName = getIntent().getStringExtra("CITY_NAME") != null ?
                getIntent().getStringExtra("CITY_NAME") : "";

        setupUI();

        if (useLocation) {
            checkLocationServicesAndPermissions();
        } else if (!cityName.isEmpty()) {
            loadWeatherByCity(cityName);
        }

        binding.btnForecast.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForecastActivity.class);
            intent.putExtra("CITY_NAME", cityName);
            intent.putExtra("USE_LOCATION", useLocation);
            if (currentWeather != null) {
                intent.putExtra("LATITUDE", currentWeather.getLat());
                intent.putExtra("LONGITUDE", currentWeather.getLon());
            }
            startActivity(intent);
        });



        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Thêm xử lý cho nút Settings
        binding.btnSettings.setOnClickListener(v -> {
            // Mở màn hình Settings
            startActivity(new Intent(this, SettingsActivity.class));
        });
    }

    private void showSimulatedAlertOptions() {
        final String[] alertTypes = {"Rain", "Thunderstorm", "Flood", "Heat", "Fog", "Snow", "Tornado"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn loại cảnh báo để mô phỏng");
        builder.setItems(alertTypes, (dialog, which) -> {
            String alertType = alertTypes[which];
            simulateWeatherAlert(alertType);
        });
        builder.show();
    }

    private void simulateWeatherAlert(String alertType) {
        WeatherAlert alert;

        switch (alertType) {
            case "Thunderstorm":
                alert = new WeatherAlert(
                        "Weather App",
                        "Thunderstorm",
                        System.currentTimeMillis() / 1000,
                        (System.currentTimeMillis() / 1000) + 86400,
                        "Giông bão mạnh"
                );
                break;
            case "Flood":
                alert = new WeatherAlert(
                        "Weather App",
                        "Flood",
                        System.currentTimeMillis() / 1000,
                        (System.currentTimeMillis() / 1000) + 86400,
                        "Cảnh báo lũ lụt"
                );
                break;
            case "Heat":
                alert = new WeatherAlert(
                        "Weather App",
                        "Heat",
                        System.currentTimeMillis() / 1000,
                        (System.currentTimeMillis() / 1000) + 86400,
                        "Nhiệt độ cao trên 35°C"
                );
                break;
            case "Fog":
                alert = new WeatherAlert(
                        "Weather App",
                        "Fog",
                        System.currentTimeMillis() / 1000,
                        (System.currentTimeMillis() / 1000) + 86400,
                        "Sương mù dày đặc"
                );
                break;
            case "Snow":
                alert = new WeatherAlert(
                        "Weather App",
                        "Snow",
                        System.currentTimeMillis() / 1000,
                        (System.currentTimeMillis() / 1000) + 86400,
                        "Tuyết rơi dày"
                );
                break;
            case "Tornado":
                alert = new WeatherAlert(
                        "Weather App",
                        "Tornado",
                        System.currentTimeMillis() / 1000,
                        (System.currentTimeMillis() / 1000) + 86400,
                        "Cảnh báo lốc xoáy"
                );
                break;
            default: // Rain
                alert = new WeatherAlert(
                        "Weather App",
                        "Rain",
                        System.currentTimeMillis() / 1000,
                        (System.currentTimeMillis() / 1000) + 86400,
                        "Mưa lớn kéo dài"
                );
                break;
        }

        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.sendAlertNotification(alert);

        Toast.makeText(this, "Đã tạo cảnh báo " + alertType, Toast.LENGTH_SHORT).show();
    }

    private void setupUI() {
        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd 'Tháng' M", new Locale("vi"));
        binding.textViewDate.setText(dateFormat.format(new Date()));
    }

    private void checkLocationServicesAndPermissions() {
        // Kiểm tra xem dịch vụ vị trí có được bật không
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            // Hiển thị dialog tùy chỉnh thay vì AlertDialog
            showLocationServicesDialog();
            return;
        }

        // Kiểm tra quyền vị trí
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Location permission is required", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Nếu mọi thứ đều ổn, lấy vị trí
        getCurrentLocation();
    }

    private void showLocationServicesDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_location_services, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogView.findViewById(R.id.btnSearchByCity).setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        });

        dialogView.findViewById(R.id.btnOpenSettings).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        dialog.show();
    }

    private void getCurrentLocation() {
        binding.progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            // Sử dụng phương thức cải tiến để lấy vị trí
            Location location = LocationUtils.getLastKnownLocation(this, fusedLocationClient);

            runOnUiThread(() -> {
                if (location != null) {
                    Log.d(TAG, "Location obtained: " + location.getLatitude() + ", " + location.getLongitude());
                    loadWeatherByLocation(location.getLatitude(), location.getLongitude());
                } else {
                    Log.e(TAG, "Could not get location");
                    // Hiển thị hộp thoại để hướng dẫn người dùng
                    showLocationServicesDialog();
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        });
    }

    private void loadWeatherByCity(String city) {
        binding.progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                CurrentWeather weather = NetworkUtils.getCurrentWeatherByCity(city);

                runOnUiThread(() -> {
                    currentWeather = weather;
                    updateUI(weather);
                    binding.progressBar.setVisibility(View.GONE);

                    // Check for severe weather alerts
                    checkForWeatherAlerts(weather.getLat(), weather.getLon());
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error loading weather by city: " + e.getMessage());
                    Toast.makeText(
                            WeatherActivity.this,
                            "Error loading weather: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                    binding.progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void loadWeatherByLocation(double latitude, double longitude) {
        binding.progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                CurrentWeather weather = NetworkUtils.getCurrentWeatherByLocation(latitude, longitude);

                runOnUiThread(() -> {
                    currentWeather = weather;
                    updateUI(weather);
                    binding.progressBar.setVisibility(View.GONE);

                    // Check for severe weather alerts
                    checkForWeatherAlerts(latitude, longitude);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error loading weather by location: " + e.getMessage());
                    Toast.makeText(
                            WeatherActivity.this,
                            "Error loading weather: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                    binding.progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private void updateUI(CurrentWeather weather) {
        // Update location name
        binding.textViewLocation.setText(weather.getName());

        // Update temperature
        binding.textViewTemperature.setText(weather.getMain().getTemp().intValue() + "°");

        // Update weather condition - Sử dụng mô tả từ API thay vì giá trị cứng
        binding.textViewCondition.setText(weather.getWeather().get(0).getDescription().toUpperCase(Locale.forLanguageTag("vi")));

        // Update weather icon - Điều chỉnh kích thước biểu tượng
        int iconResourceId = WeatherUtils.getWeatherIconResource(weather.getWeather().get(0).getIcon());
        Drawable resizedIcon = DrawableUtils.resizeDrawable(this, iconResourceId, 100, 100);
        binding.imageViewWeatherIcon.setImageDrawable(resizedIcon);

        // Update wind speed - Thêm mô tả
        double windSpeed = weather.getWind().getSpeed();
        String windDescription = WeatherUtils.getWindDescription(windSpeed);
        binding.textViewWindSpeed.setText(windSpeed + " m/s\n" + windDescription);

        // Update humidity - Thêm mô tả
        int humidity = weather.getMain().getHumidity();
        String humidityDescription = WeatherUtils.getHumidityDescription(humidity);
        binding.textViewHumidity.setText(humidity + "%\n" + humidityDescription);

        // Log dữ liệu để kiểm tra
        Log.d(TAG, "Weather data: " + weather.getWeather().get(0).getDescription() +
                ", Wind: " + windSpeed + ", Humidity: " + humidity);
    }

    private void checkForWeatherAlerts(double latitude, double longitude) {
        executor.execute(() -> {
            try {
                List<WeatherAlert> alerts = NetworkUtils.getWeatherAlerts(latitude, longitude);

                if (!alerts.isEmpty()) {
                    runOnUiThread(() -> {
                        // Send notification for severe weather
                        NotificationHelper notificationHelper = new NotificationHelper(WeatherActivity.this);
                        notificationHelper.sendAlertNotification(alerts.get(0));
                    });
                }
            } catch (Exception e) {
                // Silently fail for alerts, as they are not critical
                Log.e(TAG, "Error checking weather alerts: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
