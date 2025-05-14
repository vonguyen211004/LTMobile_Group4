package com.example.weatherapp1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.weatherapp1.R;


public class WeatherUtils {

    private static final String TAG = "WeatherUtils";

    public static int getWeatherIconResource(String iconCode) {
        switch (iconCode) {
            case "01d": return R.drawable.ic_clear_day;
            case "01n": return R.drawable.ic_clear_night;
            case "02d": return R.drawable.ic_partly_cloudy_day;
            case "02n": return R.drawable.ic_partly_cloudy_night;
            case "03d":
            case "03n": return R.drawable.ic_cloudy;
            case "04d":
            case "04n": return R.drawable.ic_cloudy;
            case "09d":
            case "09n": return R.drawable.ic_rain;
            case "10d": return R.drawable.ic_rain_day;
            case "10n": return R.drawable.ic_rain_night;
            case "11d":
            case "11n": return R.drawable.ic_thunderstorm;
            case "13d":
            case "13n": return R.drawable.ic_snow;
            case "50d":
            case "50n": return R.drawable.ic_fog;
            default: return R.drawable.ic_clear_day;
        }
    }


    public static Drawable resizeDrawable(Context context, int resourceId, int width, int height) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            return new BitmapDrawable(context.getResources(), resizedBitmap);
        } catch (Exception e) {
            Log.e(TAG, "Error resizing drawable: " + e.getMessage());
            return context.getResources().getDrawable(resourceId, context.getTheme());
        }
    }




    public static String getWindDescription(double windSpeed) {
        if (windSpeed < 0.5) {
            return "Lặng gió";
        } else if (windSpeed < 1.5) {
            return "Gió nhẹ";
        } else if (windSpeed < 3.3) {
            return "Gió nhẹ";
        } else if (windSpeed < 5.5) {
            return "Gió nhẹ";
        } else if (windSpeed < 7.9) {
            return "Gió vừa";
        } else if (windSpeed < 10.7) {
            return "Gió mạnh";
        } else if (windSpeed < 13.8) {
            return "Gió mạnh";
        } else if (windSpeed < 17.1) {
            return "Gió rất mạnh";
        } else if (windSpeed < 20.7) {
            return "Gió rất mạnh";
        } else if (windSpeed < 24.4) {
            return "Gió bão";
        } else if (windSpeed < 28.4) {
            return "Gió bão";
        } else if (windSpeed < 32.6) {
            return "Bão";
        } else {
            return "Bão lớn";
        }
    }

    public static String getHumidityDescription(int humidity) {
        if (humidity < 30) {
            return "Khô";
        } else if (humidity < 60) {
            return "Bình thường";
        } else if (humidity < 80) {
            return "Ẩm";
        } else {
            return "Rất ẩm";
        }
    }
}
