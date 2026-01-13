package com.agence.Trip.Hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

public class HotelRepositoryTest {

    final String testFilePath = "src/test/resources/com/agence/Trip/Hotel/hotels_test.csv";
    final String badFormatFilePath = "src/test/resources/com/agence/Trip/Hotel/bad_format_test.csv";
    final String emptyFilePath = "src/test/resources/com/agence/Trip/Hotel/empty_test.csv";
    final String nonExistentFilePath = "src/test/resources/com/agence/Trip/Hotel/non_existent_file.csv";

    @Test
    void testHotelRepositoryInitializationSize() {
        HotelRepository repository = new HotelRepository(testFilePath);
        
        assertEquals(3, repository.getAllHotels().size());
    }

    @Test
    void testHotelRepositoryInitializationContent() {
        HotelRepository repository = new HotelRepository(testFilePath);

        Hotel firstHotel = repository.getAllHotels().get(0);
        assertEquals("Bordeaux", firstHotel.getCity());
        assertEquals("10 Rue Sainte-Catherine", firstHotel.getAddress());
        assertEquals(4, firstHotel.getRating());
        assertEquals(85.50, firstHotel.getPricePerNight());
    }

    @Test
    void testHotelRepositoryBadFormatFileSize() {
        HotelRepository repository = new HotelRepository(badFormatFilePath);
        assertEquals(1, repository.getAllHotels().size());
    }

    @Test
    void testHotelRepositoryEmptyFileSize() {
        HotelRepository repository = new HotelRepository(emptyFilePath);
        assertEquals(0, repository.getAllHotels().size());
    }

    @Test
    void testHotelRepositoryNonExistentFileSize() {
        HotelRepository repository = new HotelRepository(nonExistentFilePath);
        assertEquals(0, repository.getAllHotels().size());
    }

    @Test
    void testErrorLoggingOutput() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            new HotelRepository(nonExistentFilePath);

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
}