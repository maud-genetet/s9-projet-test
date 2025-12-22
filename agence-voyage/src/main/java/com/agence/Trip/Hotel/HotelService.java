package com.agence.Trip.Hotel;

import java.util.List;

public class HotelService {
    private IHotelRepository hotelRepository;

    public HotelService(IHotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> findBestHotels(String city, double rating, HotelPriority priority){
        List<Hotel> hotels = hotelRepository.getAllHotels();
        hotels = filterByCity(hotels, city);
        hotels = filterByMinRating(hotels, rating);
        if (priority == HotelPriority.CHEAPEST) {
            return getCheapestHotels(hotels);
        } else {
            return getHigherRatedHotels(hotels);
        }
    }

    private List<Hotel> filterByCity(List<Hotel> hotels, String city){
        return hotels.stream().filter(hotel -> hotel.getCity().equals(city)).toList();
    }

    private List<Hotel> filterByMinRating(List<Hotel> hotels, double rating){
        return hotels.stream().filter(hotel -> hotel.getRating() >= rating).toList();
    }

    private List<Hotel> getCheapestHotels(List<Hotel> hotels){
        double minPrice = hotels.stream().mapToDouble(Hotel::getPricePerNight).min().orElse(Double.MAX_VALUE);
        return hotels.stream().filter(hotel -> hotel.getPricePerNight() == minPrice).toList();
    }

    private List<Hotel> getHigherRatedHotels(List<Hotel> hotels){
        double maxRate = hotels.stream().mapToDouble(Hotel::getRating).max().orElse(6);
        return hotels.stream().filter(hotel -> hotel.getRating() == maxRate).toList();
    }
}
