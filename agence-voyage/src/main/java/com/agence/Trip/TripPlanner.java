package com.agence.Trip;

import com.agence.Trip.Hotel.Hotel;
import com.agence.Trip.Hotel.HotelService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.agence.Trip.Activity.Activity;
import com.agence.Trip.Activity.ActivityService;
import com.agence.Trip.Transport.Itinerary;
import com.agence.Trip.Transport.ItineraryService;

public class TripPlanner {
    private HotelService hotelService;
    private ActivityService activityService;
    private ItineraryService itineraryService;

    public TripPlanner(HotelService hotelService, ActivityService activityService, ItineraryService itineraryService) {
        this.hotelService = hotelService;
        this.activityService = activityService;
        this.itineraryService = itineraryService;
    }

    public List<TripPlan> planTrip(UserCriterias criterias) {
        List<TripPlan> validPlans = new ArrayList<>();

        LocalDateTime startDateTime = criterias.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        List<Itinerary> itinerariesTo = itineraryService.findBestItineraries(
                criterias.getCityFrom(), criterias.getDestinationCity(), startDateTime,
                criterias.getTransportPriority(), criterias.getTransportType(), criterias.getMaxPrice());

        List<Hotel> bestHotels = hotelService.findBestHotels(
                criterias.getDestinationCity(), (double) criterias.getMinHotelRating(), criterias.getHotelPriority());

        Calendar cal = Calendar.getInstance();
        cal.setTime(criterias.getStartDate());
        cal.add(Calendar.DAY_OF_MONTH, criterias.getDuration());
        LocalDateTime returnDateTime = cal.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<Itinerary> itinerariesBack = itineraryService.findBestItineraries(
                criterias.getDestinationCity(), criterias.getCityFrom(), returnDateTime,
                criterias.getTransportPriority(), criterias.getTransportType(), criterias.getMaxPrice());

        for (Itinerary itinTo : itinerariesTo) {
            for (Hotel hotel : bestHotels) {
                for (Itinerary itinBack : itinerariesBack) {
                    
                    double basePrice = itinTo.getTotalPrice() + itinBack.getTotalPrice() + (hotel.getPricePerNight() * criterias.getDuration());
                    
                    if (basePrice <= criterias.getMaxPrice()) {
                        List<Activity> selectedActivities = new ArrayList<>();
                        double remainingBudget = criterias.getMaxPrice() - basePrice;

                        for (int i = 1; i < criterias.getDuration(); i++) {
                            cal.setTime(criterias.getStartDate());
                            cal.add(Calendar.DAY_OF_MONTH, i);
                            Date dayOfTrip = cal.getTime();

                            List<Activity> possibleActs = activityService.findBestActivities(
                                    criterias.getDestinationCity(), hotel.getAddress(),
                                    criterias.getMaxDistance(), criterias.getActivityCategories(), dayOfTrip);

                            // Tri des activités par prix croissant pour prendre la moins chère en premier ??
                            List<Activity> sortedActs = new ArrayList<>(possibleActs);
                            sortedActs.sort(Comparator.comparingDouble(Activity::getPrice));

                            for (Activity act : sortedActs) {
                                if (act.getPrice() <= remainingBudget) {
                                    selectedActivities.add(act);
                                    remainingBudget -= act.getPrice();
                                    break; 
                                }
                            }
                        }

                        validPlans.add(new TripPlan(hotel, selectedActivities, itinTo.getLastJourney(), itinBack.getLastJourney()));
                    }
                }
            }
        }

        if (validPlans.isEmpty()) {
            TripPlan error = new TripPlan(null, null, null, null);
            error.addError("Impossible de créer un forfait respectant le budget de " + criterias.getMaxPrice());
            return List.of(error);
        }

        return validPlans;
    }    
}
