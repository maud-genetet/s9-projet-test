package com.agence.Trip.Transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import org.junit.jupiter.api.Test;

public class ItineraryTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void testItineraryCreation() {
        Itinerary itinerary = new Itinerary();
        
        assertEquals(0, itinerary.getDurationInMinutes());
        assertEquals(0, itinerary.getTotalPrice());
        assertNull(itinerary.getLastJourney());
    }

    @Test
    void testAddSingleJourney() {
        Itinerary itinerary = new Itinerary();
        LocalDateTime departure = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arrival = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        Journey journey = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, departure, arrival);
        
        itinerary.addJourney(journey);
        
        assertEquals(journey, itinerary.getLastJourney());
        assertEquals(50.00, itinerary.getTotalPrice());
        assertTrue(itinerary.getDurationInMinutes() > 0);
    }

    @Test 
    void testAddMultipleJourneys() {
        Itinerary itinerary = new Itinerary();
        
        LocalDateTime dep1 = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arr1 = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        Journey journey1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, dep1, arr1);
        
        LocalDateTime dep2 = LocalDateTime.parse("2025-12-25 12:00:00", FORMATTER);
        LocalDateTime arr2 = LocalDateTime.parse("2025-12-25 14:30:00", FORMATTER);
        Journey journey2 = new Journey("Lyon", "Marseille", JourneyType.TRAIN, 35.00, dep2, arr2);
        
        itinerary.addJourney(journey1);
        itinerary.addJourney(journey2);
        
        assertEquals(journey2, itinerary.getLastJourney());
        assertEquals(85.00, itinerary.getTotalPrice());
        assertTrue(itinerary.getDurationInMinutes() > 0);
    }

    @Test
    void testAddJourneyWithMismatchedCities() {
        Itinerary itinerary = new Itinerary();
        
        LocalDateTime dep1 = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arr1 = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        Journey journey1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, dep1, arr1);
        
        LocalDateTime dep2 = LocalDateTime.parse("2025-12-25 12:00:00", FORMATTER);
        LocalDateTime arr2 = LocalDateTime.parse("2025-12-25 14:30:00", FORMATTER);
        Journey journey2 = new Journey("Marseille", "Nice", JourneyType.TRAIN, 40.00, dep2, arr2);
        
        itinerary.addJourney(journey1);
        
        try {
            itinerary.addJourney(journey2);
        } catch (IllegalArgumentException e) {
            assertEquals("The departure city of the new journey must match the arrival city of the last journey in the itinerary", e.getMessage());
        }
    }

    @Test 
    void testAddJourneyWithInvalidTimes() {
        Itinerary itinerary = new Itinerary();
        
        LocalDateTime dep1 = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arr1 = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        Journey journey1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, dep1, arr1);
        
        LocalDateTime dep2 = LocalDateTime.parse("2025-12-25 10:00:00", FORMATTER); // Avant l'arrivée du premier trajet
        LocalDateTime arr2 = LocalDateTime.parse("2025-12-25 12:30:00", FORMATTER);
        Journey journey2 = new Journey("Lyon", "Marseille", JourneyType.TRAIN, 35.00, dep2, arr2);
        
        itinerary.addJourney(journey1);
        
        try {
            itinerary.addJourney(journey2);
        } catch (IllegalArgumentException e) {
            assertEquals("The departure time of the new journey must be after or equal the arrival time of the last journey in the itinerary", e.getMessage());
        }
    }

    @Test 
    void testAddNullJourney() {
        Itinerary itinerary = new Itinerary();
        
        try {
            itinerary.addJourney(null);
        } catch (IllegalArgumentException e) {
            assertEquals("Journey cannot be null", e.getMessage());
        }
    }

    @Test
    void testgetDurationInMinutesSingleJourney() {
        Itinerary itinerary = new Itinerary();
        
        LocalDateTime departure = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime arrival = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        
        Journey journey = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, departure, arrival);
        itinerary.addJourney(journey);
        
        // Durée : 3h30 = 210 minutes
        assertEquals(210, itinerary.getDurationInMinutes());
    }

    @Test
    void testgetDurationInMinutesMultipleJourneys() {
        Itinerary itinerary = new Itinerary();
        
        LocalDateTime start = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime mid1 = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        LocalDateTime mid2 = LocalDateTime.parse("2025-12-25 14:30:00", FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2025-12-25 16:00:00", FORMATTER);
        
        Journey journey1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, start, mid1);
        Journey journey2 = new Journey("Lyon", "Marseille", JourneyType.TRAIN, 35.00, mid1, mid2);
        Journey journey3 = new Journey("Marseille", "Nice", JourneyType.PLANE, 45.00, mid2, end);
        
        itinerary.addJourney(journey1);
        itinerary.addJourney(journey2);
        itinerary.addJourney(journey3);
        
        // Durée totale : du 08:00 au 16:00 = 8 heures = 480 minutes
        assertEquals(480, itinerary.getDurationInMinutes());
    }

    @Test
    void testGetTotalPrice() {
        Itinerary itinerary = new Itinerary();
        
        LocalDateTime start = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime mid1 = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        LocalDateTime mid2 = LocalDateTime.parse("2025-12-25 14:30:00", FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2025-12-25 16:00:00", FORMATTER);
        
        Journey journey1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, start, mid1);
        Journey journey2 = new Journey("Lyon", "Marseille", JourneyType.TRAIN, 35.00, mid1, mid2);
        Journey journey3 = new Journey("Marseille", "Nice", JourneyType.PLANE, 45.00, mid2, end);
        
        itinerary.addJourney(journey1);
        itinerary.addJourney(journey2);
        itinerary.addJourney(journey3);
        
        // 50 + 35 + 45 = 130
        assertEquals(130.00, itinerary.getTotalPrice(), 0.01);
    }

    @Test
    void testGetLastJourney() {
        Itinerary itinerary = new Itinerary();
        LocalDateTime start = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime mid1 = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        LocalDateTime mid2 = LocalDateTime.parse("2025-12-25 14:30:00", FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2025-12-25 16:00:00", FORMATTER);
        
        Journey journey1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, start, mid1);
        Journey journey2 = new Journey("Lyon", "Marseille", JourneyType.TRAIN, 35.00, mid1, mid2);
        Journey journey3 = new Journey("Marseille", "Nice", JourneyType.PLANE, 45.00, mid2, end);
        
        itinerary.addJourney(journey1);
        itinerary.addJourney(journey2);
        itinerary.addJourney(journey3);
        
        assertEquals(journey3, itinerary.getLastJourney());
        assertEquals("Nice", itinerary.getLastJourney().getArrivalCity());
    }

    @Test
    void testClone() {
        Itinerary itinerary = new Itinerary();
        LocalDateTime start = LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER);
        LocalDateTime mid1 = LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER);
        LocalDateTime mid2 = LocalDateTime.parse("2025-12-25 14:30:00", FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2025-12-25 16:00:00", FORMATTER);
        
        Journey journey1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, start, mid1);
        Journey journey2 = new Journey("Lyon", "Marseille", JourneyType.TRAIN, 35.00, mid1, mid2);
        Journey journey3 = new Journey("Marseille", "Nice", JourneyType.PLANE, 45.00, mid2, end);
        
        itinerary.addJourney(journey1);
        itinerary.addJourney(journey2);
        itinerary.addJourney(journey3);
        
        Itinerary clonedItinerary = itinerary.clone();
        
        assertEquals(itinerary.getTotalPrice(), clonedItinerary.getTotalPrice());
        assertEquals(itinerary.getDurationInMinutes(), clonedItinerary.getDurationInMinutes());
        assertNotNull(clonedItinerary.getLastJourney());
        assertEquals("Nice", clonedItinerary.getLastJourney().getArrivalCity());
    }

    @Test
    void testComplexItinerary() {
        Itinerary itinerary = new Itinerary();
        
        Journey j1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.00, 
                               LocalDateTime.parse("2025-12-25 08:00:00", FORMATTER), 
                               LocalDateTime.parse("2025-12-25 11:30:00", FORMATTER));
        Journey j2 = new Journey("Lyon", "Marseille", JourneyType.TRAIN, 35.00, 
                               LocalDateTime.parse("2025-12-25 12:00:00", FORMATTER), 
                               LocalDateTime.parse("2025-12-25 14:30:00", FORMATTER));
        Journey j3 = new Journey("Marseille", "Nice", JourneyType.PLANE, 45.00, 
                               LocalDateTime.parse("2025-12-25 15:00:00", FORMATTER), 
                               LocalDateTime.parse("2025-12-25 15:45:00", FORMATTER));
        Journey j4 = new Journey("Nice", "Monaco", JourneyType.TRAIN, 25.00, 
                               LocalDateTime.parse("2025-12-25 17:00:00", FORMATTER), 
                               LocalDateTime.parse("2025-12-25 17:45:00", FORMATTER));
        
        itinerary.addJourney(j1);
        itinerary.addJourney(j2);
        itinerary.addJourney(j3);
        itinerary.addJourney(j4);
        
        // Vérifications
        assertEquals(585, itinerary.getDurationInMinutes()); // Durée totale en minutes
        assertEquals(155.00, itinerary.getTotalPrice()); // 50 + 35 + 45 + 25
        assertEquals("Monaco", itinerary.getLastJourney().getArrivalCity());
    }
}