package com.agence.Trip.Transport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JourneyRepository implements IJourneyRepository {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<Journey> AllJourney = new ArrayList<>();

    public JourneyRepository(String filePath){
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] data = line.split(",");

                if (data.length < 6) {
                    continue;
                }

                AllJourney.add(new Journey(
                    data[0],
                    data[1],
                    JourneyType.valueOf(data[2].toUpperCase()),
                    Double.parseDouble(data[3]),
                    LocalDateTime.parse(data[4], FORMATTER),
                    LocalDateTime.parse(data[5], FORMATTER)
                ));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
        }
        return;
    }

    @Override
    public List<Journey> getAllJourney() {
        return AllJourney;
    }
    
}
