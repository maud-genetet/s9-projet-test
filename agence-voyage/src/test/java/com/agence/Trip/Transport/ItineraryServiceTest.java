package com.agence.Trip.Transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItineraryServiceTest {

    private ItineraryService itineraryService;
    private IJourneyRepository mockRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String CITY_PARIS = "Paris";
    private final String CITY_LYON = "Lyon";
    private final String CITY_MARSEILLE = "Marseille";
    private final String CITY_NICE = "Nice";
    private final LocalDateTime TARGET_LocalDateTime = LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER);
    private final double MAX_PRICE = 200.0;

    @BeforeEach
    public void setUp() {
        this.mockRepository = mock(IJourneyRepository.class);
        this.itineraryService = new ItineraryService(mockRepository);
    }

    @Test
    public void shouldReturnDirectItinerary_WhenDirectJourneyExists() {
        // Arrange
        Journey directJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 150.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(directJourney));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertEquals(1, result.size());
        assertEquals(150.0, result.get(0).getTotalPrice());
    }

    @Test
    public void shouldReturnConnectingItinerary_WhenDirectJourneyNotExists() {
        // Arrange
        Journey journey1 = new Journey(CITY_PARIS, CITY_LYON, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey journey2 = new Journey(CITY_LYON, CITY_NICE, JourneyType.TRAIN, 60.0,
                LocalDateTime.parse("2025-12-25 04:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 06:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(journey1, journey2));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertEquals(1, result.size());
        assertEquals(110.0, result.get(0).getTotalPrice()); // 50 + 60
        assertEquals(360, result.get(0).getDurationInMinutes()); // 6 hours total
    }

    @Test
    public void shouldReturnEmpty_WhenNoDepartureJourneyFromCity() {
        // Arrange
        Journey journey = new Journey(CITY_LYON, CITY_NICE, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(journey));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmpty_WhenJourneyLocalDateTimeBeforeDepartureLocalDateTime() {
        // Arrange
        Journey oldJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 150.0,
                LocalDateTime.parse("2025-12-20 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-20 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(oldJourney));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmpty_WhenItineraryIsNotOnTheSameDay() {
        // Arrange
        Journey journey1 = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 150.0,
                LocalDateTime.parse("2025-12-25 23:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-26 01:00:00", FORMATTER));
        Journey journey2 = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 150.0,
                LocalDateTime.parse("2025-12-24 23:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 01:00:00", FORMATTER));
        Journey journey3 = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 150.0,
                LocalDateTime.parse("2025-12-26 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-26 02:00:00", FORMATTER));
        Journey journey4 = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 150.0,
                LocalDateTime.parse("2025-12-24 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-24 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(journey1, journey2, journey3));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmpty_WhenPriceTooExpensive() {
        // Arrange
        Journey expensiveJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 250.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(expensiveJourney));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, 200.0);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFilterByJourneyType_WhenTypeIsNotIndifferent() {
        // Arrange
        Journey trainJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.TRAIN, 100.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey planeJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 150.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(trainJourney, planeJourney));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE, JourneyType.TRAIN,
                MAX_PRICE);

        // Assert
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getTotalPrice());
        assertEquals(JourneyType.TRAIN, result.get(0).getLastJourney().getJourneyType());
        assertEquals(CITY_NICE, result.get(0).getLastJourney().getArrivalCity());
    }

    @Test
    public void shouldReturnAll_WhenJourneyTypeIsIndifferent() {
        // Arrange
        Journey trainJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.TRAIN, 100.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey planeJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 150.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(trainJourney, planeJourney));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.DURATION,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        for (Itinerary itin : result) {
            System.out.println("Itinerary : " + itin);
        }
        assertEquals(2, result.size());
        assertEquals(JourneyType.TRAIN, result.get(0).getLastJourney().getJourneyType());
        assertEquals(JourneyType.PLANE, result.get(1).getLastJourney().getJourneyType());
    }

    @Test
    public void shouldReturnBestPrice_WhenPriorityIsPrice() {
        // Arrange
        Journey cheapJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 100.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER));
        Journey expensiveJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.TRAIN, 150.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(cheapJourney, expensiveJourney));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getTotalPrice());
    }

    @Test
    public void shouldReturnMultipleItineraries_WhenMultiplePossibleConnections() {
        // Arrange
        // Paris -> Lyon et Paris -> Marseille (départs)
        Journey paris2lyon = new Journey(CITY_PARIS, CITY_LYON, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey paris2marseille = new Journey(CITY_PARIS, CITY_MARSEILLE, JourneyType.PLANE, 80.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER));

        // Connexions vers Nice
        Journey lyon2nice = new Journey(CITY_LYON, CITY_NICE, JourneyType.TRAIN, 60.0,
                LocalDateTime.parse("2025-12-25 05:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 06:00:00", FORMATTER));
        Journey marseille2nice = new Journey(CITY_MARSEILLE, CITY_NICE, JourneyType.PLANE, 50.0,
                LocalDateTime.parse("2025-12-25 04:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 06:00:00", FORMATTER));

        when(mockRepository.getAllJourney())
                .thenReturn(List.of(paris2lyon, paris2marseille, lyon2nice, marseille2nice));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.DURATION,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertEquals(2, result.size()); // Deux itinéraires possibles
        assertEquals(110.0, result.get(0).getTotalPrice()); // Paris->Lyon->Nice
        assertEquals(130.0, result.get(1).getTotalPrice()); // Paris->Marseille->Nice
        assertEquals(360, result.get(0).getDurationInMinutes()); // 6 heures pour le premier itinéraire
        assertEquals(360, result.get(1).getDurationInMinutes()); // 6 heures pour le second itinéraire
    }

    @Test
    public void shouldReturnCheapest_WhenMultiplePossibleItineraries() {
        // Arrange
        Journey paris2lyon = new Journey(CITY_PARIS, CITY_LYON, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey paris2marseille = new Journey(CITY_PARIS, CITY_MARSEILLE, JourneyType.PLANE, 80.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER));

        // Connexions vers Nice
        Journey lyon2nice = new Journey(CITY_LYON, CITY_NICE, JourneyType.TRAIN, 60.0,
                LocalDateTime.parse("2025-12-25 05:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 06:00:00", FORMATTER));
        Journey marseille2nice = new Journey(CITY_MARSEILLE, CITY_NICE, JourneyType.PLANE, 50.0,
                LocalDateTime.parse("2025-12-25 04:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 05:00:00", FORMATTER));

        when(mockRepository.getAllJourney())
                .thenReturn(List.of(paris2lyon, paris2marseille, lyon2nice, marseille2nice));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertEquals(1, result.size());
        assertEquals(110.0, result.get(0).getTotalPrice()); // Paris->Lyon->Nice est le moins cher
        assertEquals(360, result.get(0).getDurationInMinutes()); // 6 heures pour l'itinéraire
    }

    @Test
    public void shouldReturnCheapests_WhenMultipleItinerariesWithSameBestPrice() {
        // Arrange
        Journey journey1 = new Journey(CITY_PARIS, CITY_LYON, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey journey2 = new Journey(CITY_LYON, CITY_NICE, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 05:00:00", FORMATTER));

        Journey journey3 = new Journey(CITY_PARIS, CITY_MARSEILLE, JourneyType.PLANE, 70.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER));
        Journey journey4 = new Journey(CITY_MARSEILLE, CITY_NICE, JourneyType.PLANE, 30.0,
                LocalDateTime.parse("2025-12-25 04:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 06:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(journey1, journey2, journey3, journey4));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getTotalPrice()); // Paris->Lyon->Nice
        assertEquals(100.0, result.get(1).getTotalPrice()); // Paris->Marseille->Nice
    }

    @Test
    public void shouldReturnFastest_WhenPriorityIsDuration() {
        // Arrange
        Journey slowJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.PLANE, 100.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 05:00:00", FORMATTER));
        Journey fastJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.TRAIN, 150.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey intermediateJourney = new Journey(CITY_PARIS, CITY_NICE, JourneyType.TRAIN, 120.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(slowJourney, fastJourney, intermediateJourney));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.DURATION,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertEquals(1, result.size());
        assertEquals(150.0, result.get(0).getTotalPrice());
        assertEquals(120, result.get(0).getDurationInMinutes());
    }

    @Test
    public void shouldReturnMultiplesFastests_WhenMultipleItinerariesWithSameBestDuration() {
        // Arrange
        Journey journey1 = new Journey(CITY_PARIS, CITY_LYON, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey journey2 = new Journey(CITY_LYON, CITY_NICE, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 05:00:00", FORMATTER));

        Journey journey3 = new Journey(CITY_PARIS, CITY_MARSEILLE, JourneyType.PLANE, 70.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey journey4 = new Journey(CITY_MARSEILLE, CITY_NICE, JourneyType.PLANE, 30.0,
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 05:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(journey1, journey2, journey3, journey4));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.DURATION,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertEquals(2, result.size());
        assertEquals(300, result.get(0).getDurationInMinutes()); // Paris->Lyon->Nice
        assertEquals(300, result.get(1).getDurationInMinutes()); // Paris->Marseille->Nice
    }

    @Test
    public void shouldReturnEmpty_WhenNoConnectionBetweenCities() {
        // Arrange
        Journey journey = new Journey(CITY_PARIS, CITY_LYON, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(journey));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                CITY_PARIS, CITY_NICE, TARGET_LocalDateTime, TransportPriority.PRICE,
                JourneyType.INDIFFERENT, MAX_PRICE);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test 
    public void shouldReturnCloneItinerary_WhenCloningItinerary() {
        // Arrange
        Journey journey1 = new Journey(CITY_PARIS, CITY_LYON, JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 00:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 02:00:00", FORMATTER));
        Journey journey2 = new Journey(CITY_LYON, CITY_NICE, JourneyType.PLANE, 50.0,
                LocalDateTime.parse("2025-12-25 03:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 05:00:00", FORMATTER));

        Itinerary itinerary = new Itinerary();
        itinerary.addJourney(journey1);
        itinerary.addJourney(journey2);

        // Act
        Itinerary clonedItinerary = itinerary.clone();

        // Assert
        assertEquals(300, clonedItinerary.getDurationInMinutes());
        assertEquals(itinerary.getTotalPrice(), clonedItinerary.getTotalPrice());
    }

    @Test
    void shouldReturnEmpty_WhenNoJourneyOnSameDayOrAfter() {
        // Arrange
        LocalDateTime targetDate = LocalDateTime.parse("2025-12-25 10:00:00", FORMATTER);
        Journey journey = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-24 10:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-24 12:00:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(journey));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                "Paris", "Lyon", targetDate, TransportPriority.PRICE, JourneyType.INDIFFERENT, 100);

        // Assert
        assertTrue(result.isEmpty(), "Les journeys avant la date de départ doivent être ignorés");
    }

    @Test
    void shouldCloneItinerary_WhenMultipleConnectionsExist() {
        // Arrange
        Journey first = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 10:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 12:00:00", FORMATTER));

        Journey second = new Journey("Lyon", "Nice", JourneyType.TRAIN, 70.0,
                LocalDateTime.parse("2025-12-25 13:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 15:00:00", FORMATTER));

        // Assert
        Itinerary itinerary = new Itinerary();
        itinerary.addJourney(first);

        Itinerary clonedItinerary = itinerary.clone();
        clonedItinerary.addJourney(second);

        assertEquals(120, itinerary.getDurationInMinutes());
        assertEquals(300, clonedItinerary.getDurationInMinutes());
    }

    @Test
    void shouldReturnCheapest_WhenMultipleItinerariesWithSamePrice() {
        // Arrange
        Journey j1 = new Journey("Paris", "Lyon", JourneyType.TRAIN, 50.0,
                LocalDateTime.parse("2025-12-25 10:00:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 12:00:00", FORMATTER));
        Journey j2 = new Journey("Paris", "Lyon", JourneyType.PLANE, 50.0,
                LocalDateTime.parse("2025-12-25 10:30:00", FORMATTER),
                LocalDateTime.parse("2025-12-25 12:30:00", FORMATTER));

        when(mockRepository.getAllJourney()).thenReturn(List.of(j1, j2));

        // Act
        List<Itinerary> result = itineraryService.findBestItineraries(
                "Paris", "Lyon", LocalDateTime.parse("2025-12-25 09:00:00", FORMATTER),
                TransportPriority.PRICE, JourneyType.INDIFFERENT, 100);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(i -> i.getTotalPrice() == 50.0));
    }
}