package com.agence.Trip.Transport;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class ItineraryService {

    private IJourneyRepository journeyRepository;

    public ItineraryService(IJourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

    public List<Itinerary> findBestItineraries(String departureCity, String arrivalCity, Date departureDate, TransportPriority priority, JourneyType journeyType, double maxPrice) {
        List<Journey> allJourneys = journeyRepository.getAllJourney();
        List<Itinerary> matchingItineraries = new ArrayList<>();

        // Filtrer les journeys qui commencent de la ville de départ et après la date
        List<Journey> afterJourneys = getAllJourneysAfterOrEqualDate(allJourneys, departureDate);
        afterJourneys = getAllJourneysByJourneyType(afterJourneys, journeyType);
        List<Journey> posiblesNextJourneys = getAllJourneysStartFromCity(afterJourneys, departureCity);

        // Pour chaque journey de départ, construire tous les itinéraires complets
        for (Journey startJourney : posiblesNextJourneys) {
            Itinerary itinerary = new Itinerary();
            itinerary.addJourney(startJourney);
            
            // Récupérer tous les itinéraires possibles
            List<Itinerary> allItinerariesFromStart = getAllItineraries(itinerary, afterJourneys, arrivalCity);
            matchingItineraries.addAll(allItinerariesFromStart);
        }

        // Filtrer les itinéraires selon la priorité
        matchingItineraries = deleteItinerariesTooExpensive(matchingItineraries, maxPrice);
        if (priority == TransportPriority.PRICE) {
            matchingItineraries = findItineratiesWithBestPrice(matchingItineraries);
        } else if (priority == TransportPriority.DURATION) {
            matchingItineraries = findItineratiesWithBestDuration(matchingItineraries);
        }

        return matchingItineraries;
    }

    private List<Itinerary> getAllItineraries(Itinerary itinerary, List<Journey> allJourneys, String arrivalCity) {
        List<Itinerary> results = new ArrayList<>();
        Journey lastJourney = itinerary.getLastJourney();

        // Si on a atteint la destination, ajouter cet itinéraire
        if (lastJourney.getArrivalCity().equalsIgnoreCase(arrivalCity)) {
            results.add(itinerary);
            return results;
        }

        // Chercher les prochains journeys possibles
        List<Journey> afterJourneys = getAllJourneysAfterOrEqualDate(allJourneys, lastJourney.getArrivalDate());
        List<Journey> posiblesNextJourneys = getAllJourneysStartFromCity(afterJourneys, lastJourney.getArrivalCity());

        // Pour chaque journey possible, explorer tous les chemins
        for (Journey nextJourney : posiblesNextJourneys) {
            Itinerary newItinerary = itinerary.clone();
            newItinerary.addJourney(nextJourney);

            // Récursivement, ajouter tous les itinéraires trouvés
            List<Itinerary> foundItineraries = getAllItineraries(newItinerary, afterJourneys, arrivalCity);
            results.addAll(foundItineraries);
        }

        return results;
    }

    private List<Journey> getAllJourneysAfterOrEqualDate(List<Journey> journeys, Date date) {
        List<Journey> filteredJourneys = new ArrayList<>();
        for (Journey journey : journeys) {
            if (!journey.getDepartureDate().before(date)) {
                filteredJourneys.add(journey);
            }
        }
        return filteredJourneys;
    }

    private List<Journey> getAllJourneysStartFromCity(List<Journey> journeys, String departureCity) {
        List<Journey> filteredJourneys = new ArrayList<>();
        for (Journey journey : journeys) {
            if (journey.getDepartureCity().equalsIgnoreCase(departureCity)) {
                filteredJourneys.add(journey);
            }
        }
        return filteredJourneys;
    }

    private List<Journey> getAllJourneysByJourneyType(List<Journey> journeys, JourneyType journeyType) {
        if (journeyType == JourneyType.INDIFFERENT) {
            return journeys;
        }

        List<Journey> filteredJourneys = new ArrayList<>();
        for (Journey journey : journeys) {
            if (journey.getJourneyType() == journeyType) {
                filteredJourneys.add(journey);
            }
        }
        return filteredJourneys;
    }

    private List<Itinerary> findItineratiesWithBestPrice(List<Itinerary> itineraries) {
        List<Itinerary> bestItineraries = new ArrayList<>();
        double bestPrice = Double.MAX_VALUE;

        for (Itinerary itinerary : itineraries) {
            double totalPrice = itinerary.getTotalPrice();
            if (totalPrice < bestPrice) {
                bestItineraries.clear();
                bestItineraries.add(itinerary);
                bestPrice = totalPrice;
            } else if (totalPrice == bestPrice) {
                bestItineraries.add(itinerary);
            }
        }

        return bestItineraries;
    }

    private List<Itinerary> findItineratiesWithBestDuration(List<Itinerary> itineraries) {
        List<Itinerary> bestItineraries = new ArrayList<>();
        int bestDuration = Integer.MAX_VALUE;

        for (Itinerary itinerary : itineraries) {
            int duration = itinerary.getDuration();
            if (duration < bestDuration) {
                bestItineraries.clear();
                bestItineraries.add(itinerary);
                bestDuration = duration;
            } else if (duration == bestDuration) {
                bestItineraries.add(itinerary);
            }
        }

        return bestItineraries;
    }

    private List<Itinerary> deleteItinerariesTooExpensive(List<Itinerary> itineraries, double maxPrice) {
        List<Itinerary> filteredItineraries = new ArrayList<>();
        for (Itinerary itinerary : itineraries) {
            if (itinerary.getTotalPrice() <= maxPrice) {
                filteredItineraries.add(itinerary);
            }
        }
        return filteredItineraries;
    }

}