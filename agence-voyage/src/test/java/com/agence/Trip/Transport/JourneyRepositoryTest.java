package com.agence.Trip.Transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;

public class JourneyRepositoryTest {

    final String testFilePath = "src/test/resources/com/agence/Trip/Transport/journey_test.csv";
    final String badFormatFilePath = "src/test/resources/com/agence/Trip/Transport/bad_format_test.csv";
    final String emptyFilePath = "src/test/resources/com/agence/Trip/Transport/empty_test.csv";
    final String nonExistentFilePath = "src/test/resources/com/agence/Trip/Transport/non_existent_file.csv";

    @Test
    void testJourneyRepositoryInitializationSize() {
        JourneyRepository repository = new JourneyRepository(testFilePath);
        
        assertEquals(10, repository.getAllJourney().size());
    }

    @Test
    void testJourneyRepositoryInitializationContent() {
        JourneyRepository repository = new JourneyRepository(testFilePath);

        Journey firstJourney = repository.getAllJourney().get(0);
        assertEquals("Paris", firstJourney.getDepartureCity());
        assertEquals("Lyon", firstJourney.getArrivalCity());
        assertEquals(JourneyType.TRAIN, firstJourney.getJourneyType());
        assertEquals(55.99, firstJourney.getPrice());
    }

    @Test
    void testJourneyRepositoryAllCitiesLoaded() {
        JourneyRepository repository = new JourneyRepository(testFilePath);

        // Vérifier que tous les trajets sont chargés correctement
        assertEquals(10, repository.getAllJourney().size());
        
        // Verifier tous les trajet
        /*Paris,Lyon,TRAIN,55.99,2025-12-25 08:00:00,2025-12-25 11:30:00
Paris,Marseille,PLANE,120.00,2025-12-25 10:00:00,2025-12-25 12:00:00
Lyon,Marseille,TRAIN,35.00,2025-12-25 12:00:00,2025-12-25 14:30:00
Lyon,Nice,TRAIN,60.00,2025-12-25 13:00:00,2025-12-25 16:00:00
Marseille,Nice,PLANE,45.00,2025-12-25 15:00:00,2025-12-25 15:45:00
Paris,Nice,PLANE,150.00,2025-12-25 11:00:00,2025-12-25 13:00:00
Nice,Monaco,TRAIN,25.00,2025-12-25 17:00:00,2025-12-25 17:45:00
Marseille,Monaco,PLANE,80.00,2025-12-25 16:00:00,2025-12-25 16:30:00
Paris,Bordeaux,TRaIN,45.00,2025-12-25 09:00:00,2025-12-25 11:00:00
Bordeaux,Marseille,TRAIN,90.00,2025-12-25 12:00:00,2025-12-25 16:00:00 */
        Journey firstJourney = repository.getAllJourney().get(0);
        assertEquals("Paris", firstJourney.getDepartureCity());
        assertEquals("Lyon", firstJourney.getArrivalCity());
        assertEquals(JourneyType.TRAIN, firstJourney.getJourneyType());
        assertEquals(55.99, firstJourney.getPrice());
        assertEquals("2025-12-25 08:00", firstJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 11:30", firstJourney.getArrivalLocalDateTime().toString().replace('T', ' '));

        Journey secondJourney = repository.getAllJourney().get(1);
        assertEquals("Paris", secondJourney.getDepartureCity());
        assertEquals("Marseille", secondJourney.getArrivalCity());
        assertEquals(JourneyType.PLANE, secondJourney.getJourneyType());
        assertEquals(120.00, secondJourney.getPrice());
        assertEquals("2025-12-25 10:00", secondJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 12:00", secondJourney.getArrivalLocalDateTime().toString().replace('T', ' '));

        Journey thirdJourney = repository.getAllJourney().get(2);
        assertEquals("Lyon", thirdJourney.getDepartureCity());
        assertEquals("Marseille", thirdJourney.getArrivalCity());
        assertEquals(JourneyType.TRAIN, thirdJourney.getJourneyType());
        assertEquals(35.00, thirdJourney.getPrice());
        assertEquals("2025-12-25 12:00", thirdJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 14:30", thirdJourney.getArrivalLocalDateTime().toString().replace('T', ' '));

        Journey fourthJourney = repository.getAllJourney().get(3);
        assertEquals("Lyon", fourthJourney.getDepartureCity());
        assertEquals("Nice", fourthJourney.getArrivalCity());
        assertEquals(JourneyType.TRAIN, fourthJourney.getJourneyType());
        assertEquals(60.00, fourthJourney.getPrice());
        assertEquals("2025-12-25 13:00", fourthJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 16:00", fourthJourney.getArrivalLocalDateTime().toString().replace('T', ' '));  

        Journey fifthJourney = repository.getAllJourney().get(4);
        assertEquals("Marseille", fifthJourney.getDepartureCity());
        assertEquals("Nice", fifthJourney.getArrivalCity());
        assertEquals(JourneyType.PLANE, fifthJourney.getJourneyType());
        assertEquals(45.00, fifthJourney.getPrice());
        assertEquals("2025-12-25 15:00", fifthJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 15:45", fifthJourney.getArrivalLocalDateTime().toString().replace('T', ' '));

        Journey sixthJourney = repository.getAllJourney().get(5);
        assertEquals("Paris", sixthJourney.getDepartureCity());
        assertEquals("Nice", sixthJourney.getArrivalCity());
        assertEquals(JourneyType.PLANE, sixthJourney.getJourneyType());
        assertEquals(150.00, sixthJourney.getPrice());
        assertEquals("2025-12-25 11:00", sixthJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 13:00", sixthJourney.getArrivalLocalDateTime().toString().replace('T', ' '));

        Journey seventhJourney = repository.getAllJourney().get(6);
        assertEquals("Nice", seventhJourney.getDepartureCity());
        assertEquals("Monaco", seventhJourney.getArrivalCity());
        assertEquals(JourneyType.TRAIN, seventhJourney.getJourneyType());
        assertEquals(25.00, seventhJourney.getPrice());
        assertEquals("2025-12-25 17:00", seventhJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 17:45", seventhJourney.getArrivalLocalDateTime().toString().replace('T', ' '));

        Journey eighthJourney = repository.getAllJourney().get(7);
        assertEquals("Marseille", eighthJourney.getDepartureCity());
        assertEquals("Monaco", eighthJourney.getArrivalCity());
        assertEquals(JourneyType.PLANE, eighthJourney.getJourneyType());
        assertEquals(80.00, eighthJourney.getPrice());
        assertEquals("2025-12-25 16:00", eighthJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 16:30", eighthJourney.getArrivalLocalDateTime().toString().replace('T', ' '));

        Journey ninthJourney = repository.getAllJourney().get(8);
        assertEquals("Paris", ninthJourney.getDepartureCity());
        assertEquals("Bordeaux", ninthJourney.getArrivalCity());
        assertEquals(JourneyType.TRAIN, ninthJourney.getJourneyType());
        assertEquals(45.00, ninthJourney.getPrice());   
        assertEquals("2025-12-25 09:00", ninthJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 11:00", ninthJourney.getArrivalLocalDateTime().toString().replace('T', ' '));
        
        Journey lastJourney = repository.getAllJourney().get(9);
        assertEquals("Bordeaux", lastJourney.getDepartureCity());
        assertEquals("Marseille", lastJourney.getArrivalCity());
        assertEquals(JourneyType.TRAIN, lastJourney.getJourneyType());
        assertEquals(90.00, lastJourney.getPrice());
        assertEquals("2025-12-25 12:00", lastJourney.getDepartureLocalDateTime().toString().replace('T', ' '));
        assertEquals("2025-12-25 16:00", lastJourney.getArrivalLocalDateTime().toString().replace('T', ' '));
    }

    @Test
    void testJourneyRepositoryBadFormatFileSize() {
        JourneyRepository repository = new JourneyRepository(badFormatFilePath);
        // Le fichier ne contient qu'une ligne malformée, donc 0 trajet valide
        assertEquals(0, repository.getAllJourney().size());
    }

    @Test
    void testJourneyRepositoryEmptyFileSize() {
        JourneyRepository repository = new JourneyRepository(emptyFilePath);
        assertEquals(0, repository.getAllJourney().size());
    }

    @Test
    void testJourneyRepositoryNonExistentFileSize() {
        JourneyRepository repository = new JourneyRepository(nonExistentFilePath);
        assertEquals(0, repository.getAllJourney().size());
    }

    @Test
    void testErrorLoggingOutput() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            new JourneyRepository(nonExistentFilePath);

            String output = errContent.toString();
            
            assertTrue(output.contains("Error reading file"), "Le début du message doit être présent");
            assertTrue(output.contains(nonExistentFilePath), "Le chemin du fichier doit être présent");
            
            boolean containsSystemErrorEn = output.contains("(No such file or directory)");
            boolean containsSystemErrorFr = output.contains("(Aucun fichier ou dossier de ce nom)");
            boolean containsSystemError = containsSystemErrorEn || containsSystemErrorFr;
                                       
            assertTrue(containsSystemError);
            
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void testTotalPriceCalculation() {
        JourneyRepository repository = new JourneyRepository(testFilePath);
        
        double totalPrice = 0;
        for (Journey journey : repository.getAllJourney()) {
            totalPrice += journey.getPrice();
        }
        
        // 55.99 + 120 + 35 + 60 + 45 + 150 + 25 + 80 + 45 + 90 = 700
        assertEquals(705.99, totalPrice);
    }

    @Test
    public void testJourneyTypeIsCaseInsensitive() {
        JourneyRepository repository = new JourneyRepository(testFilePath);
        
        List<Journey> journeys = repository.getAllJourney();
        
        assertEquals(10, journeys.size(), "Doit charger 10 journeys");
        
        assertTrue(journeys.stream()
            .anyMatch(j -> j.getJourneyType() == JourneyType.PLANE),
            "Au moins un trajet doit avoir le type PLANE");
        assertTrue(journeys.stream()
            .anyMatch(j -> j.getJourneyType() == JourneyType.TRAIN),
            "Au moins un trajet doit avoir le type TRAIN");
    }

    @Test
    public void testJourneyDistanceCorrectlyParsed() {
        JourneyRepository repository = new JourneyRepository(testFilePath);
        
        List<Journey> journeys = repository.getAllJourney();
        
        assertEquals(10, journeys.size(), "Doit avoir 10 journeys");
        
        Journey firstJourney = journeys.get(0);
        assertEquals(55.99, firstJourney.getPrice(), 0.01, 
                "Le prix du premier trajet doit être 55.99 parsé depuis data[3]");
        
        for (Journey journey : journeys) {
            assertNotEquals(0.0, journey.getPrice(), 
                    "Le prix ne doit pas être 0");
            assertTrue(journey.getPrice() > 0, 
                    "Le prix doit être positif");
        }
    }

    @Test
    void testJourneyPriceIsFromCorrectCsvColumn() {
        JourneyRepository repository = new JourneyRepository(testFilePath);

        Journey journey = repository.getAllJourney().get(4); // Marseille → Nice

        assertEquals(
            45.00,
            journey.getPrice(),
            0.001,
            "Le prix doit provenir exactement de la colonne data[3]"
        );
    }

}