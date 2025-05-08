package com.example.weatherapp1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherapp1.databinding.ActivityMainBinding;
import com.example.weatherapp1.utils.DrawableUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Điều chỉnh kích thước biểu tượng
        ImageView globeIcon = binding.imageViewGlobe;
        DrawableUtils.resizeImageViewDrawable(globeIcon, 120, 120);

        // Set up click listeners for the buttons
        binding.btnAccept.setOnClickListener(v -> checkLocationPermission());

        binding.btnManualSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
    }

    private void checkLocationPermission() {
        // Kiểm tra quyền vị trí
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Nếu quyền chưa được cấp, kiểm tra xem có nên hiển thị giải thích không
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
            ) {
                // Hiển thị hộp thoại giải thích
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission to show weather for your current location")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Yêu cầu quyền
                            requestLocationPermission();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            } else {
                // Không cần giải thích, yêu cầu quyền trực tiếp
                requestLocationPermission();
            }
        } else {
            // Quyền đã được cấp, kiểm tra quyền thông báo trên Android 13+
            checkNotificationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private void checkNotificationPermission() {
        // Kiểm tra quyền thông báo trên Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1002
                );
            } else {
                // Tất cả quyền đã được cấp, chuyển đến màn hình thời tiết
                proceedToWeatherActivity();
            }
        } else {
            // Phiên bản Android cũ hơn không cần quyền thông báo riêng
            proceedToWeatherActivity();
        }
    }

    private void proceedToWeatherActivity() {
        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("USE_LOCATION", true);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền vị trí được cấp, kiểm tra quyền thông báo
                checkNotificationPermission();
            } else {
                // Quyền vị trí bị từ chối
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                ) {
                    // Người dùng đã chọn "Don't ask again", hiển thị hộp thoại hướng dẫn
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Denied")
                            .setMessage("You have denied location permission permanently. Please go to settings to enable it.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                // Chuyển đến tìm kiếm thủ công
                                startActivity(new Intent(this, SearchActivity.class));
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                } else {
                    // Chuyển đến tìm kiếm thủ công
                    startActivity(new Intent(this, SearchActivity.class));
                }
            }
        } else if (requestCode == 1002) {
            // Kết quả quyền thông báo, không quan trọng kết quả, vẫn tiếp tục
            proceedToWeatherActivity();
        }
    }
}
