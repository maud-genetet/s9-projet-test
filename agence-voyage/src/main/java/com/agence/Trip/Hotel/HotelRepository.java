package com.agence.Trip.Hotel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HotelRepository implements IHotelRepository {

    private List<Hotel> allHotels = new ArrayList<>();

    public HotelRepository(String filePath){
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] data = line.split(",");

                if (data.length < 4) {
                    continue;
                }

                allHotels.add(new Hotel(data[0], data[1], Integer.parseInt(data[2]), Double.parseDouble(data[3])));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
        }
        return;
    }

    @Override
    public List<Hotel> getAllHotels() {
        return allHotels;
    }
    
}
