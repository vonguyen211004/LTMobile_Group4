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
        public Temperature(double day) {
            this.day = day;
        }

        public double getDay() {
            return day;
        }
    }

    public static class Weather {
        private final int id;
        private final String icon;

        public Weather(int id, String icon) {
            this.id = id;
            this.icon = icon;
        }

        public int getId() {
            return id;
        }
        public String getIcon() {
            return icon;
        }
    }
}
