package com.agence.Geolocation;

public class Coordinates {
    private final double lat;
    private final double lon;

    public Coordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    public String toString() {
        return "Coordinates{" +
                "latitude=" + lat +
                ", longitude=" + lon +
                '}';
    }
}