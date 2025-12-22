package com.agence.Trip.Hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HotelServiceTest {

    private HotelService hotelService;
    private IHotelRepository mockRepository;

    private final String CITY_BORDEAUX = "Bordeaux";
    private final String CITY_PARIS = "Paris";

    @BeforeEach
    public void setUp() {
        this.mockRepository = mock(IHotelRepository.class);
        this.hotelService = new HotelService(mockRepository);
    }

    @Test
    public void shouldReturnCheapestHotels_WhenPriorityIsCheapest() {
        // Arrange
        Hotel cheap = new Hotel("Rue A", CITY_BORDEAUX, 4, 50.0);
        Hotel expensive = new Hotel("Rue B", CITY_BORDEAUX, 4, 150.0);
        Hotel otherCity = new Hotel("Rue C", CITY_PARIS, 5, 30.0);
        
        when(mockRepository.getAllHotels()).thenReturn(List.of(cheap, expensive, otherCity));

        // Act
        List<Hotel> result = hotelService.findBestHotels(CITY_BORDEAUX, 3.0, HotelPriority.CHEAPEST);

        // Assert
        assertEquals(1, result.size());
        assertEquals(cheap, result.get(0));
    }

    @Test
    public void shouldReturnMultipleHotels_WhenPricesAreSameAndCheapest() {
        // Arrange
        Hotel hotel1 = new Hotel("Rue 1", CITY_BORDEAUX, 4, 60.0);
        Hotel hotel2 = new Hotel("Rue 2", CITY_BORDEAUX, 5, 60.0);
        when(mockRepository.getAllHotels()).thenReturn(List.of(hotel1, hotel2));

        // Act
        List<Hotel> result = hotelService.findBestHotels(CITY_BORDEAUX, 4.0, HotelPriority.CHEAPEST);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(hotel1, hotel2)));
    }

    @Test
    public void shouldReturnHigherRatedHotels_WhenPriorityIsRating() {
        // Arrange
        Hotel lowRate = new Hotel("Rue 1", CITY_PARIS, 3, 50.0);
        Hotel highRate = new Hotel("Rue 2", CITY_PARIS, 5, 200.0);
        when(mockRepository.getAllHotels()).thenReturn(List.of(lowRate, highRate));

        // Act
        List<Hotel> result = hotelService.findBestHotels(CITY_PARIS, 1.0, HotelPriority.HIGHEST_RATED);

        // Assert
        assertEquals(1, result.size());
        assertEquals(highRate, result.get(0));
    }

    @Test
    public void shouldReturnEmpty_WhenNoHotelMatchesMinRating() {
        // Arrange
        Hotel hotel = new Hotel("Rue A", CITY_BORDEAUX, 2, 100.0);
        when(mockRepository.getAllHotels()).thenReturn(List.of(hotel));

        // Act
        List<Hotel> result = hotelService.findBestHotels(CITY_BORDEAUX, 4.0, HotelPriority.CHEAPEST);

        // Assert
        assertTrue(result.isEmpty()); // Message d'erreur ??
    }

    @Test
    public void shouldReturnEmpty_WhenCityNotFound() {
        // Arrange
        Hotel parisHotel = new Hotel("Rue B", CITY_PARIS, 5, 300.0);
        when(mockRepository.getAllHotels()).thenReturn(List.of(parisHotel));

        // Act
        List<Hotel> result = hotelService.findBestHotels(CITY_BORDEAUX, 1.0, HotelPriority.CHEAPEST);

        // Assert
        assertTrue(result.isEmpty()); // Message d'erreur ??
    }

    @Test
    public void shouldMatchRating_WhenRatingIsExactlyMin() {
        // Arrange
        Hotel edgeHotel = new Hotel("Rue C", CITY_BORDEAUX, 3, 100.0);
        when(mockRepository.getAllHotels()).thenReturn(List.of(edgeHotel));

        // Act
        List<Hotel> result = hotelService.findBestHotels(CITY_BORDEAUX, 3.0, HotelPriority.CHEAPEST);

        // Assert
        assertEquals(1, result.size());
        assertEquals(edgeHotel, result.get(0));
    }

    @Test
    public void shouldReturnMultipleHotels_WhenRatingsAreSameAndHighest() {
        // Arrange
        Hotel h1 = new Hotel("Rue D", CITY_PARIS, 5, 100.0);
        Hotel h2 = new Hotel("Rue E", CITY_PARIS, 5, 150.0);
        when(mockRepository.getAllHotels()).thenReturn(List.of(h1, h2));

        // Act
        List<Hotel> result = hotelService.findBestHotels(CITY_PARIS, 4.0, HotelPriority.HIGHEST_RATED);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(h1, h2)));
    }
}