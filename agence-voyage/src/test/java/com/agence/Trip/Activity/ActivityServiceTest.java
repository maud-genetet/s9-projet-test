package com.agence.Trip.Activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agence.Geolocation.IGeoService;


public class ActivityServiceTest {

    private ActivityService activityService;
    private IActivityRepository mockRepository;
    private IGeoService mockGeoService;

    private final String CITY_PARIS = "Paris";
    private final String HOTEL_ADDRESS = "10 Rue de la Paix";
    private final Date TARGET_DATE = Date.valueOf("2024-07-15");
    private final Double MAX_DISTANCE = 5.0;

    private void mockDistance(String destinationAddress, double distanceToReturn) {
        when(mockGeoService.getDistanceKm(anyString(), eq(HOTEL_ADDRESS), eq(destinationAddress)))
            .thenReturn(distanceToReturn);
    }

    @BeforeEach
    public void setUp() {
        this.mockRepository = mock(IActivityRepository.class);
        this.mockGeoService = mock(IGeoService.class);
        this.activityService = new ActivityService(mockRepository, mockGeoService);
    }
    
    @Test
    public void shouldReturnMatches_WhenCriteriaAreMet() {
        // Arrange
        Activity cheap = new Activity(CITY_PARIS, "Cheap St", TARGET_DATE, ActivityCategory.CULTURE, 0.01);
        Activity normal = new Activity(CITY_PARIS, "Normal St", TARGET_DATE, ActivityCategory.SPORT, 10.5);
        Activity close = new Activity(CITY_PARIS, "Close St", TARGET_DATE, ActivityCategory.CULTURE, 25.0);
        Activity tooFar = new Activity(CITY_PARIS, "Far St", TARGET_DATE, ActivityCategory.CULTURE, 12.0);
        List<Activity> dbActivities = List.of(cheap, normal, close, tooFar);        

        when(mockRepository.getAllActivities()).thenReturn(dbActivities);
        mockDistance("Cheap St", 4.5);
        mockDistance("Normal St", 4.5);
        mockDistance("Close St", 1.0);
        mockDistance("Far St", 10.0); // Hors limite (> 5.0)

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, MAX_DISTANCE, 
            List.of(ActivityCategory.CULTURE, ActivityCategory.SPORT), TARGET_DATE
        );

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of(cheap, normal, close)));
    }

    @Test
    public void shouldReturnMatch_WhenCityIsTargeted() {
        // Arrange
        Activity lyonActivity = new Activity("Lyon", "Lyon St", TARGET_DATE, ActivityCategory.CULTURE, 20.0);
        Activity parisActivity = new Activity(CITY_PARIS, "Paris St", TARGET_DATE, ActivityCategory.CULTURE, 20.0);

        when(mockRepository.getAllActivities()).thenReturn(List.of(lyonActivity, parisActivity));
        mockDistance("Lyon St", 2.0); // Distance valide simulée

        // Act
        List<Activity> result = activityService.findBestActivities(
            "Lyon", HOTEL_ADDRESS, MAX_DISTANCE, 
            List.of(ActivityCategory.CULTURE), TARGET_DATE
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals(lyonActivity, result.get(0));
    }

    @Test
    public void shouldReturnEmpty_WhenCityNotFound() {
        // Arrange
        Activity parisActivity = new Activity(CITY_PARIS, "Paris St", TARGET_DATE, ActivityCategory.CULTURE, 20.0);
        when(mockRepository.getAllActivities()).thenReturn(List.of(parisActivity));

        // Act
        List<Activity> result = activityService.findBestActivities(
            "Marseille", HOTEL_ADDRESS, MAX_DISTANCE, 
            List.of(ActivityCategory.CULTURE), TARGET_DATE
        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnMatch_WhenCategoryMatches() {
        // Arrange
        Activity adventure = new Activity(CITY_PARIS, "Adv St", TARGET_DATE, ActivityCategory.ADVENTURE, 15.0);
        Activity culture = new Activity(CITY_PARIS, "Cult St", TARGET_DATE, ActivityCategory.CULTURE, 15.0);

        when(mockRepository.getAllActivities()).thenReturn(List.of(adventure, culture));
        mockDistance("Adv St", 2.0);
        mockDistance("Cult St", 2.0);

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, MAX_DISTANCE, 
            List.of(ActivityCategory.ADVENTURE), TARGET_DATE
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals(adventure, result.get(0));
    }

    @Test
    public void shouldReturnEmpty_WhenDistanceExceeded() {
        // Arrange
        Activity farActivity = new Activity(CITY_PARIS, "Far St", TARGET_DATE, ActivityCategory.CULTURE, 10.0);
        when(mockRepository.getAllActivities()).thenReturn(List.of(farActivity));
        
        mockDistance("Far St", 6.0);

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, 2.0, 
            List.of(ActivityCategory.CULTURE), TARGET_DATE
        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnMatch_WhenDistanceIsExactlyMax() {
        // Arrange
        Activity edgeActivity = new Activity(CITY_PARIS, "Edge St", TARGET_DATE, ActivityCategory.CULTURE, 10.0);
        when(mockRepository.getAllActivities()).thenReturn(List.of(edgeActivity));
        
        mockDistance("Edge St", 5.0);

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, 5.0, 
            List.of(ActivityCategory.CULTURE), TARGET_DATE
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals(edgeActivity, result.get(0));
    }

    @Test
    public void shouldReturnEmpty_WhenDateDoesNotMatch() {
        // Arrange
        Activity differentDate = new Activity(CITY_PARIS, "Date St", Date.valueOf("2024-07-16"), ActivityCategory.CULTURE, 18.0);
        when(mockRepository.getAllActivities()).thenReturn(List.of(differentDate));
        mockDistance("Date St", 2.0);

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, MAX_DISTANCE, 
            List.of(ActivityCategory.CULTURE), TARGET_DATE
        );

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnAll_WhenCategoriesListIsEmpty() {
        // Arrange
        Activity sport = new Activity(CITY_PARIS, "Sport St", TARGET_DATE, ActivityCategory.SPORT, 10.0);
        Activity culture = new Activity(CITY_PARIS, "Cult St", TARGET_DATE, ActivityCategory.CULTURE, 10.0);

        when(mockRepository.getAllActivities()).thenReturn(List.of(sport, culture));
        mockDistance("Sport St", 1.0);
        mockDistance("Cult St", 1.0);

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, MAX_DISTANCE, 
            Collections.emptyList(), TARGET_DATE
        );

        // Assert
        assertEquals(2, result.size());
    }
    
    @Test
    public void shouldReturnAll_WhenCategoryIsAny() {
        // Arrange
        Activity sport = new Activity(CITY_PARIS, "Sport St", TARGET_DATE, ActivityCategory.SPORT, 10.0);
        
        when(mockRepository.getAllActivities()).thenReturn(List.of(sport));
        mockDistance("Sport St", 1.0);

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, MAX_DISTANCE, 
            List.of(ActivityCategory.ANY), TARGET_DATE
        );

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    public void shouldReturnAll_WhenMaxDistanceIsZero() {
        // Arrange
        Activity closeActivity = new Activity(CITY_PARIS, "Close St", TARGET_DATE, ActivityCategory.CULTURE, 10.0);
        Activity veryFarActivity = new Activity(CITY_PARIS, "Far Away St", TARGET_DATE, ActivityCategory.CULTURE, 10.0);

        when(mockRepository.getAllActivities()).thenReturn(List.of(closeActivity, veryFarActivity));
        
        mockDistance("Close St", 0.1);
        mockDistance("Far Away St", 100.0);

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, 0.0, 
            List.of(ActivityCategory.CULTURE), TARGET_DATE
        );

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    public void shouldFilterCorrectly_WhenMaxDistanceIsSmall() {
        // Arrange
        Activity veryClose = new Activity(CITY_PARIS, "Very Close St", TARGET_DATE, ActivityCategory.CULTURE, 10.0);
        Activity slightlyFar = new Activity(CITY_PARIS, "Slightly Far St", TARGET_DATE, ActivityCategory.CULTURE, 10.0);

        when(mockRepository.getAllActivities()).thenReturn(List.of(veryClose, slightlyFar));
        
        mockDistance("Very Close St", 0.1);  // Distance très courte
        mockDistance("Slightly Far St", 0.8);

        // Act
        List<Activity> result = activityService.findBestActivities(
            CITY_PARIS, HOTEL_ADDRESS, 0.5, 
            List.of(ActivityCategory.CULTURE), TARGET_DATE
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals(veryClose, result.get(0));
    }
}