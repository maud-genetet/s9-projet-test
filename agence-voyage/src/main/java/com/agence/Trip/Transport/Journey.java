package com.agence.Trip.Transport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Journey {

    private String departureCity;
    private String arrivalCity;
    private JourneyType journeyType;
    private double price;
    private LocalDateTime departureLocalDateTime;
    private LocalDateTime arrivalLocalDateTime;

    public Journey(String departureCity, String arrivalCity, JourneyType journeyType, double price, LocalDateTime departureLocalDateTime, LocalDateTime arrivalLocalDateTime) {
        if (departureCity == null || arrivalCity == null || journeyType == null || departureLocalDateTime == null || arrivalLocalDateTime == null) {
            throw new IllegalArgumentException("None of the parameters can be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (arrivalLocalDateTime.isBefore(departureLocalDateTime)) {
            throw new IllegalArgumentException("Arrival time cannot be before departure time");
        }
        if (departureCity.isEmpty() || arrivalCity.isEmpty()) {
            throw new IllegalArgumentException("City names cannot be empty");
        }
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.journeyType = journeyType;
        this.price = price;
        this.departureLocalDateTime = departureLocalDateTime;
        this.arrivalLocalDateTime = arrivalLocalDateTime;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public JourneyType getJourneyType() {
        return journeyType;
    }

    public void setJourneyType(JourneyType journeyType) {
        this.journeyType = journeyType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getDepartureLocalDateTime() {
        return departureLocalDateTime;
    }

    public void setDepartureLocalDateTime(LocalDateTime departureLocalDateTime) {
        this.departureLocalDateTime = departureLocalDateTime;
    }

    public LocalDateTime getArrivalLocalDateTime() {
        return arrivalLocalDateTime;
    }

    public void setArrivalLocalDateTime(LocalDateTime arrivalLocalDateTime) {
        this.arrivalLocalDateTime = arrivalLocalDateTime;
    }

    @Override
    public String toString() {
        return "Journey [departureCity=" + departureCity + ", arrivalCity=" + arrivalCity + ", journeyType="
                + journeyType + ", price=" + price + ", departureLocalDateTime=" + departureLocalDateTime + ", arrivalLocalDateTime=" + arrivalLocalDateTime
                + "]";
    }

}
