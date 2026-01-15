package com.agence;

import org.json.JSONObject;
import com.agence.Trip.Activity.IActivityRepository;
import com.agence.Trip.TripPlan;
import com.agence.Trip.TripPlanner;
import com.agence.Trip.UserCriterias;
import com.agence.Trip.Activity.ActivityCategory;
import com.agence.Trip.Activity.ActivityRepository;
import com.agence.Trip.Activity.ActivityService;
import com.agence.Trip.Hotel.IHotelRepository;
import com.agence.Trip.Hotel.HotelPriority;
import com.agence.Trip.Hotel.HotelRepository;
import com.agence.Trip.Hotel.HotelService;
import com.agence.Trip.Transport.IJourneyRepository;
import com.agence.Trip.Transport.ItineraryService;
import com.agence.Trip.Transport.JourneyRepository;
import com.agence.Trip.Transport.JourneyType;
import com.agence.Trip.Transport.TransportPriority;
import com.agence.Geolocation.IGeoService;
import com.agence.Geolocation.ApiGeoService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class App {

    public static void main(String[] args) {
        if (args.length < 5) {
            System.err.println("Usage: java App <activitiesCSVPath> <hotelsCSVPath> <journeysCSVPath> <userRequestsJSONPath> <outputJSONPath>");
            System.exit(1);
        }

        try {
            String activityDataPath = args[0];
            String hotelDataPath = args[1];
            String journeyDataPath = args[2];
            String userRequestsPath = args[3];
            String outputFilePath = args[4];

            TripPlanner tripPlanner = createTripPlanner(activityDataPath, hotelDataPath, journeyDataPath);

            UserCriterias userCriterias = loadCriterias(userRequestsPath);

            List<TripPlan> tripPlans = tripPlanner.planTrip(userCriterias);

            writePlanToJson(tripPlans, outputFilePath);

        } catch (Exception e) {
            System.err.println("Error (" + e.getClass().getSimpleName() + "): " + e.getMessage());
        }
    }

    private static TripPlanner createTripPlanner(String activityPath, String hotelPath, String journeyPath) {
        IActivityRepository activityRepository = new ActivityRepository(activityPath);
        IHotelRepository hotelRepository = new HotelRepository(hotelPath);
        IJourneyRepository journeyRepository = new JourneyRepository(journeyPath);

        IGeoService apiGeoService = new ApiGeoService();
        ActivityService activityService = new ActivityService(activityRepository, apiGeoService);
        HotelService hotelService = new HotelService(hotelRepository);
        ItineraryService itineraryService = new ItineraryService(journeyRepository);

        return new TripPlanner(hotelService, activityService, itineraryService);
    }

    private static UserCriterias loadCriterias(String criteriasFilePath) throws IOException {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(criteriasFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(jsonContent.toString());
        
        String destinationCity = jsonObject.getString("destinationCity");
        String cityFrom = jsonObject.getString("cityFrom");
        Date startDate = null;
        try {
            String dateStr = jsonObject.getString("startDate");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            startDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int duration = jsonObject.getInt("duration");
        double maxPrice = jsonObject.getDouble("maxPrice");
        int minHotelRating = jsonObject.getInt("minHotelRating");
        HotelPriority hotelPriority = HotelPriority.valueOf(jsonObject.getString("hotelPriority"));
        List<ActivityCategory> activityCategories = jsonObject.getJSONArray("activityCategories").toList().stream()
                .map(obj -> ActivityCategory.valueOf((String) obj))
                .toList();
        double maxDistance = jsonObject.getDouble("maxDistance");
        JourneyType transportType = JourneyType.valueOf(jsonObject.getString("transportType"));
        TransportPriority transportPriority = TransportPriority.valueOf(jsonObject.getString("transportPriority"));
        
        return new UserCriterias(
                destinationCity, cityFrom, startDate, duration, maxPrice,
                minHotelRating, hotelPriority, activityCategories,
                maxDistance, transportType, transportPriority
        );
    }

    private static void writePlanToJson(List<TripPlan> tripPlans, String outputPath) throws IOException {
        JSONObject jsonObject = new JSONObject();
        
        for (int i = 0; i < tripPlans.size(); i++) {
            jsonObject.put("tripPlan_" + (i + 1), tripPlans.get(i).toString());
        }
        
        try (FileWriter fileWriter = new FileWriter(outputPath)) {
            fileWriter.write(jsonObject.toString(4));
            System.out.println("Result saved to " + outputPath);
        }
    }
}