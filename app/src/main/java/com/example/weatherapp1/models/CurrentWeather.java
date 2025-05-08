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

    public static class Main {
        private final Double temp;
        private final Double feelsLike;
        private final Double tempMin;
        private final Double tempMax;
        private final Integer pressure;
        private final Integer humidity;

        public Main(Double temp, Double feelsLike, Double tempMin, Double tempMax, Integer pressure, Integer humidity) {
            this.temp = temp;
            this.feelsLike = feelsLike;
            this.tempMin = tempMin;
            this.tempMax = tempMax;
            this.pressure = pressure;
            this.humidity = humidity;
        }

        public Double getTemp() {
            return temp;
        }

        public Double getFeelsLike() {
            return feelsLike;
        }

        public Double getTempMin() {
            return tempMin;
        }

        public Double getTempMax() {
            return tempMax;
        }

        public Integer getPressure() {
            return pressure;
        }

        public Integer getHumidity() {
            return humidity;
        }
    }

    public static class Wind {
        private final Double speed;
        private final Integer deg;

        public Wind(Double speed, Integer deg) {
            this.speed = speed;
            this.deg = deg;
        }

        public Double getSpeed() {
            return speed;
        }

        public Integer getDeg() {
            return deg;
        }
    }
}
