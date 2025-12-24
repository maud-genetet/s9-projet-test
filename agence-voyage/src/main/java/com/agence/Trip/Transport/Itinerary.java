package com.agence.Trip.Transport;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class Itinerary {

    private ArrayList<Journey> journeys;

    public Itinerary() {
        this.journeys = new ArrayList<>();
    }

    public void addJourney(Journey journey) {
        this.journeys.add(journey);
    }

    public int getDuration() {
        if (journeys.isEmpty()) {
            return 0;
        }
        Date startDate = journeys.get(0).getDepartureDate();
        Date endDate = journeys.get(journeys.size() - 1).getArrivalDate();
        long durationInMillis = endDate.getTime() - startDate.getTime();
        return (int) (durationInMillis / (1000 * 60));
    }

    public double getTotalPrice() {
        double total = 0;
        for (Journey journey : journeys) {
            total += journey.getPrice();
        }
        return total;
    }

    public Journey getLastJourney() {
        if (journeys.isEmpty()) {
            return null;
        }
        return journeys.get(journeys.size() - 1);
    }

    public Itinerary clone() {
        Itinerary newItinerary = new Itinerary();
        for (Journey journey : this.journeys) {
            newItinerary.addJourney(journey);
        }
        return newItinerary;
    }
}