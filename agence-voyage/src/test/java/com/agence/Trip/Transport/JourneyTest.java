package com.agence.Trip.Transport;

import static org.junit.jupiter.api.Assertions.assertEquals;

 import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import org.junit.jupiter.api.Test;

public class JourneyTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void testJourneyCreation() {
        String departureCity = "Paris";
        String arrivalCity = "Lyon";
        JourneyType journeyType = JourneyType.TRAIN;
        double price = 50.00;
        LocalDateTime departureLocalDateTime = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arrivalLocalDateTime = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);

        Journey journey = new Journey(departureCity, arrivalCity, journeyType, price, departureLocalDateTime, arrivalLocalDateTime);

        assertEquals(departureCity, journey.getDepartureCity());
        assertEquals(arrivalCity, journey.getArrivalCity());
        assertEquals(journeyType, journey.getJourneyType());
        assertEquals(price, journey.getPrice());
        assertEquals(departureLocalDateTime, journey.getDepartureLocalDateTime());
        assertEquals(arrivalLocalDateTime, journey.getArrivalLocalDateTime());
    }

    @Test
    void testJourneyCreationWithInvalidParameters() {
        String departureCity = "Paris";
        String arrivalCity = "Lyon";
        String emptyCity = "";
        JourneyType journeyType = JourneyType.TRAIN;
        double price = 50.00;
        LocalDateTime departureLocalDateTime = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arrivalLocalDateTime = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);

        // Test null parameters
        try {
            new Journey(null, arrivalCity, journeyType, price, departureLocalDateTime, arrivalLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("None of the parameters can be null", e.getMessage());
        }

        try {
            new Journey(departureCity, null, journeyType, price, departureLocalDateTime, arrivalLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("None of the parameters can be null", e.getMessage());
        }

        try {
            new Journey(departureCity, arrivalCity, null, price, departureLocalDateTime, arrivalLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("None of the parameters can be null", e.getMessage());
        }

        try {
            new Journey(departureCity, arrivalCity, journeyType, price, null, arrivalLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("None of the parameters can be null", e.getMessage());
        }

        try {
            new Journey(departureCity, arrivalCity, journeyType, price, departureLocalDateTime, null);
        } catch (IllegalArgumentException e) {
            assertEquals("None of the parameters can be null", e.getMessage());
        }

        // Test negative price
        try {
            new Journey(departureCity, arrivalCity, journeyType, -10.00, departureLocalDateTime, arrivalLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("Price cannot be negative", e.getMessage());
        }

        // Test arrival before departure
        try {
            new Journey(departureCity, arrivalCity, journeyType, price, arrivalLocalDateTime, departureLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("Arrival time cannot be before departure time", e.getMessage());
        }

        // Test empty city names
        try {
            new Journey(emptyCity, arrivalCity, journeyType, price, departureLocalDateTime, arrivalLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("City names cannot be empty", e.getMessage());
        }

        // Test empty arrival city
        try {
            new Journey(departureCity, emptyCity, journeyType, price, departureLocalDateTime, arrivalLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("City names cannot be empty", e.getMessage());
        }

        // Test with empty both cities
        try {
            new Journey(emptyCity, emptyCity, journeyType, price, departureLocalDateTime, arrivalLocalDateTime);
        } catch (IllegalArgumentException e) {
            assertEquals("City names cannot be empty", e.getMessage());
        }
    }

    @Test
    void testJourneyToString() {
        String departureCity = "Paris";
        String arrivalCity = "Lyon";
        JourneyType journeyType = JourneyType.TRAIN;
        double price = 50.00;
        LocalDateTime departureLocalDateTime = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arrivalLocalDateTime = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);

        Journey journey = new Journey(departureCity, arrivalCity, journeyType, price, departureLocalDateTime, arrivalLocalDateTime);

        String expectedString = "Journey [departureCity=" + departureCity + ", arrivalCity=" + arrivalCity 
                + ", journeyType=" + journeyType + ", price=" + price + ", departureLocalDateTime=" + departureLocalDateTime 
                + ", arrivalLocalDateTime=" + arrivalLocalDateTime + "]";

        assertEquals(expectedString, journey.toString());
    }

    @Test
    void testJourneySetters() {
        LocalDateTime originalDeparture = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime originalArrival = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        
        Journey journey = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, originalDeparture, originalArrival);

        journey.setDepartureCity("Marseille");
        journey.setArrivalCity("Nice");
        journey.setJourneyType(JourneyType.PLANE);
        journey.setPrice(150.50);
        
        LocalDateTime newDepartureLocalDateTime = LocalDateTime.parse("2025-12-25 10:00:00", FORMATTER);
        LocalDateTime newArrivalLocalDateTime = LocalDateTime.parse("2025-12-25 12:00:00", FORMATTER);
        journey.setDepartureLocalDateTime(newDepartureLocalDateTime);
        journey.setArrivalLocalDateTime(newArrivalLocalDateTime);

        assertEquals("Marseille", journey.getDepartureCity());
        assertEquals("Nice", journey.getArrivalCity());
        assertEquals(JourneyType.PLANE, journey.getJourneyType());
        assertEquals(150.50, journey.getPrice());
        assertEquals(newDepartureLocalDateTime, journey.getDepartureLocalDateTime());
        assertEquals(newArrivalLocalDateTime, journey.getArrivalLocalDateTime());
    }

    @Test
    void testJourneyDifferentTypes() {
        LocalDateTime departure = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arrival = LocalDateTime.parse("2025-12-25 12:00:00", FORMATTER);

        Journey trainJourney = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, departure, arrival);
        assertEquals(JourneyType.TRAIN, trainJourney.getJourneyType());

        Journey planeJourney = new Journey("Paris", "Nice", JourneyType.PLANE, 150.00, departure, arrival);
        assertEquals(JourneyType.PLANE, planeJourney.getJourneyType());
    }
}