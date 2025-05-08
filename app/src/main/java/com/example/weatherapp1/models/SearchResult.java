package com.example.weatherapp1.models;

public class SearchResult {
    private final String name;
    private final String country;
    private final double lat;
    private final double lon;

    public SearchResult(String name, String country, double lat, double lon) {
        this.name = name;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
