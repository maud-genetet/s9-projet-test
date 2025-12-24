package com.agence.Trip.Transport;
import java.util.Date;

public class Journey {

    private String departureCity;
    private String arrivalCity;
    private JourneyType journeyType;
    private double price;
    private Date departureDate;
    private Date arrivalDate;

    public Journey(String departureCity, String arrivalCity, JourneyType journeyType, double price, Date departureDate, Date arrivalDate) {
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.journeyType = journeyType;
        this.price = price;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
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

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    @Override
    public String toString() {
        return "Journey [departureCity=" + departureCity + ", arrivalCity=" + arrivalCity + ", journeyType="
                + journeyType + ", price=" + price + ", departureDate=" + departureDate + ", arrivalDate=" + arrivalDate
                + "]";
    }

}
