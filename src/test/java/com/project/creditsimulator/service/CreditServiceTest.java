package com.project.creditsimulator.service;

import com.project.creditsimulator.model.Credit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class CreditServiceTest {
    private CreditService creditService;

    @BeforeEach
    void setUp() {
        creditService = new CreditService();
    }

    @Test
    void testCalculateInstallments() {
        Credit credit = new Credit("Mobil", "Baru", 2024, new BigDecimal("500000000"), 5, new BigDecimal("175000000"));
        BigDecimal[] installments = creditService.calculateInstallments(credit);
        assertEquals(5, installments.length);
        assertTrue(installments[0].compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testGetInterestRate() {
        assertEquals(new BigDecimal("0.08"), creditService.getInterestRate(1, "Mobil"));
        assertEquals(new BigDecimal("0.081"), creditService.getInterestRate(2, "Mobil"));
        assertEquals(new BigDecimal("0.086"), creditService.getInterestRate(3, "Mobil"));
    }

    @Test
    void testInputValidation_NewVehicle_Valid() {
        Credit credit = new Credit("Mobil", "Baru", 2024, new BigDecimal("500000000"), 5, new BigDecimal("175000000"));
        assertDoesNotThrow(() -> creditService.inputValidation(credit));
    }

    @Test
    void testInputValidation_UsedVehicle_Valid() {
        Credit credit = new Credit("Mobil", "Bekas", 2018, new BigDecimal("500000000"), 5, new BigDecimal("125000000"));
        assertDoesNotThrow(() -> creditService.inputValidation(credit));
    }

    @Test
    void testInputValidation_InvalidYear() {
        int currentYear = Year.now().getValue()-1;
        Credit credit = new Credit("Mobil", "Baru", 2022, new BigDecimal("500000000"), 5, new BigDecimal("175000000"));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> creditService.inputValidation(credit));
        assertEquals("For new vehicles, year cannot be less than " + currentYear, exception.getMessage());
    }

    @Test
    void testProcessFile(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("test.txt");
        Files.write(tempFile, List.of("Mobil|Baru|2024|500000000|5|175000000"));

        try (MockedStatic<Logger> loggerMockedStatic = mockStatic(Logger.class)) {
            creditService.processFile(tempFile.toString());
            assertTrue(Files.exists(tempFile));
        }
    }

}
