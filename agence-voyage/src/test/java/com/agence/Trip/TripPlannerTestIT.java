package com.agence.Trip;

import com.agence.Geolocation.ApiGeoService;
import com.agence.Trip.Activity.ActivityCategory;
import com.agence.Trip.Activity.ActivityRepository;
import com.agence.Trip.Activity.ActivityService;
import com.agence.Trip.Hotel.HotelPriority;
import com.agence.Trip.Hotel.HotelRepository;
import com.agence.Trip.Hotel.HotelService;
import com.agence.Trip.Transport.ItineraryService;
import com.agence.Trip.Transport.JourneyRepository;
import com.agence.Trip.Transport.JourneyType;
import com.agence.Trip.Transport.TransportPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TripPlannerTestIT {

    private TripPlanner tripPlanner;

    @BeforeEach
    void setUp() {
        String activityCsv = "src/test/resources/com/agence/Trip/it_activities_test.csv";
        String hotelCsv = "src/test/resources/com/agence/Trip/it_hotels_test.csv";
        String journeyCsv = "src/test/resources/com/agence/Trip/it_journey_test.csv";

        ActivityRepository activityRepo = new ActivityRepository(activityCsv);
        HotelRepository hotelRepo = new HotelRepository(hotelCsv);
        JourneyRepository journeyRepo = new JourneyRepository(journeyCsv);
        
        ApiGeoService geoService = new ApiGeoService();
        ActivityService activityService = new ActivityService(activityRepo, geoService);
        HotelService hotelService = new HotelService(hotelRepo);
        ItineraryService itineraryService = new ItineraryService(journeyRepo);

        this.tripPlanner = new TripPlanner(hotelService, activityService, itineraryService);
    }

    private Date getFixedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Test
    void shouldReturnValidTripPlanWhenCriteriaAreMet() {
        UserCriterias criterias = new UserCriterias(
            "Nice", "Paris", getFixedDate(), 3, 
            2000.0, 
            3, HotelPriority.CHEAPEST, 
            List.of(ActivityCategory.CULTURE), 
            20.0, // On remet une distance > 0 pour activer le filtre par VILLE
            JourneyType.PLANE, TransportPriority.PRICE
        );

        List<TripPlan> results = tripPlanner.planTrip(criterias);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        TripPlan plan = results.get(0);
        assertTrue(plan.isValid());
        assertEquals("Nice", plan.getHotel().getCity());
        assertFalse(plan.getActivities().isEmpty());
        assertEquals("Musée Matisse", plan.getActivities().get(0).getAddress());
    }

    @Test
    void shouldReturnErrorPlanWhenBudgetIsTooLowForTransportAndHotel() {
        UserCriterias tightBudget = new UserCriterias(
            "Nice", "Paris", getFixedDate(), 3, 
            50.0, 
            3, HotelPriority.CHEAPEST, List.of(ActivityCategory.CULTURE), 
            20.0, 
            JourneyType.PLANE, TransportPriority.PRICE
        );

        List<TripPlan> results = tripPlanner.planTrip(tightBudget);

        assertFalse(results.isEmpty());
        assertFalse(results.get(0).isValid());
        assertFalse(results.get(0).getErrors().isEmpty());
    }

    @Test
    void shouldReturnErrorWhenNoHotelMatchesRating() {
        UserCriterias luxuryCriterias = new UserCriterias(
            "Nice", "Paris", getFixedDate(), 3, 
            2000.0, 
            5, 
            HotelPriority.CHEAPEST, List.of(ActivityCategory.CULTURE), 
            20.0, 
            JourneyType.PLANE, TransportPriority.PRICE
        );

        List<TripPlan> results = tripPlanner.planTrip(luxuryCriterias);

        assertFalse(results.get(0).isValid());
    }

    @Test
    void shouldReturnErrorWhenNoTransportMatchesRoute() {
        UserCriterias wrongDest = new UserCriterias(
            "Bordeaux", "Paris", getFixedDate(), 3, 
            2000.0, 3, HotelPriority.CHEAPEST, List.of(ActivityCategory.CULTURE), 
            20.0, 
            JourneyType.PLANE, TransportPriority.PRICE
        );

        List<TripPlan> results = tripPlanner.planTrip(wrongDest);

        assertFalse(results.get(0).isValid());
    }

    @Test
    void shouldReturnValidPlanWithEmptyActivitiesWhenBudgetIsTight() {
        // Budget 600.0 exact (Vol 300 + Hotel 300). Reste 0 pour l'activité.
        UserCriterias tightActivityBudget = new UserCriterias(
            "Nice", "Paris", getFixedDate(), 3, 
            600.0, 
            3, HotelPriority.CHEAPEST, 
            List.of(ActivityCategory.CULTURE), 
            20.0, 
            JourneyType.PLANE, TransportPriority.PRICE
        );

        List<TripPlan> results = tripPlanner.planTrip(tightActivityBudget);

        assertFalse(results.isEmpty());
        TripPlan plan = results.get(0);
        
        assertTrue(plan.isValid());
        assertNotNull(plan.getHotel());
        assertTrue(plan.getActivities().isEmpty(), "Activités doivent être vides car budget insuffisant");
    }
}