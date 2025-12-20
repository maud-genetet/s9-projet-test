package com.agence.Trip.Activity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class ActivityTest {

    @Test
    void testActivityCreation() {
        String city = "Paris";
        String address = "123 Champs-Élysées";
        Date date = new Date();
        ActivityCategory category = ActivityCategory.CULTURE;
        double price = 49.99;

        Activity activity = new Activity(city, address, date, category, price);

        assertEquals(city, activity.getCity());
        assertEquals(address, activity.getAddress());
        assertEquals(date, activity.getDate());
        assertEquals(category, activity.getCategory());
        assertEquals(price, activity.getPrice());
    }

    @Test
    void testActivityToString() {
        String city = "Paris";
        String address = "123 Champs-Élysées";
        Date date = new Date(1633036800000L);
        ActivityCategory category = ActivityCategory.CULTURE;
        double price = 49.99;

        Activity activity = new Activity(city, address, date, category, price);

        String expectedString = "Activity{" +
                "city='Paris'" +
                ", address='123 Champs-Élysées'" +
                ", date=" + date +
                ", category=CULTURE" +
                ", price=49.99" +
                '}';

        assertEquals(expectedString, activity.toString());
    }
}
