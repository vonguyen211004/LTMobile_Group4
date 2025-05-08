package com.example.weatherapp1.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.weatherapp1.MainActivity;
import com.example.weatherapp1.NotificationHelper;
import com.example.weatherapp1.R;
import com.example.weatherapp1.WeatherApp;
import com.example.weatherapp1.models.WeatherAlert;
import com.example.weatherapp1.utils.LocationUtils;
import com.example.weatherapp1.utils.NetworkUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherForegroundService extends Service {

    private static final String TAG = "WeatherForegroundSvc";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ScheduledExecutorService scheduler;
    private final int notificationId = 1001;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    checkWeatherAlerts(location);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(notificationId, createNotification());

        requestLocationUpdates();
        startPeriodicWeatherCheck();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    private Notification createNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, WeatherApp.CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.monitoring_weather))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void requestLocationUpdates() {
        try {
            LocationRequest locationRequest = new LocationRequest.Builder(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    TimeUnit.HOURS.toMillis(1)
            ).build();

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        } catch (SecurityException e) {
            Log.e(TAG, "Error requesting location updates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startPeriodicWeatherCheck() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Sử dụng phương thức với Context
                Location location = LocationUtils.getLastKnownLocation(this, fusedLocationClient);
                if (location != null) {
                    Log.d(TAG, "Periodic check: Location obtained: " + location.getLatitude() + ", " + location.getLongitude());
                    checkWeatherAlerts(location);
                } else {
                    Log.e(TAG, "Periodic check: Could not get location");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in periodic weather check: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.HOURS);
    }

    private void checkWeatherAlerts(Location location) {
        executor.execute(() -> {
            try {
                List<WeatherAlert> alerts = NetworkUtils.getWeatherAlerts(
                        location.getLatitude(), location.getLongitude());

                if (!alerts.isEmpty()) {
                    Log.d(TAG, "Weather alert found: " + alerts.get(0).getEvent());
                    NotificationHelper notificationHelper = new NotificationHelper(this);
                    notificationHelper.sendAlertNotification(alerts.get(0));
                } else {
                    Log.d(TAG, "No weather alerts found");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error checking weather alerts: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
