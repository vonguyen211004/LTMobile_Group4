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
                Log.e(TAG, "Quyền truy cập vị trí chưa được cấp");
                return null;
            }


            LocationManager locationManager = (LocationManager)
                    context.getSystemService(Context.LOCATION_SERVICE);

            if (locationManager != null &&
                    !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.e(TAG, "Các dịch vụ vị trí bị vô hiệu hóa");
                return null;
            }


            final CountDownLatch latch = new CountDownLatch(1);
            final Location[] locationResult = new Location[1];


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


            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );


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


            latch.await(10, TimeUnit.SECONDS);
            fusedLocationClient.removeLocationUpdates(locationCallback);

            if (locationResult[0] == null) {
                Log.e(TAG, "Không thể lấy vị trí trong thời gian chờ");
            } else {
                Log.d(TAG, "Vị trí đã lấy được: " + locationResult[0].getLatitude() + ", " + locationResult[0].getLongitude());
            }

            return locationResult[0];
        } catch (SecurityException | InterruptedException e) {
            Log.e(TAG, "Lỗi khi lấy vị trí: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
