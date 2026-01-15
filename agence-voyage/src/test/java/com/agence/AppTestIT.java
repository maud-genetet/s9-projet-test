package com.agence;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTestIT {

    @Test
    public void shouldRunApplicationAndGenerateReport() {
        // Arrange
        String basePath = "src/test/resources/com/agence/";
        
        String activityCsv = basePath + "/Trip/it_activities_test.csv";
        String hotelCsv    = basePath + "/Trip/it_hotels_test.csv";
        String journeyCsv  = basePath + "/Trip/it_journey_test.csv";
        String requestJson = basePath + "it_request_test.json";
        
        String outputJson = "target/output_test_result.json";

        String[] args = {activityCsv, hotelCsv, journeyCsv, requestJson, outputJson};

        // Act
        App.main(args);

        // Assert
        File outputFile = new File(outputJson);
        assertTrue(outputFile.exists(), "Le fichier de résultat JSON doit être généré");
        assertTrue(outputFile.length() > 0, "Le fichier de résultat ne doit pas être vide");
    }
}