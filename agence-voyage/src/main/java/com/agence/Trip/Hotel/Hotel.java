package com.agence.Trip.Hotel;

public class Hotel {
    private String address;
    private String city;
    private Integer rating;
    private double pricePerNight;

    public Hotel(String address, String city, Integer rating, double pricePerNight) {
        this.address = address;
        this.city = city;
        this.rating = rating;
        this.pricePerNight = pricePerNight;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public Integer getRating() {
        return rating;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public String toString() {
        return "Hotel{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", rating=" + rating +
                ", pricePerNight=" + pricePerNight +
                '}';
    }
}
