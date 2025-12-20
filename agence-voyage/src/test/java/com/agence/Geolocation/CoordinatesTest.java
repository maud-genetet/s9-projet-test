package com.agence.Geolocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CoordinatesTest {
    
    @Test
    void testCoordinatesCreation() {
        double lat = 48.8566;
        double lon = 2.3522;

        Coordinates coordinates = new Coordinates(lat, lon);

        assertEquals(coordinates.getLatitude(), lat);
        assertEquals(coordinates.getLongitude(), lon);
    }

    @Test
    void testCoordinatesToString() {
        double lat = 48.8566;
        double lon = 2.3522;
        Coordinates coordinates = new Coordinates(lat, lon);
        String expectedString = "Coordinates{latitude=48.8566, longitude=2.3522}";

        String coordString = coordinates.toString();

        assertEquals(coordString, expectedString);
    }

    @Test
    void testCoordinatesNegativeValues() {
        double lat = -33.8688;
        double lon = -151.2093;
        Coordinates coordinates = new Coordinates(lat, lon);

        assertEquals(coordinates.getLatitude(), lat);
        assertEquals(coordinates.getLongitude(), lon);
    }
}
