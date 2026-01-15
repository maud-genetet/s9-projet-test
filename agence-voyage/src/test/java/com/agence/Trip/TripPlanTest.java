package com.agence.Trip;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.agence.Trip.Activity.Activity;
import com.agence.Trip.Hotel.Hotel;
import com.agence.Trip.Transport.Journey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class TripPlanTest {

    private Hotel mockHotel;
    private Journey mockJourneyTo;
    private Journey mockJourneyBack;
    private Activity mockActivity;

    @BeforeEach
    void setUp() {
        mockHotel = mock(Hotel.class);
        mockJourneyTo = mock(Journey.class);
        mockJourneyBack = mock(Journey.class);
        mockActivity = mock(Activity.class);

        when(mockHotel.toString()).thenReturn("MockHotel");
        when(mockJourneyTo.toString()).thenReturn("JourneyTo");
        when(mockJourneyBack.toString()).thenReturn("JourneyBack");
        when(mockActivity.toString()).thenReturn("MockActivity");
    }

    @Test
    void testValidTripPlan() {
        // Arrange
        List<Activity> activities = List.of(mockActivity);
        
        // Act
        TripPlan plan = new TripPlan(mockHotel, activities, mockJourneyTo, mockJourneyBack);

        // Assert
        assertEquals(mockHotel, plan.getHotel());
        assertEquals(activities, plan.getActivities());
        assertEquals(mockJourneyTo, plan.getJourneyTo());
        assertEquals(mockJourneyBack, plan.getJourneyBack());

        assertTrue(plan.isValid());

        String output = plan.toString();
        assertTrue(output.contains("Trip Plan Details:"));
        assertTrue(output.contains("MockHotel"));
        assertTrue(output.contains("- MockActivity"));
    }

    @Test
    void shouldReturnExactFormattingForValidTripPlan() {
        // Arrange
        Hotel mockHotel = mock(Hotel.class);
        when(mockHotel.toString()).thenReturn("Hotel ABC");
        
        Journey mockTo = mock(Journey.class);
        when(mockTo.toString()).thenReturn("Train123");
        
        Journey mockBack = mock(Journey.class);
        when(mockBack.toString()).thenReturn("Avion456");
        
        Activity mockAct = mock(Activity.class);
        when(mockAct.toString()).thenReturn("Concert");
        
        TripPlan plan = new TripPlan(mockHotel, List.of(mockAct), mockTo, mockBack);

        // Act
        String result = plan.toString();

        // Assert
        String expected = "Trip Plan Details:\n" +
                        "Hotel: Hotel ABC\n" +
                        "Journey To: Train123\n" +
                        "Journey Back: Avion456\n" +
                        "Activities:\n" +
                        "- Concert\n";
                        
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnExactFormattingForInvalidTripPlan() {
        // Arrange
        TripPlan plan = new TripPlan(null, new ArrayList<>(), null, null);
        plan.addError("Erreur Budget");

        // Act
        String result = plan.toString();

        // Assert
        String expected = "Trip Plan is invalid due to the following errors:\n" +
                        "- Erreur Budget\n";
                        
        assertEquals(expected, result);
    }
}