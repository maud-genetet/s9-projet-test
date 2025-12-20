package com.agence.Trip.Activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Date;

import org.junit.jupiter.api.Test;

public class ActivityRepositoryTest {
 
    final String testFilePath = "src/test/resources/com/agence/Trip/Activity/activities_test.csv";
    final String badFormatFilePath = "src/test/resources/com/agence/Trip/Activity/bad_format_test.csv";
    final String emptyFilePath = "src/test/resources/com/agence/Trip/Activity/empty_test.csv";
    final String nonExistentFilePath = "src/test/resources/com/agence/Trip/Activity/non_existent_file.csv";

    @Test
    void testActivityRepositoryInitializationSize() {
        ActivityRepository repository = new ActivityRepository(testFilePath);
        
        assertEquals(10, repository.getAllActivities().size());
    }

    @Test
    void testActivityRepositoryInitializationContent() {
        ActivityRepository repository = new ActivityRepository(testFilePath);

        Activity firstActivity = repository.getAllActivities().get(0);
        assertEquals("Paris", firstActivity.getCity());
        assertEquals("Musée du Louvre", firstActivity.getAddress());
        assertEquals(Date.valueOf("2025-06-15"), firstActivity.getDate());
        assertEquals(ActivityCategory.CULTURE, firstActivity.getCategory());
        assertEquals(15.50, firstActivity.getPrice());
    }

    @Test
    void testActivityRepositoryBadFormatFileSize() {
        ActivityRepository repository = new ActivityRepository(badFormatFilePath);
        assertEquals(1, repository.getAllActivities().size());
    }

    @Test
    void testActivityRepositoryEmptyFileSize() {
        ActivityRepository repository = new ActivityRepository(emptyFilePath);
        assertEquals(0, repository.getAllActivities().size());
    }

    @Test
    void testActivityRepositoryNonExistentFileSize() {
        ActivityRepository repository = new ActivityRepository(nonExistentFilePath);
        assertEquals(0, repository.getAllActivities().size());
    }

    @Test
    void testErrorLoggingOutput() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            ActivityRepository repository = new ActivityRepository(nonExistentFilePath);

            String output = errContent.toString();
            
            assertTrue(output.contains("Error reading file"), "Le message d'erreur doit être logué");
            assertTrue(output.contains(nonExistentFilePath), "Le chemin du fichier doit apparaître dans les logs");
            
        } finally {
            System.setErr(originalErr);
        }
    }
}
