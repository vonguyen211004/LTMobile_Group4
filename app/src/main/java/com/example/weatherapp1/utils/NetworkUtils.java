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

    private static final String OPEN_WEATHER_API_KEY = "b6a17513fe3cc325eb68090a914ff364";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private static final String GEO_URL = "https://api.openweathermap.org/geo/1.0";

    public static List<SearchResult> searchCities(String query) throws IOException, JSONException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        URL url = new URL(GEO_URL + "/direct?q=" + encodedQuery + "&limit=5&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "URL tìm kiếm thành phố: "  + url.toString());
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

        Log.d(TAG, "Lấy thời tiết theo thành phố URL: " + url.toString());
        String response = getResponseFromUrl(url);
        return parseCurrentWeather(response);
    }

    public static CurrentWeather getCurrentWeatherByLocation(double lat, double lon) throws IOException, JSONException {
        URL url = new URL(BASE_URL + "/weather?lat=" + lat + "&lon=" + lon + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "Lấy thời tiết theo vị trí URL: "+ url.toString());
        String response = getResponseFromUrl(url);
        return parseCurrentWeather(response);
    }

    public static Forecast getForecastByCity(String city) throws IOException, JSONException {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
        URL geoUrl = new URL(GEO_URL + "/direct?q=" + encodedCity + "&limit=1&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "Geo URL: " + geoUrl.toString());
        String geoResponse = getResponseFromUrl(geoUrl);
        JSONArray geoJsonArray = new JSONArray(geoResponse);

        if (geoJsonArray.length() == 0) {
            throw new IOException("Không tìm thấy thành phố");
        }

        JSONObject geoJsonObject = geoJsonArray.getJSONObject(0);
        double lat = geoJsonObject.getDouble("lat");
        double lon = geoJsonObject.getDouble("lon");

        return getForecastByLocation(lat, lon);
    }

    public static Forecast getForecastByLocation(double lat, double lon) throws IOException, JSONException {
        URL url = new URL(BASE_URL + "/forecast?lat=" + lat + "&lon=" + lon + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);

        Log.d(TAG, "Lấy dự báo thời tiết URL: " + url.toString());
        String response = getResponseFromUrl(url);
        return parseForecast5Day(response);
    }

    public static List<WeatherAlert> getWeatherAlerts(double lat, double lon) throws IOException, JSONException {
        List<WeatherAlert> alerts = new ArrayList<>();

        try {
            URL currentUrl = new URL(BASE_URL + "/weather?lat=" + lat + "&lon=" + lon + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);
            String currentResponse = getResponseFromUrl(currentUrl);
            JSONObject currentData = new JSONObject(currentResponse);


            URL forecastUrl = new URL(BASE_URL + "/forecast?lat=" + lat + "&lon=" + lon + "&units=metric&lang=vi&appid=" + OPEN_WEATHER_API_KEY);
            String forecastResponse = getResponseFromUrl(forecastUrl);
            JSONObject forecastData = new JSONObject(forecastResponse);


            processWeatherDataForAlerts(currentData, alerts, true);


            JSONArray listArray = forecastData.getJSONArray("list");
            for (int i = 0; i < Math.min(8, listArray.length()); i++) {
                JSONObject timeObject = listArray.getJSONObject(i);
                processWeatherDataForAlerts(timeObject, alerts, false);


                if (alerts.size() >= 4) {
                    break;
                }
            }


            if (alerts.isEmpty()) {
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
            Log.e(TAG, "Lỗi khi lấy cảnh báo thời tiết: " + e.getMessage());
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

                if (weatherId >= 200 && weatherId < 300 && !containsAlertType(alerts, "Thunderstorm")) {
                    alerts.add(createWeatherAlert("Thunderstorm", "Giông bão", description));
                }

                else if ((weatherId >= 300 && weatherId < 600) && !containsAlertType(alerts, "Rain")) {
                    alerts.add(createWeatherAlert("Rain", main, description));
                }


                else if (weatherId >= 600 && weatherId < 700 && !containsAlertType(alerts, "Snow")) {
                    alerts.add(createWeatherAlert("Snow", "Tuyết", description));
                }


                else if (weatherId >= 700 && weatherId < 800 && !containsAlertType(alerts, "Fog")) {
                    alerts.add(createWeatherAlert("Fog", "Sương mù", description));
                }


                else if (weatherId == 781 && !containsAlertType(alerts, "Tornado")) {
                    alerts.add(createWeatherAlert("Tornado", "Lốc xoáy", description));
                }
            }
        }


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
                event,
                currentTime,
                currentTime + 86400,
                description
        );
    }

    private static CurrentWeather parseCurrentWeather(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);

        JSONArray weatherArray = jsonObject.getJSONArray("weather");
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        CurrentWeather.Weather weather = new CurrentWeather.Weather(
                weatherObject.getInt("id"),
                weatherObject.getString("description"),
                weatherObject.getString("icon")
        );


        JSONObject mainObject = jsonObject.getJSONObject("main");
        CurrentWeather.Main main = new CurrentWeather.Main(
                mainObject.getDouble("temp"),
                mainObject.getInt("humidity")
        );


        JSONObject windObject = jsonObject.getJSONObject("wind");
        CurrentWeather.Wind wind = new CurrentWeather.Wind(
                windObject.getDouble("speed")
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


    private static Forecast parseForecast5Day(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray listArray = jsonObject.getJSONArray("list");


        List<Forecast.HourlyForecast> hourlyList = new ArrayList<>();

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
                                    weatherObject.getString("icon")
                            )
                    )
            );
        }


        List<Forecast.DailyForecast> dailyList = new ArrayList<>();


        java.util.Map<String, JSONObject> dailyMap = new java.util.HashMap<>();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");


        for (int i = 0; i < listArray.length(); i++) {
            JSONObject timeObject = listArray.getJSONObject(i);
            long timestamp = timeObject.getLong("dt");
            String date = dateFormat.format(new java.util.Date(timestamp * 1000));

            if (!dailyMap.containsKey(date)) {
                dailyMap.put(date, timeObject);
            } else {

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


        for (String date : dailyMap.keySet()) {
            JSONObject timeObject = dailyMap.get(date);
            JSONArray weatherArray = timeObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            JSONObject mainObject = timeObject.getJSONObject("main");

            dailyList.add(
                    new Forecast.DailyForecast(
                            timeObject.getLong("dt"),
                            new Forecast.Temperature(
                                    mainObject.getDouble("temp")

                            ),
                            new Forecast.Weather(
                                    weatherObject.getInt("id"),
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
