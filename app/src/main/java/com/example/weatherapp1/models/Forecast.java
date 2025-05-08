package com.example.weatherapp1.models;

import java.util.List;

public class Forecast {
    private final List<HourlyForecast> hourly;
    private final List<DailyForecast> daily;

    public Forecast(List<HourlyForecast> hourly, List<DailyForecast> daily) {
        this.hourly = hourly;
        this.daily = daily;
    }

    public List<HourlyForecast> getHourly() {
        return hourly;
    }

    public List<DailyForecast> getDaily() {
        return daily;
    }

    public static class HourlyForecast {
        private final long dt;
        private final double temp;
        private final Weather weather;

        public HourlyForecast(long dt, double temp, Weather weather) {
            this.dt = dt;
            this.temp = temp;
            this.weather = weather;
        }

        public long getDt() {
            return dt;
        }

        public double getTemp() {
            return temp;
        }

        public Weather getWeather() {
            return weather;
        }
    }

    public static class DailyForecast {
        private final long dt;
        private final Temperature temp;
        private final Weather weather;

        public DailyForecast(long dt, Temperature temp, Weather weather) {
            this.dt = dt;
            this.temp = temp;
            this.weather = weather;
        }

        public long getDt() {
            return dt;
        }

        public Temperature getTemp() {
            return temp;
        }

        public Weather getWeather() {
            return weather;
        }
    }

    public static class Temperature {
        private final double day;
        private final double min;
        private final double max;

        public Temperature(double day, double min, double max) {
            this.day = day;
            this.min = min;
            this.max = max;
        }

        public double getDay() {
            return day;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }
    }

    public static class Weather {
        private final int id;
        private final String main;
        private final String description;
        private final String icon;

        public Weather(int id, String main, String description, String icon) {
            this.id = id;
            this.main = main;
            this.description = description;
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }
}
