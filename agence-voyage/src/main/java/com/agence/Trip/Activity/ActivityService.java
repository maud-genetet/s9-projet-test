package com.agence.Trip.Activity;

import java.util.Date;
import java.util.List;

import com.agence.Geolocation.IGeoService;

public class ActivityService {
    private final IActivityRepository activityRepository;
    private final IGeoService geoService;

    public ActivityService(IActivityRepository activityRepository, IGeoService geoService) {
        this.activityRepository = activityRepository;
        this.geoService = geoService;
    }

    public List<Activity> findBestActivities(String city, String hotelAddress, double maxDistanceToHotel, List<ActivityCategory> categories, Date date) {
        List<Activity> activities = activityRepository.getAllActivities();
        if (maxDistanceToHotel > 0) {
            activities = filterByMaxDistance(activities, city, hotelAddress, maxDistanceToHotel);
        }
        activities = filterByDate(activities, date);
        if (!categories.isEmpty() && categories.get(0) != ActivityCategory.ANY) { 
            activities = filterByCategory(activities, categories);
        }
        return activities;
    }
    
    private List<Activity> filterByMaxDistance(List<Activity> activities, String city, String centerAddress, double maxDistance) {
        List<Activity> filteredActivities = activities.stream()
                .filter(activity -> activity.getCity().equalsIgnoreCase(city))
                .toList();
        filteredActivities = filteredActivities.stream()
                .filter(activity -> {
                    double distance = geoService.getDistanceKm(city, centerAddress, activity.getAddress());
                    return distance <= maxDistance;
                })
                .toList();
        return filteredActivities;
    }
    
    private List<Activity> filterByDate(List<Activity> activities, Date date) {
        List<Activity> filteredActivities = activities.stream()
                .filter(activity -> activity.getDate().equals(date))
                .toList();
        return filteredActivities;
    }

    private List<Activity> filterByCategory(List<Activity> activities, List<ActivityCategory> categories) {
        List<Activity> filteredActivities = activities.stream()
                .filter(activity -> categories.contains(activity.getCategory()))
                .toList();
        return filteredActivities;
    }
}
