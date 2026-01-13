package com.agence.Trip.Transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
        assertEquals(50.00, firstJourney.getPrice());
    }

    @Test
    void testJourneyRepositoryAllCitiesLoaded() {
        JourneyRepository repository = new JourneyRepository(testFilePath);

        // Vérifier que tous les trajets sont chargés correctement
        assertEquals(10, repository.getAllJourney().size());
        
        // Vérifier quelques trajets spécifiques
        Journey secondJourney = repository.getAllJourney().get(1);
        assertEquals("Paris", secondJourney.getDepartureCity());
        assertEquals("Marseille", secondJourney.getArrivalCity());
        assertEquals(JourneyType.PLANE, secondJourney.getJourneyType());
        assertEquals(120.00, secondJourney.getPrice());
        
        Journey lastJourney = repository.getAllJourney().get(9);
        assertEquals("Bordeaux", lastJourney.getDepartureCity());
        assertEquals("Marseille", lastJourney.getArrivalCity());
        assertEquals(JourneyType.TRAIN, lastJourney.getJourneyType());
        assertEquals(90.00, lastJourney.getPrice());
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
        
        // 50 + 120 + 35 + 60 + 45 + 150 + 25 + 80 + 45 + 90 = 700
        assertEquals(700.00, totalPrice);
    }
}