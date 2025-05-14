package com.example.weatherapp1.models;

import java.util.List;

public class CurrentWeather {
    private final int id;
    private final String name;
    private final List<Weather> weather;
    private final Main main;
    private final Wind wind;
    private final double lat;
    private final double lon;

    public CurrentWeather(int id, String name, List<Weather> weather, Main main, Wind wind, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.weather = weather;
        this.main = main;
        this.wind = wind;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public static class Weather {
        private final int id;
        private final String description;
        private final String icon;

        public Weather(int id, String description, String icon) {
            this.id = id;
            this.description = description;
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public static class Main {
        private final Double temp;
        private final Integer humidity;

        public Main(Double temp, Integer humidity) {
            this.temp = temp;
            this.humidity = humidity;
        }

        public Double getTemp() {
            return temp;
        }

        public Integer getHumidity() {
            return humidity;
        }
    }

    public static class Wind {
        private final Double speed;

        public Wind(Double speed) {
            this.speed = speed;
        }

        public Double getSpeed() {
            return speed;
        }
    }
}
