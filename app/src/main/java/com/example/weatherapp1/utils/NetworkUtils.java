package com.example.weatherapp1.utils;

import android.util.Log;

import com.example.weatherapp1.models.CurrentWeather;
import com.example.weatherapp1.models.Forecast;
import com.example.weatherapp1.models.SearchResult;
import com.example.weatherapp1.models.WeatherAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    // Sử dụng API key của bạn
    private static final String OPEN_WEATHER_API_KEY = "b6a17513fe3cc325eb68090a914ff364";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private static final String GEO_URL = "https://api.openweathermap.org/geo/1.0";

    public static List<SearchResult> searchCities(String query) throws IOException, JSONException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        URL url = new URL(GEO_URL + "/direct?q=" + encodedQuery + "&limit=5&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "Search cities URL: " + url.toString());
        String response = getResponseFromUrl(url);
        JSONArray jsonArray = new JSONArray(response);
        List<SearchResult> results = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            results.add(
                    new SearchResult(
                            jsonObject.getString("name"),
                            jsonObject.getString("country"),
                            jsonObject.getDouble("lat"),
                            jsonObject.getDouble("lon")
                    )
            );
        }

        return results;
    }

    public static CurrentWeather getCurrentWeatherByCity(String city) throws IOException, JSONException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
        URL url = new URL(BASE_URL + "/weather?q=" + encodedCity + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "Get weather by city URL: " + url.toString());
        String response = getResponseFromUrl(url);
        return parseCurrentWeather(response);
    }

    public static CurrentWeather getCurrentWeatherByLocation(double lat, double lon) throws IOException, JSONException {
        URL url = new URL(BASE_URL + "/weather?lat=" + lat + "&lon=" + lon + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "Get weather by location URL: " + url.toString());
        String response = getResponseFromUrl(url);
        return parseCurrentWeather(response);
    }

    public static Forecast getForecastByCity(String city) throws IOException, JSONException {
        // First get coordinates from city name
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
        URL geoUrl = new URL(GEO_URL + "/direct?q=" + encodedCity + "&limit=1&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "Geo URL: " + geoUrl.toString());
        String geoResponse = getResponseFromUrl(geoUrl);
        JSONArray geoJsonArray = new JSONArray(geoResponse);

        if (geoJsonArray.length() == 0) {
            throw new IOException("City not found");
        }

        JSONObject geoJsonObject = geoJsonArray.getJSONObject(0);
        double lat = geoJsonObject.getDouble("lat");
        double lon = geoJsonObject.getDouble("lon");

        // Now get forecast with coordinates
        return getForecastByLocation(lat, lon);
    }

    public static Forecast getForecastByLocation(double lat, double lon) throws IOException, JSONException {
        // Thay đổi từ onecall API sang forecast API (5 day / 3 hour forecast)
        URL url = new URL(BASE_URL + "/forecast?lat=" + lat + "&lon=" + lon + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "Get forecast URL: " + url.toString());
        String response = getResponseFromUrl(url);
        return parseForecast5Day(response);
    }

    public static List<WeatherAlert> getWeatherAlerts(double lat, double lon) throws IOException, JSONException {
        // Lấy cả dữ liệu thời tiết hiện tại và dự báo để kiểm tra các điều kiện thời tiết khắc nghiệt
        List<WeatherAlert> alerts = new ArrayList<>();

        try {
            // 1. Kiểm tra thời tiết hiện tại
            URL currentUrl = new URL(BASE_URL + "/weather?lat=" + lat + "&lon=" + lon + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);
            String currentResponse = getResponseFromUrl(currentUrl);
            JSONObject currentData = new JSONObject(currentResponse);

            // 2. Kiểm tra dự báo
            URL forecastUrl = new URL(BASE_URL + "/forecast?lat=" + lat + "&lon=" + lon + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);
            String forecastResponse = getResponseFromUrl(forecastUrl);
            JSONObject forecastData = new JSONObject(forecastResponse);

            // Xử lý dữ liệu thời tiết hiện tại
            processWeatherDataForAlerts(currentData, alerts, true);

            // Xử lý dữ liệu dự báo
            JSONArray listArray = forecastData.getJSONArray("list");
            for (int i = 0; i < Math.min(8, listArray.length()); i++) {
                JSONObject timeObject = listArray.getJSONObject(i);
                processWeatherDataForAlerts(timeObject, alerts, false);

                // Nếu đã tìm thấy đủ các loại cảnh báo, dừng lại
                if (alerts.size() >= 4) {
                    break;
                }
            }

            // Nếu không có cảnh báo nào, kiểm tra các điều kiện khác
            if (alerts.isEmpty()) {
                // Kiểm tra nhiệt độ cao
                if (currentData.has("main")) {
                    JSONObject mainObject = currentData.getJSONObject("main");
                    double temp = mainObject.getDouble("temp");
                    if (temp > 35) {
                        alerts.add(createWeatherAlert("Heat", "Nhiệt độ cao", "Nhiệt độ cao trên 35°C"));
                    } else if (temp < 0) {
                        alerts.add(createWeatherAlert("Cold", "Nhiệt độ thấp", "Nhiệt độ dưới 0°C"));
                    }
                }
            }

            Log.d(TAG, "Found " + alerts.size() + " weather alerts");

        } catch (Exception e) {
            Log.e(TAG, "Error getting weather alerts: " + e.getMessage());
            e.printStackTrace();
        }

        return alerts;
    }

    private static void processWeatherDataForAlerts(JSONObject weatherData, List<WeatherAlert> alerts, boolean isCurrent) throws JSONException {
        if (weatherData.has("weather")) {
            JSONArray weatherArray = weatherData.getJSONArray("weather");
            for (int j = 0; j < weatherArray.length(); j++) {
                JSONObject weatherObject = weatherArray.getJSONObject(j);
                int weatherId = weatherObject.getInt("id");
                String main = weatherObject.getString("main");
                String description = weatherObject.getString("description");

                // Kiểm tra các loại thời tiết khác nhau và thêm cảnh báo tương ứng

                // Kiểm tra bão và mưa dông (Thunderstorm)
                if (weatherId >= 200 && weatherId < 300 && !containsAlertType(alerts, "Thunderstorm")) {
                    alerts.add(createWeatherAlert("Thunderstorm", "Giông bão", description));
                }

                // Kiểm tra mưa (Rain, Drizzle)
                else if ((weatherId >= 300 && weatherId < 600) && !containsAlertType(alerts, "Rain")) {
                    alerts.add(createWeatherAlert("Rain", main, description));
                }

                // Kiểm tra tuyết (Snow)
                else if (weatherId >= 600 && weatherId < 700 && !containsAlertType(alerts, "Snow")) {
                    alerts.add(createWeatherAlert("Snow", "Tuyết", description));
                }

                // Kiểm tra sương mù (Fog, Mist, Haze)
                else if (weatherId >= 700 && weatherId < 800 && !containsAlertType(alerts, "Fog")) {
                    alerts.add(createWeatherAlert("Fog", "Sương mù", description));
                }

                // Kiểm tra lốc xoáy (Tornado)
                else if (weatherId == 781 && !containsAlertType(alerts, "Tornado")) {
                    alerts.add(createWeatherAlert("Tornado", "Lốc xoáy", description));
                }
            }
        }

        // Kiểm tra lũ lụt dựa trên lượng mưa
        if (weatherData.has("rain")) {
            JSONObject rainObject = weatherData.getJSONObject("rain");
            double rainAmount = 0;
            if (rainObject.has("3h")) {
                rainAmount = rainObject.getDouble("3h");
            } else if (rainObject.has("1h")) {
                rainAmount = rainObject.getDouble("1h");
            }

            if (rainAmount > 20 && !containsAlertType(alerts, "Flood")) {
                alerts.add(createWeatherAlert("Flood", "Lũ lụt", "Lượng mưa lớn có thể gây ngập lụt"));
            }
        }
    }

    private static boolean containsAlertType(List<WeatherAlert> alerts, String type) {
        for (WeatherAlert alert : alerts) {
            if (alert.getEvent().contains(type)) {
                return true;
            }
        }
        return false;
    }

    private static WeatherAlert createWeatherAlert(String event, String title, String description) {
        long currentTime = System.currentTimeMillis() / 1000;
        return new WeatherAlert(
                "Weather App",
                event,
                currentTime,
                currentTime + 86400, // 24 giờ
                description
        );
    }

    private static CurrentWeather parseCurrentWeather(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);

        // Parse weather array
        JSONArray weatherArray = jsonObject.getJSONArray("weather");
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        CurrentWeather.Weather weather = new CurrentWeather.Weather(
                weatherObject.getInt("id"),
                weatherObject.getString("main"),
                weatherObject.getString("description"),
                weatherObject.getString("icon")
        );

        // Parse main object
        JSONObject mainObject = jsonObject.getJSONObject("main");
        CurrentWeather.Main main = new CurrentWeather.Main(
                mainObject.getDouble("temp"),
                mainObject.getDouble("feels_like"),
                mainObject.getDouble("temp_min"),
                mainObject.getDouble("temp_max"),
                mainObject.getInt("pressure"),
                mainObject.getInt("humidity")
        );

        // Parse wind object
        JSONObject windObject = jsonObject.getJSONObject("wind");
        CurrentWeather.Wind wind = new CurrentWeather.Wind(
                windObject.getDouble("speed"),
                windObject.getInt("deg")
        );

        return new CurrentWeather(
                jsonObject.getInt("id"),
                jsonObject.getString("name"),
                List.of(weather),
                main,
                wind,
                jsonObject.getJSONObject("coord").getDouble("lat"),
                jsonObject.getJSONObject("coord").getDouble("lon")
        );
    }

    // Phương thức phân tích dữ liệu từ API forecast 5 ngày
    private static Forecast parseForecast5Day(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray listArray = jsonObject.getJSONArray("list");

        // Parse hourly forecast (3 giờ một lần)
        List<Forecast.HourlyForecast> hourlyList = new ArrayList<>();

        // Lấy dữ liệu cho 24 giờ đầu tiên (8 mục)
        for (int i = 0; i < Math.min(8, listArray.length()); i++) {
            JSONObject timeObject = listArray.getJSONObject(i);
            JSONArray weatherArray = timeObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            JSONObject mainObject = timeObject.getJSONObject("main");

            hourlyList.add(
                    new Forecast.HourlyForecast(
                            timeObject.getLong("dt"),
                            mainObject.getDouble("temp"),
                            new Forecast.Weather(
                                    weatherObject.getInt("id"),
                                    weatherObject.getString("main"),
                                    weatherObject.getString("description"),
                                    weatherObject.getString("icon")
                            )
                    )
            );
        }

        // Parse daily forecast (lấy dữ liệu cho 5 ngày)
        List<Forecast.DailyForecast> dailyList = new ArrayList<>();

        // Tạo map để theo dõi ngày
        java.util.Map<String, JSONObject> dailyMap = new java.util.HashMap<>();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");

        // Lặp qua tất cả các mục để tìm nhiệt độ cao nhất và thấp nhất cho mỗi ngày
        for (int i = 0; i < listArray.length(); i++) {
            JSONObject timeObject = listArray.getJSONObject(i);
            long timestamp = timeObject.getLong("dt");
            String date = dateFormat.format(new java.util.Date(timestamp * 1000));

            if (!dailyMap.containsKey(date)) {
                dailyMap.put(date, timeObject);
            } else {
                // Cập nhật nhiệt độ cao nhất/thấp nhất nếu cần
                JSONObject existingObject = dailyMap.get(date);
                JSONObject existingMain = existingObject.getJSONObject("main");
                JSONObject currentMain = timeObject.getJSONObject("main");

                if (currentMain.getDouble("temp_max") > existingMain.getDouble("temp_max")) {
                    existingMain.put("temp_max", currentMain.getDouble("temp_max"));
                }

                if (currentMain.getDouble("temp_min") < existingMain.getDouble("temp_min")) {
                    existingMain.put("temp_min", currentMain.getDouble("temp_min"));
                }
            }
        }

        // Chuyển đổi map thành danh sách dự báo hàng ngày
        for (String date : dailyMap.keySet()) {
            JSONObject timeObject = dailyMap.get(date);
            JSONArray weatherArray = timeObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            JSONObject mainObject = timeObject.getJSONObject("main");

            dailyList.add(
                    new Forecast.DailyForecast(
                            timeObject.getLong("dt"),
                            new Forecast.Temperature(
                                    mainObject.getDouble("temp"),
                                    mainObject.getDouble("temp_min"),
                                    mainObject.getDouble("temp_max")
                            ),
                            new Forecast.Weather(
                                    weatherObject.getInt("id"),
                                    weatherObject.getString("main"),
                                    weatherObject.getString("description"),
                                    weatherObject.getString("icon")
                            )
                    )
            );
        }

        return new Forecast(hourlyList, dailyList);
    }

    private static String getResponseFromUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);

        int responseCode = connection.getResponseCode();
        Log.d(TAG, "Response code: " + responseCode);

        if (responseCode != HttpURLConnection.HTTP_OK) {
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream())
            );

            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();

            Log.e(TAG, "Error response: " + errorResponse.toString());
            throw new IOException("HTTP error code: " + responseCode + ", message: " + errorResponse.toString());
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}
