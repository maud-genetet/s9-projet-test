package com.agence.Geolocation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApiGeoServiceTest {
    
    private IGeoService apiGeoService;

    @BeforeEach
    public void setUp() {
        apiGeoService = new ApiGeoService();
    }

    @Test
    void testGetCoordinates() {
        String city = "Paris";
        String address = "Eiffel Tower";

        Coordinates coordinates = apiGeoService.getCoordinates(city, address);

        assert(coordinates.getLatitude() > 48.85 && coordinates.getLatitude() < 48.86);
        assert(coordinates.getLongitude() > 2.29 && coordinates.getLongitude() < 2.30);
    }

    @Test
    void testGetCoordinatesInvalidAddress() {
        String city = "NowhereCity";
        String address = "1234 Unknown St";

        Coordinates coordinates = apiGeoService.getCoordinates(city, address);

        assert(coordinates.getLatitude() == 0.0);
        assert(coordinates.getLongitude() == 0.0);
    }

    @Test
    void testGetCoordinatesEmptyAddress() {
        String city = "";
        String address = "";

        Coordinates coordinates = apiGeoService.getCoordinates(city, address);

        assert(coordinates.getLatitude() == 0.0);
        assert(coordinates.getLongitude() == 0.0);
    }
 
    @Test
    void testGetCoordinatesOnlyEmptyAddress() {
        String city = "Paris";
        String address = "";

        Coordinates coordinates = apiGeoService.getCoordinates(city, address);

        assert(coordinates.getLatitude() == 0.0);
        assert(coordinates.getLongitude() == 0.0);
    }

    @Test
    void testGetCoordinatesSpecialCharacters() {
        String city = "München";
        String address = "Marienplatz 1";

        Coordinates coordinates = apiGeoService.getCoordinates(city, address);

        assert(coordinates.getLatitude() > 48.13 && coordinates.getLatitude() < 48.14);
        assert(coordinates.getLongitude() > 11.57 && coordinates.getLongitude() < 11.58);
    }

    @Test
    void testGetCoordinatesRateLimiting() {
        String city = "Paris";
        String address1 = "Eiffel Tower";
        String address2 = "Louvre Museum";

        long startTime = System.currentTimeMillis();
        apiGeoService.getCoordinates(city, address1);
        apiGeoService.getCoordinates(city, address2);
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        assert(duration >= ApiGeoService.REQUEST_INTERVAL_MS);
    }

    @Test
    void testGetCoordinatesNullAddress() {
        String city = null;
        String address = null;

        Coordinates coordinates = apiGeoService.getCoordinates(city, address);

        assert(coordinates.getLatitude() == 0.0);
        assert(coordinates.getLongitude() == 0.0);
    }

    @Test
    void testGetCoordinatesOnlyNullAddress() {
        String city = "Paris";
        String address = null;

        Coordinates coordinates = apiGeoService.getCoordinates(city, address);

        assert(coordinates.getLatitude() == 0.0);
        assert(coordinates.getLongitude() == 0.0);
    }
    
    @Test
    void testGetDistanceKm() {
        String city = "Paris";
        String address1 = "Eiffel Tower";
        String address2 = "Louvre Museum";

        double distance = apiGeoService.getDistanceKm(city, address1, address2);

        assert(distance > 2.0 && distance < 4.0);
    }

    @Test
    void testGetDistanceKmSameAddress() {
        String city = "Paris";
        String address = "Eiffel Tower";

        double distance = apiGeoService.getDistanceKm(city, address, address);

        assert(distance == 0.0);
    }

    @Test
    void testGetDistanceKmInvalidAddress() {
        String city = "NowhereCity";
        String address1 = "1234 Unknown St";
        String address2 = "5678 Unknown Ave";

        double distance = apiGeoService.getDistanceKm(city, address1, address2);

        assert(distance == 0.0);
    }

    @Test
    void testGetDistanceKmOneInvalidAddress() {
        String city = "Paris";
        String address1 = "Eiffel Tower";
        String address2 = "5678 Unknown Ave";

        double distance = apiGeoService.getDistanceKm(city, address1, address2);

        assert(distance > 0.0);
    }

    @Test
    void testGetDistanceKmEmptyAddresses() {
        String city = "";
        String address1 = "";
        String address2 = "";

        double distance = apiGeoService.getDistanceKm(city, address1, address2);

        assert(distance == 0.0);
    }

    @Test
    void testGetDistanceKmSpecialCharacters() {
        String city = "München";
        String address1 = "Marienplatz 1";
        String address2 = "Viktualienmarkt 3";

        double distance = apiGeoService.getDistanceKm(city, address1, address2);

        assert(distance > 0.25 && distance < 0.3);
    }

    @Test
    void testGetDistanceKmNullAddresses() {
        String city = null;
        String address1 = null;
        String address2 = null;

        double distance = apiGeoService.getDistanceKm(city, address1, address2);

        assert(distance == 0.0);
    }

    @Test
    void testGetDistanceKmLargeDistance() {
        String city = "New York";
        String address1 = "Statue of Liberty";
        String address2 = "Central Park";

        double distance = apiGeoService.getDistanceKm(city, address1, address2);

        assert(distance > 5.0);
    }
}