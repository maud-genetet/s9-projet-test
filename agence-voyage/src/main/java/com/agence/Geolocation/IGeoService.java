package com.agence.Geolocation;

public interface IGeoService {
    
    public Coordinates getCoordinates(String city, String address);
    public double getDistanceKm(String city, String address1, String address2);
}
