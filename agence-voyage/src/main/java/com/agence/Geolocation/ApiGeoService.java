package com.agence.Geolocation;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiGeoService implements IGeoService {

    private final String apiKey;
    private final String baseUrl = "https://geocode.maps.co/search";
    static final long REQUEST_INTERVAL_MS = 1000;
    private long lastRequestTime = 0;

    ApiGeoService() {
        Dotenv dotenv = Dotenv.load();
        apiKey = dotenv.get("GEO_API_KEY");
    }

    public Coordinates getCoordinates(String city, String address) {
        if (
            city == null || city.isEmpty() || 
            address == null || address.isEmpty()
        ) {
            return new Coordinates(0.0, 0.0);
        }

        long now = System.currentTimeMillis();
        long interval = now - this.lastRequestTime;
        if (interval < REQUEST_INTERVAL_MS) {
            try {
                Thread.sleep(REQUEST_INTERVAL_MS - interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        this.lastRequestTime = System.currentTimeMillis();

        String apiKey = this.apiKey;
        String requestAddress = String.format("%s %s", city, address);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(String.format(baseUrl + "?q=%s&api_key=%s", requestAddress, apiKey))
            .build();

        double lat = 0.0;
        double lon = 0.0;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Geocoding API request failed: " + response);
                return new Coordinates(0.0, 0.0);
            }

            String responseBody = response.body().string();
            JSONArray jArray = new JSONArray(responseBody);

            if (jArray.length() == 0) {
                System.err.println("No geocoding results found for address: " + requestAddress);
                return new Coordinates(0.0, 0.0);
            }

            JSONObject jObject = jArray.getJSONObject(0);
            lat = jObject.getDouble("lat");
            lon = jObject.getDouble("lon");
        } catch (IOException e) {
            System.err.println("Error during Geocoding API request: " + e.getMessage());
            e.printStackTrace();
        }
        return new Coordinates(lat, lon);
    }

    public double getDistanceKm(String city, String address1, String address2) {
        Coordinates coord1 = getCoordinates(city, address1);
        Coordinates coord2 = getCoordinates(city, address2);

        double lat1 = Math.toRadians(coord1.getLatitude());
        double lon1 = Math.toRadians(coord1.getLongitude());
        double lat2 = Math.toRadians(coord2.getLatitude());
        double lon2 = Math.toRadians(coord2.getLongitude());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.pow(Math.sin(dlat / 2), 2) +
                    Math.cos(lat1) * Math.cos(lat2) *
                    Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        double earthRadiusKm = 6371.0; 
        return earthRadiusKm * c;
    } 
}
