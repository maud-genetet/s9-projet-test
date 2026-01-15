package com.agence.Trip.Activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ActivityRepository implements IActivityRepository {
    private List<Activity> allActivities;

    public ActivityRepository(String filePath) {
        this.allActivities = new ArrayList<>();
        String line;
        String separator = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                if (line.startsWith("city")) { continue; }
                if (line.trim().isEmpty()) { continue; }

                String[] data = line.split(separator);

                String city = data[0];
                String address = data[1];
                String dateString = data[2];
                String categoryString = data[3];
                String priceString = data[4];

                Date date = Date.valueOf(dateString);
                ActivityCategory category = ActivityCategory.valueOf(categoryString);
                double price = Double.parseDouble(priceString);

                Activity activity = new Activity(city, address, date, category, price);
                allActivities.add(activity);                
            }
        } catch (IOException e) {
            System.err.println("Error reading file \"" + filePath + "\"");
        } 
    }

    @Override
    public List<Activity> getAllActivities() {
        return allActivities;
    }
}
