package com.project.creditsimulator.service;

import com.project.creditsimulator.model.Credit;
import com.project.creditsimulator.util.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void testValidCredit() {
        Credit credit = new Credit("Mobil", "BARU", Year.now().getValue(), new BigDecimal("50000000"), 5, "35");
        assertDoesNotThrow(() -> creditService.inputValidation(credit));
    }

    @Test
    void testInvalidVehicleYearFormat() {
        Credit credit = new Credit("Mobil", "BARU", 123, new BigDecimal("50000000"), 5, "35");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> creditService.inputValidation(credit));
        assertEquals("Vehicle year must be a 4-digit number", exception.getMessage());
    }


    @Test
    void testInvalidVehicleYearForNew() {
        int invalidYear = Year.now().getValue() - 2;
        Credit credit = new Credit("Mobil", "BARU", invalidYear, new BigDecimal("50000000"), 5, "35");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> creditService.inputValidation(credit));
        assertEquals("For new vehicles, year cannot be less than " + (Year.now().getValue() - 1), exception.getMessage());
    }

    @Test
    void testInvalidLoanAmount() {
        Credit credit = new Credit("Mobil", "BARU", Year.now().getValue(), new BigDecimal("2000000000"), 5, "35");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> creditService.inputValidation(credit));
        assertEquals("Loan amount must not exceed 1 billion", exception.getMessage());
    }

    @Test
    void testInvalidTenure() {
        Credit credit = new Credit("Mobil", "BARU", Year.now().getValue(), new BigDecimal("50000000"), 7, "35");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> creditService.inputValidation(credit));
        assertEquals("Tenure must not exceed 6 years", exception.getMessage());
    }

    @Test
    void testNullDownPaymentPercent() {
        Credit credit = new Credit("Mobil", "BARU", 2024, new BigDecimal("50000000"), 5, "101");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> creditService.inputValidation(credit));
        assertEquals("Down payment must be a valid percentage between 0 and 100", exception.getMessage());
    }


    @Test
    void testDownPaymentValidation() {
        Credit credit = new Credit("Mobil", "BARU", Year.now().getValue(), new BigDecimal("10000000"), 5, "20");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> creditService.inputValidation(credit));
        assertEquals("Down payment must be at least 35% of loan amount.", exception.getMessage());
    }

    @Test
    void testCalculateInstallments() {
        Credit credit = new Credit("Mobil", "BARU", Year.now().getValue(), new BigDecimal("120000000"), 3, "35");
        BigDecimal[] installments = creditService.calculateInstallments(credit);
        assertNotNull(installments);
        assertEquals(3, installments.length);
        for (BigDecimal installment : installments) {
            assertTrue(installment.compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Test
    void testGetInterestRate() {
        BigDecimal rateForMobil = creditService.getInterestRate(1, "Mobil");
        assertEquals(new BigDecimal("0.08"), rateForMobil);

        BigDecimal rateForMotor = creditService.getInterestRate(1, "Motor");
        assertEquals(new BigDecimal("0.09"), rateForMotor);

        BigDecimal rateForYear2 = creditService.getInterestRate(2, "Mobil");
        assertEquals(new BigDecimal("0.081"), rateForYear2);

        BigDecimal rateForYear3 = creditService.getInterestRate(3, "Mobil");
        assertEquals(new BigDecimal("0.086"), rateForYear3);
    }

    @Test
    void testProcessFile() throws IOException {
        Path tempFile = Files.createTempFile("test_credit", ".txt");
        String content = "Mobil|BARU|2024|100000000|5|35\n";
        Files.write(tempFile, content.getBytes(), StandardOpenOption.WRITE);

        creditService.processFile(tempFile.toString());
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testProcessFile_FileNotFound() {
        creditService.processFile("non_existent_file.txt");
    }

    @Test
    void testLoadFromWebService() {
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            String jsonResponse = "[{\"vehicleType\":\"Mobil\",\"vehicleCondition\":\"BARU\",\"vehicleYear\":2023,\"loanAmount\":50000000,\"tenure\":5,\"downPaymentPercent\":\"35\"}]";
            mockedHttpClient.when(HttpClient::get).thenReturn(jsonResponse);
            creditService.loadFromWebService();
        }
    }

    @Test
    void testLoadFromWebService_InvalidResponse() {
        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            mockedHttpClient.when(HttpClient::get).thenReturn("Invalid JSON");
            creditService.loadFromWebService();
        }
    }

    @Test
    void testGetMonthlyInstallment() {
        BigDecimal totalLoan = new BigDecimal("50000000");
        BigDecimal tenureMonths = new BigDecimal("60");
        BigDecimal installment = creditService.getMonthlyInstallment(0, 0, totalLoan, tenureMonths);
        assertNotNull(installment);
        assertTrue(installment.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testDisplayInstallments() {
        Credit credit = new Credit("Mobil", "BARU", 2023, new BigDecimal("100000000"), 5, "35");
        assertDoesNotThrow(() -> creditService.displayInstallments(credit));
    }

}
