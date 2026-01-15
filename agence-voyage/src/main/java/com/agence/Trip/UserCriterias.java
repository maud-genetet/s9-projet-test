package com.agence.Trip;

import java.util.Date;

import com.agence.Trip.Hotel.HotelPriority;
import com.agence.Trip.Transport.JourneyType;
import com.agence.Trip.Transport.TransportPriority;
import com.agence.Trip.Activity.ActivityCategory;
import java.util.List;

public class UserCriterias {
    private String destinationCity;
    private String cityFrom;
    private Date startDate;
    private int duration;
    private double maxPrice;
    private int minHotelRating;
    private HotelPriority hotelPriority;
    private List<ActivityCategory> activityCategories;
    private double maxDistance;
    private JourneyType transportType;
    private TransportPriority transportPriority;

    public UserCriterias(String destinationCity, String cityFrom, Date startDate, int duration, double maxPrice,
            int minHotelRating, HotelPriority hotelPriority, List<ActivityCategory> activityCategories,
            double maxDistance, JourneyType transportType, TransportPriority transportPriority) {
        this.destinationCity = destinationCity;
        this.cityFrom = cityFrom;
        this.startDate = startDate;
        this.duration = duration;
        this.maxPrice = maxPrice;
        this.minHotelRating = minHotelRating;
        this.hotelPriority = hotelPriority;
        this.activityCategories = activityCategories;
        this.maxDistance = maxDistance;
        this.transportType = transportType;
        this.transportPriority = transportPriority;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public String getCityFrom() {
        return cityFrom;
    }

    public Date getStartDate() {
        return startDate;
    }

    public int getDuration() {
        return duration;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public int getMinHotelRating() {
        return minHotelRating;
    }

    public HotelPriority getHotelPriority() {
        return hotelPriority;
    }

    public List<ActivityCategory> getActivityCategories() {
        return activityCategories;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public JourneyType getTransportType() {
        return transportType;
    }

    public TransportPriority getTransportPriority() {
        return transportPriority;
    }
}
