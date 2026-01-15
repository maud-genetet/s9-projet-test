package com.agence.Trip;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.agence.Trip.Activity.Activity;
import com.agence.Trip.Activity.ActivityCategory;
import com.agence.Trip.Activity.ActivityService;
import com.agence.Trip.Hotel.Hotel;
import com.agence.Trip.Hotel.HotelPriority;
import com.agence.Trip.Hotel.HotelService;
import com.agence.Trip.Transport.Itinerary;
import com.agence.Trip.Transport.ItineraryService;
import com.agence.Trip.Transport.Journey;
import com.agence.Trip.Transport.JourneyType;
import com.agence.Trip.Transport.TransportPriority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class TripPlannerTest {

    private TripPlanner tripPlanner;
    private HotelService hotelService;
    private ActivityService activityService;
    private ItineraryService itineraryService;

    @BeforeEach
    void setUp() {
        hotelService = mock(HotelService.class);
        activityService = mock(ActivityService.class);
        itineraryService = mock(ItineraryService.class);
        tripPlanner = new TripPlanner(hotelService, activityService, itineraryService);
    }

    @Test
    void shouldCreateValidTripPlanWithActivitiesWhenBudgetSufficient() {
        // Arrange
        UserCriterias criterias = new UserCriterias(
            "Paris", "Lyon", new Date(), 3, 500.0,
            4, HotelPriority.HIGHEST_RATED, List.of(ActivityCategory.CULTURE),
            15.0, JourneyType.PLANE, TransportPriority.DURATION
        );

        Hotel hotel = createMockHotel(50.0, "123 Rue Test");
        when(hotelService.findBestHotels(eq("Paris"), eq(4.0), eq(HotelPriority.HIGHEST_RATED)))
            .thenReturn(List.of(hotel));

        Itinerary itin = createMockItinerary(50.0);
        when(itineraryService.findBestItineraries(
            anyString(), anyString(), any(), eq(TransportPriority.DURATION), 
            eq(JourneyType.PLANE), eq(500.0)))
            .thenReturn(List.of(itin));

        Activity act1 = mock(Activity.class);
        when(act1.getPrice()).thenReturn(100.0);
        Activity act2 = mock(Activity.class);
        when(act2.getPrice()).thenReturn(150.0);

        when(activityService.findBestActivities(
            eq("Paris"), eq("123 Rue Test"), eq(15.0), 
            eq(List.of(ActivityCategory.CULTURE)), any()))
            .thenReturn(List.of(act1))
            .thenReturn(List.of(act2));

        // Act
        List<TripPlan> results = tripPlanner.planTrip(criterias);

        // Assert
        assertEquals(1, results.size());
        assertTrue(results.get(0).isValid());
        assertNotNull(results.get(0).getHotel());
        assertNotNull(results.get(0).getJourneyTo());
        assertNotNull(results.get(0).getJourneyBack());
        assertEquals(2, results.get(0).getActivities().size());
        assertEquals(100.0, results.get(0).getActivities().get(0).getPrice());
        assertEquals(150.0, results.get(0).getActivities().get(1).getPrice());
    }

    @Test
    void shouldReturnErrorPlanWhenBudgetTooLow() {
        // Arrange
        UserCriterias criterias = new UserCriterias(
            "Paris", "Lyon", new Date(), 2, 100.0,
            3, HotelPriority.CHEAPEST, List.of(ActivityCategory.ANY),
            10.0, JourneyType.INDIFFERENT, TransportPriority.PRICE
        );

        setupBasicMocks(200.0, 200.0);

        // Act
        List<TripPlan> results = tripPlanner.planTrip(criterias);

        // Assert
        assertEquals(1, results.size());
        assertFalse(results.get(0).isValid());
        assertTrue(results.get(0).getErrors().get(0).contains("100.0"));
        assertNull(results.get(0).getHotel());
    }

    @Test
    void shouldSelectCheapestActivitiesAndDecrementBudgetCorrectly() {
        // Arrange
        UserCriterias criterias = new UserCriterias(
            "Paris", "Lyon", new Date(), 4, 500.0,
            3, HotelPriority.CHEAPEST, List.of(ActivityCategory.ANY),
            10.0, JourneyType.INDIFFERENT, TransportPriority.PRICE
        );

        setupBasicMocks(50.0, 50.0);

        Activity expensive = mock(Activity.class);
        when(expensive.getPrice()).thenReturn(150.0);
        Activity cheap = mock(Activity.class);
        when(cheap.getPrice()).thenReturn(70.0);

        when(activityService.findBestActivities(anyString(), anyString(), anyDouble(), any(), any()))
            .thenReturn(List.of(expensive, cheap))
            .thenReturn(List.of(expensive, cheap))
            .thenReturn(List.of(expensive, cheap));

        // Act
        List<TripPlan> results = tripPlanner.planTrip(criterias);

        // Assert
        List<Activity> activities = results.get(0).getActivities();
        assertEquals(2, activities.size());
        assertEquals(70.0, activities.get(0).getPrice());
        assertEquals(70.0, activities.get(1).getPrice());
    }

    @Test
    void shouldCalculateDatesCorrectlyWithPreciseTimeValidation() {
        // Arrange
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.MARCH, 10, 14, 30, 45);
        cal.set(Calendar.MILLISECOND, 123);
        Date startDate = cal.getTime();

        UserCriterias criterias = new UserCriterias(
            "Paris", "Lyon", startDate, 5, 1000.0,
            3, HotelPriority.CHEAPEST, List.of(ActivityCategory.ANY),
            10.0, JourneyType.INDIFFERENT, TransportPriority.PRICE
        );

        setupBasicMocks(50.0, 50.0);

        // Act
        tripPlanner.planTrip(criterias);

        // Assert
        ArgumentCaptor<LocalDateTime> itinCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(itineraryService, times(2)).findBestItineraries(
            anyString(), anyString(), itinCaptor.capture(), any(), any(), anyDouble()
        );

        List<LocalDateTime> itinDates = itinCaptor.getAllValues();
        LocalDateTime startLdt = itinDates.get(0);
        LocalDateTime returnLdt = itinDates.get(1);

        assertEquals(10, startLdt.getDayOfMonth());
        assertEquals(14, startLdt.getHour());
        assertEquals(30, startLdt.getMinute());
        assertEquals(45, startLdt.getSecond());

        assertEquals(15, returnLdt.getDayOfMonth());
        assertEquals(14, returnLdt.getHour());
        assertEquals(30, returnLdt.getMinute());
        assertEquals(45, returnLdt.getSecond());

        ArgumentCaptor<Date> actCaptor = ArgumentCaptor.forClass(Date.class);
        verify(activityService, times(4)).findBestActivities(
            anyString(), anyString(), anyDouble(), any(), actCaptor.capture()
        );

        Calendar check = Calendar.getInstance();
        List<Date> actDates = actCaptor.getAllValues();
        
        for (int i = 0; i < 4; i++) {
            check.setTime(actDates.get(i));
            assertEquals(11 + i, check.get(Calendar.DAY_OF_MONTH));
            assertEquals(14, check.get(Calendar.HOUR_OF_DAY));
            assertEquals(30, check.get(Calendar.MINUTE));
            assertEquals(45, check.get(Calendar.SECOND));
        }
    }

    @Test
    void shouldGenerateMultiplePlansWithDifferentCombinations() {
        // Arrange
        UserCriterias criterias = new UserCriterias(
            "Paris", "Lyon", new Date(), 2, 300.0,
            3, HotelPriority.CHEAPEST, List.of(ActivityCategory.ANY),
            10.0, JourneyType.INDIFFERENT, TransportPriority.PRICE
        );

        Hotel cheap = createMockHotel(40.0, "Cheap");
        Hotel expensive = createMockHotel(150.0, "Expensive");
        when(hotelService.findBestHotels(anyString(), anyDouble(), any()))
            .thenReturn(List.of(cheap, expensive));

        Itinerary itin1 = createMockItinerary(30.0);
        Itinerary itin2 = createMockItinerary(50.0);
        when(itineraryService.findBestItineraries(anyString(), anyString(), any(), any(), any(), anyDouble()))
            .thenReturn(List.of(itin1, itin2));

        when(activityService.findBestActivities(anyString(), anyString(), anyDouble(), any(), any()))
            .thenReturn(new ArrayList<>());

        // Act
        List<TripPlan> results = tripPlanner.planTrip(criterias);

        // Assert
        assertEquals(4, results.size());
        assertTrue(results.stream().allMatch(plan -> 
            plan.getHotel().getPricePerNight() == 40.0));
    }

    @Test
    void shouldHandleEmptyServiceResults() {
        // Arrange
        UserCriterias criterias = new UserCriterias(
            "Paris", "Lyon", new Date(), 1, 500.0,
            3, HotelPriority.CHEAPEST, List.of(ActivityCategory.ANY),
            10.0, JourneyType.INDIFFERENT, TransportPriority.PRICE
        );

        when(hotelService.findBestHotels(anyString(), anyDouble(), any()))
            .thenReturn(new ArrayList<>());
        when(itineraryService.findBestItineraries(anyString(), anyString(), any(), any(), any(), anyDouble()))
            .thenReturn(new ArrayList<>());

        // Act
        List<TripPlan> results = tripPlanner.planTrip(criterias);

        // Assert
        assertEquals(1, results.size());
        assertFalse(results.get(0).isValid());
        verify(activityService, never()).findBestActivities(anyString(), anyString(), anyDouble(), any(), any());
    }

    @Test
    void shouldHandleZeroBudgetAndRejectExpensiveActivities() {
        // Arrange
        UserCriterias criterias = new UserCriterias(
            "Paris", "Lyon", new Date(), 2, 200.0,
            3, HotelPriority.CHEAPEST, List.of(ActivityCategory.ANY),
            10.0, JourneyType.INDIFFERENT, TransportPriority.PRICE
        );

        setupBasicMocks(50.0, 50.0);

        Activity tooExpensive = mock(Activity.class);
        when(tooExpensive.getPrice()).thenReturn(10.0);
        when(activityService.findBestActivities(anyString(), anyString(), anyDouble(), any(), any()))
            .thenReturn(List.of(tooExpensive));

        // Act
        List<TripPlan> results = tripPlanner.planTrip(criterias);

        // Assert
        assertTrue(results.get(0).isValid());
        assertEquals(0, results.get(0).getActivities().size());
    }

    @Test
    void shouldUseSameInstancesFromServices() {
        // Arrange
        UserCriterias criterias = new UserCriterias(
            "Paris", "Lyon", new Date(), 2, 500.0,
            3, HotelPriority.CHEAPEST, List.of(ActivityCategory.ANY),
            10.0, JourneyType.INDIFFERENT, TransportPriority.PRICE
        );

        Hotel specificHotel = createMockHotel(50.0, "Specific");
        when(hotelService.findBestHotels(anyString(), anyDouble(), any()))
            .thenReturn(List.of(specificHotel));

        Journey journeyTo = mock(Journey.class);
        Journey journeyBack = mock(Journey.class);
        
        Itinerary itinTo = mock(Itinerary.class);
        when(itinTo.getTotalPrice()).thenReturn(30.0);
        when(itinTo.getLastJourney()).thenReturn(journeyTo);
        
        Itinerary itinBack = mock(Itinerary.class);
        when(itinBack.getTotalPrice()).thenReturn(30.0);
        when(itinBack.getLastJourney()).thenReturn(journeyBack);

        when(itineraryService.findBestItineraries(anyString(), anyString(), any(), any(), any(), anyDouble()))
            .thenReturn(List.of(itinTo))
            .thenReturn(List.of(itinBack));

        when(activityService.findBestActivities(anyString(), anyString(), anyDouble(), any(), any()))
            .thenReturn(new ArrayList<>());

        // Act
        List<TripPlan> results = tripPlanner.planTrip(criterias);

        // Assert
        assertSame(specificHotel, results.get(0).getHotel());
        assertSame(journeyTo, results.get(0).getJourneyTo());
        assertSame(journeyBack, results.get(0).getJourneyBack());
    }

    private void setupBasicMocks(double hotelPrice, double itineraryPrice) {
        Hotel mockHotel = createMockHotel(hotelPrice, "Test Address");
        when(hotelService.findBestHotels(anyString(), anyDouble(), any()))
            .thenReturn(List.of(mockHotel));

        Itinerary mockItinerary = createMockItinerary(itineraryPrice);
        when(itineraryService.findBestItineraries(anyString(), anyString(), any(), any(), any(), anyDouble()))
            .thenReturn(List.of(mockItinerary));

        when(activityService.findBestActivities(anyString(), anyString(), anyDouble(), any(), any()))
            .thenReturn(new ArrayList<>());
    }

    private Hotel createMockHotel(double pricePerNight, String address) {
        Hotel hotel = mock(Hotel.class);
        when(hotel.getPricePerNight()).thenReturn(pricePerNight);
        when(hotel.getAddress()).thenReturn(address);
        return hotel;
    }

    private Itinerary createMockItinerary(double totalPrice) {
        Itinerary itinerary = mock(Itinerary.class);
        when(itinerary.getTotalPrice()).thenReturn(totalPrice);
        Journey mockJourney = mock(Journey.class);
        when(itinerary.getLastJourney()).thenReturn(mockJourney);
        return itinerary;
    }
}