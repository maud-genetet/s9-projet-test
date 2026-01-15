package com.agence.Trip;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;
import com.agence.Trip.Hotel.HotelPriority;
import com.agence.Trip.Activity.ActivityCategory;
import com.agence.Trip.Transport.JourneyType;
import com.agence.Trip.Transport.TransportPriority;

class UserCriteriasTest {

    @Test
    void shouldCorrectlyStoreAndRetrieveAllCriterias() {
        // Arrange
        String dest = "Paris";
        String from = "Lyon";
        Date start = new Date(123456789L);
        int duration = 5;
        double maxPrice = 1500.0;
        int minRating = 4;
        HotelPriority hotelPrio = HotelPriority.HIGHEST_RATED;
        List<ActivityCategory> cats = List.of(ActivityCategory.CULTURE, ActivityCategory.SPORT);
        double maxDist = 12.5;
        JourneyType transportType = JourneyType.TRAIN;
        TransportPriority transportPrio = TransportPriority.DURATION;

        // Act
        UserCriterias criterias = new UserCriterias(
            dest, from, start, duration, maxPrice, 
            minRating, hotelPrio, cats, maxDist, 
            transportType, transportPrio
        );

        // Assert
        assertAll("Verify all fields to kill PIT mutants",
            () -> assertEquals(dest, criterias.getDestinationCity()),
            () -> assertEquals(from, criterias.getCityFrom()),
            () -> assertEquals(start, criterias.getStartDate()),
            () -> assertEquals(duration, criterias.getDuration()),
            () -> assertEquals(maxPrice, criterias.getMaxPrice()),
            () -> assertEquals(minRating, criterias.getMinHotelRating()),
            () -> assertEquals(hotelPrio, criterias.getHotelPriority()),
            () -> assertEquals(cats, criterias.getActivityCategories()),
            () -> assertEquals(maxDist, criterias.getMaxDistance()),
            () -> assertEquals(transportType, criterias.getTransportType()),
            () -> assertEquals(transportPrio, criterias.getTransportPriority())
        );
    }
}