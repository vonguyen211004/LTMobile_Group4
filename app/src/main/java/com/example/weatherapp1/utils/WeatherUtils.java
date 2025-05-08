package com.example.weatherapp1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.weatherapp1.R;

import java.util.Locale;

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

    public static String getWeatherConditionInVietnamese(String condition) {
        switch (condition) {
            case "Clear": return "QUANG ĐÃNG";
            case "Clouds": return "NHIỀU MÂY";
            case "Rain": return "MƯA";
            case "Drizzle": return "MƯA PHÙN";
            case "Thunderstorm": return "GIÔNG BÃO";
            case "Snow": return "TUYẾT";
            case "Mist":
            case "Fog":
            case "Haze": return "SƯƠNG MÙ";
            case "Dust":
            case "Sand": return "BỤI";
            case "Smoke": return "KHÓI";
            case "Tornado": return "LỐC XOÁY";
            default: return condition.toUpperCase(Locale.forLanguageTag("vi"));
        }
    }

    public static boolean isSevereWeather(String condition) {
        switch (condition) {
            case "Thunderstorm":
            case "Tornado":
            case "Hurricane":
            case "Tropical Storm": return true;
            default: return false;
        }
    }

    // Phương thức mới để điều chỉnh kích thước biểu tượng
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

    // Phương thức mới để chuyển đổi mã thời tiết sang mô tả
    public static String getWeatherDescription(int weatherId) {
        if (weatherId >= 200 && weatherId < 300) {
            return "Giông bão";
        } else if (weatherId >= 300 && weatherId < 400) {
            return "Mưa phùn";
        } else if (weatherId >= 500 && weatherId < 600) {
            return "Mưa";
        } else if (weatherId >= 600 && weatherId < 700) {
            return "Tuyết";
        } else if (weatherId >= 700 && weatherId < 800) {
            return "Sương mù";
        } else if (weatherId == 800) {
            return "Quang đãng";
        } else if (weatherId > 800 && weatherId < 900) {
            return "Nhiều mây";
        } else {
            return "Không xác định";
        }
    }

    // Phương thức mới để chuyển đổi tốc độ gió sang mô tả
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

    // Phương thức mới để chuyển đổi độ ẩm sang mô tả
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
