package com.example.weatherapp1.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LocationUtils {

    private static final String TAG = "LocationUtils";

    // Phương thức nhận cả Context và FusedLocationProviderClient
    public static Location getLastKnownLocation(Context context, FusedLocationProviderClient fusedLocationClient) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "Location permissions not granted");
                return null;
            }

            // Kiểm tra xem GPS có bật không
            LocationManager locationManager = (LocationManager)
                    context.getSystemService(Context.LOCATION_SERVICE);

            if (locationManager != null &&
                    !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.e(TAG, "Location providers are disabled");
                return null;
            }

            // Sử dụng CountDownLatch để đợi kết quả
            final CountDownLatch latch = new CountDownLatch(1);
            final Location[] locationResult = new Location[1];

            // Tạo yêu cầu vị trí với độ ưu tiên cao
            LocationRequest locationRequest = new LocationRequest.Builder(500)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMaxUpdates(1)
                    .build();

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult result) {
                    if (result.getLocations().size() > 0) {
                        locationResult[0] = result.getLocations().get(0);
                    }
                    latch.countDown();
                }
            };

            // Yêu cầu cập nhật vị trí
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );

            // Đồng thời thử lấy vị trí cuối cùng đã biết
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        locationResult[0] = task.getResult();
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                        latch.countDown();
                    }
                }
            });

            // Đợi tối đa 10 giây
            latch.await(10, TimeUnit.SECONDS);
            fusedLocationClient.removeLocationUpdates(locationCallback);

            if (locationResult[0] == null) {
                Log.e(TAG, "Could not get location within timeout");
            } else {
                Log.d(TAG, "Location obtained: " + locationResult[0].getLatitude() + ", " + locationResult[0].getLongitude());
            }

            return locationResult[0];
        } catch (SecurityException | InterruptedException e) {
            Log.e(TAG, "Error getting location: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Phương thức chỉ nhận FusedLocationProviderClient - cần Context làm tham số
    public static Location getLastKnownLocation(FusedLocationProviderClient fusedLocationClient) {
        // Không thể kiểm tra quyền vì không có Context
        // Chỉ thực hiện getLastLocation() mà không kiểm tra quyền
        try {
            // Sử dụng CountDownLatch để đợi kết quả
            final CountDownLatch latch = new CountDownLatch(1);
            final Location[] locationResult = new Location[1];

            // Đồng thời thử lấy vị trí cuối cùng đã biết
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        locationResult[0] = task.getResult();
                    }
                    latch.countDown();
                }
            });

            // Đợi tối đa 5 giây
            latch.await(5, TimeUnit.SECONDS);

            return locationResult[0];
        } catch (SecurityException | InterruptedException e) {
            Log.e(TAG, "Error getting location: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
