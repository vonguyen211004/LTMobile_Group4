package com.example.weatherapp1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherapp1.databinding.ActivitySettingsBinding;
import com.example.weatherapp1.utils.MockLocationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Đảm bảo icon hiển thị đúng
        binding.btnBack.setImageResource(R.drawable.ic_back);
        binding.btnBack.setColorFilter(null);
        binding.btnBack.setVisibility(View.VISIBLE);

        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Thiết lập các nút vị trí giả
        binding.btnSetHaiChau.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                MockLocationUtils.setHaiChauLocation(fusedLocationClient);
                Toast.makeText(this, "Đã thiết lập vị trí: Hải Châu, Đà Nẵng", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSetHaDong.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                MockLocationUtils.setHaDongLocation(fusedLocationClient);
                Toast.makeText(this, "Đã thiết lập vị trí: Hà Đông, Hà Nội", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnDisableMock.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                MockLocationUtils.disableMockLocation(fusedLocationClient);
                Toast.makeText(this, "Đã tắt vị trí giả", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return false;
        }
        return true;
    }
}
