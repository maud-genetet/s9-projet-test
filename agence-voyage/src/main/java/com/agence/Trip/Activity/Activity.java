package com.agence.Trip.Activity;

import java.util.Date;

public class Activity {

    private final String city;
    private final String address;
    private final Date date;
    private final ActivityCategory category;
    private final double price;

    Activity(String city, String address, Date date, ActivityCategory category, double price) {
        this.city = city;
        this.address = address;
        this.date = date;
        this.category = category;
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public Date getDate() {
        return date;
    }

    public ActivityCategory getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        return "Activity{" +
                "city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", date=" + date +
                ", category=" + category +
                ", price=" + price +
                '}';
    }
}
