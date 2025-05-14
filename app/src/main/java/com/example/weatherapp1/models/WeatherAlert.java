package com.example.weatherapp1.models;

public class WeatherAlert {
    private final String event;
    private final long start;
    private final long end;
    private final String description;

    public WeatherAlert(String event, long start, long end, String description) {
        this.event = event;
        this.start = start;
        this.end = end;
        this.description = description;
    }

    public String getEvent() {
        return event;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getDescription() {
        return description;
    }
}
