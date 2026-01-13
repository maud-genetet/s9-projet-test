package com.agence.Trip;

import java.util.ArrayList;
import java.util.List;

import com.agence.Trip.Activity.Activity;
import com.agence.Trip.Hotel.Hotel;
import com.agence.Trip.Transport.Journey;

public class TripPlan {
    private Hotel hotel;
    private List<Activity> activities;
    private Journey journeyTo;
    private Journey journeyBack;
    private List<String> errors;

    public TripPlan(Hotel hotel, List<Activity> activities, Journey journeyTo, Journey journeyBack) {
        this.hotel = hotel;
        this.activities = activities;
        this.journeyTo = journeyTo;
        this.journeyBack = journeyBack;
        this.errors = new ArrayList<>();
    }

    public Hotel getHotel() {
        return hotel;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public Journey getJourneyTo() {
        return journeyTo;
    }

    public Journey getJourneyBack() {
        return journeyBack;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!isValid()) {
            sb.append("Trip Plan is invalid due to the following errors:\n");
            for (String error : errors) {
                sb.append("- ").append(error).append("\n");
            }
            return sb.toString();
        }

        sb.append("Trip Plan Details:\n");
        sb.append("Hotel: ").append(hotel).append("\n");
        sb.append("Journey To: ").append(journeyTo).append("\n");
        sb.append("Journey Back: ").append(journeyBack).append("\n");
        sb.append("Activities:\n");
        for (Activity activity : activities) {
            sb.append("- ").append(activity).append("\n");
        }
        return sb.toString();
    }
    
}
