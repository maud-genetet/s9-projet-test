package com.agence.Trip.Hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HotelTest {

    @Test
    void testHotelCreation() {
        String city = "Paris";
        String address = "123 Champs-Élysées";
        Integer rating = 4;
        double price = 49.99;

        Hotel hotel = new Hotel(address, city, rating, price);

        assertEquals(city, hotel.getCity());
        assertEquals(address, hotel.getAddress());
        assertEquals(rating, hotel.getRating());
        assertEquals(price, hotel.getPricePerNight());
    }

    @Test
    void testActivityToString() {
        String city = "Paris";
        String address = "123 Champs-Élysées";
        Integer rating = 4;
        double price = 49.99;

        Hotel hotel = new Hotel(address, city, rating, price);

        String expectedString = "Hotel{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", rating=" + rating +
                ", pricePerNight=" + price +
                '}';

        assertEquals(expectedString, hotel.toString());
    }
}
