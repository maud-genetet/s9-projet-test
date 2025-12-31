package com.agence.Trip.Transport;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class Itinerary {

    private ArrayList<Journey> journeys;

    public Itinerary() {
        this.journeys = new ArrayList<>();
    }

    public void addJourney(Journey journey) {
        if (journey == null) {
            throw new IllegalArgumentException("Journey cannot be null");
        }
        if (!this.journeys.isEmpty()) {
            Journey lastJourney = this.getLastJourney();
            if (!lastJourney.getArrivalCity().equalsIgnoreCase(journey.getDepartureCity())) {
                throw new IllegalArgumentException("The departure city of the new journey must match the arrival city of the last journey in the itinerary");
            }
            if (journey.getDepartureLocalDateTime().isBefore(lastJourney.getArrivalLocalDateTime())) {
                throw new IllegalArgumentException("The departure time of the new journey must be after or equal the arrival time of the last journey in the itinerary");
            }
        }
        this.journeys.add(journey);
    }

    public int getDurationInMinutes() {
        if (journeys.isEmpty()) {
            return 0;
        }
        LocalDateTime startLocalDateTime = journeys.get(0).getDepartureLocalDateTime();
        LocalDateTime endLocalDateTime = journeys.get(journeys.size() - 1).getArrivalLocalDateTime();
        long ts1 = endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long ts2 = startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long durationInMillis = ts1 - ts2;
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