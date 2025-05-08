package com.example.weatherapp1.utils;

import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;

/**
 * Tiện ích để mô phỏng vị trí trong môi trường phát triển
 * Chỉ sử dụng cho mục đích kiểm thử
 */
public class MockLocationUtils {

    private static final String TAG = "MockLocationUtils";

    // Tọa độ Hải Châu, Đà Nẵng
    public static final double HAI_CHAU_LATITUDE = 16.0472;
    public static final double HAI_CHAU_LONGITUDE = 108.2220;

    // Tọa độ Hà Đông, Hà Nội
    public static final double HA_DONG_LATITUDE = 20.9698;
    public static final double HA_DONG_LONGITUDE = 105.7875;

    /**
     * Thiết lập vị trí giả cho LDPlayer hoặc các máy ảo Android khác
     *
     * Hướng dẫn sử dụng:
     * 1. Trong LDPlayer, mở Developer Options (Settings > About > Tap Build Number 7 times)
     * 2. Trong Developer Options, bật "Allow mock locations"
     * 3. Sử dụng phương thức này để thiết lập vị trí giả
     *
     * Lưu ý: Phương thức này chỉ hoạt động khi ứng dụng có quyền ACCESS_FINE_LOCATION
     */
    public static void setMockLocation(FusedLocationProviderClient fusedLocationClient,
                                       double latitude, double longitude) {
        try {
            // Tạo vị trí giả
            Location mockLocation = new Location("mock");
            mockLocation.setLatitude(latitude);
            mockLocation.setLongitude(longitude);
            mockLocation.setAccuracy(3.0f); // Độ chính xác 3 mét
            mockLocation.setTime(System.currentTimeMillis());
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

            // Thiết lập vị trí giả cho FusedLocationProvider
            fusedLocationClient.setMockMode(true);
            fusedLocationClient.setMockLocation(mockLocation);

            Log.d(TAG, "Mock location set to: " + latitude + ", " + longitude);
        } catch (SecurityException e) {
            Log.e(TAG, "Error setting mock location: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Thiết lập vị trí Hải Châu, Đà Nẵng
     */
    public static void setHaiChauLocation(FusedLocationProviderClient fusedLocationClient) {
        setMockLocation(fusedLocationClient, HAI_CHAU_LATITUDE, HAI_CHAU_LONGITUDE);
    }

    /**
     * Thiết lập vị trí Hà Đông, Hà Nội
     */
    public static void setHaDongLocation(FusedLocationProviderClient fusedLocationClient) {
        setMockLocation(fusedLocationClient, HA_DONG_LATITUDE, HA_DONG_LONGITUDE);
    }

    /**
     * Tắt chế độ vị trí giả
     */
    public static void disableMockLocation(FusedLocationProviderClient fusedLocationClient) {
        try {
            fusedLocationClient.setMockMode(false);
            Log.d(TAG, "Mock location disabled");
        } catch (SecurityException e) {
            Log.e(TAG, "Error disabling mock location: " + e.getMessage());
        }
    }
}
