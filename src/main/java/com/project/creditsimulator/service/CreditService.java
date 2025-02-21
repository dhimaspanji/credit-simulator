package com.project.creditsimulator.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.project.creditsimulator.model.Credit;
import com.project.creditsimulator.util.Constants;
import com.project.creditsimulator.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.List;

public class CreditService {
    private static final Logger logger = LoggerFactory.getLogger(CreditService.class);

    public void loadFromWebService() {
        try {
            Gson gson = new Gson();
            String jsonResponse = HttpClient.get();
            JsonElement jsonElement = JsonParser.parseString(jsonResponse);

            if (jsonElement.isJsonArray()) {
                List<Credit> credits = gson.fromJson(jsonElement, new TypeToken<List<Credit>>() {}.getType());

                logger.info("Received {} credit records from web service", credits.size());

                if (credits.size() > 1) {
                    logger.info("Credit records:");
                    for (Credit credit : credits) {
                        logger.info("Vehicle Type: {}, Condition: {}, Year: {}, Amount: {}, Tenure: {}, Down Payment Percent: {}",
                                credit.vehicleType(), credit.vehicleCondition(), credit.vehicleYear(),
                                credit.loanAmount(), credit.tenure(), credit.downPaymentPercent());

                        // Validation
                        inputValidation(credit);
                    }
                }

                credits.forEach(this::displayInstallments);
            } else if (jsonElement.isJsonObject()) {
                Credit credit = gson.fromJson(jsonElement, Credit.class);

                // Validation
                inputValidation(credit);

                displayInstallments(credit);
            } else {
                logger.error("Unexpected JSON format: {}", jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Error fetching data from web service: {}", e.getMessage());
        }
    }

    public void processFile(String filename) {
        Path filePath = Paths.get(filename);

        if (!Files.exists(filePath)) {
            logger.error("File not found: {}", filename);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                processLine(line, lineNumber);
            }
        } catch (IOException e) {
            logger.error("Error reading file {}: {}", filename, e.getMessage(), e);
        }
    }

    public void displayInstallments(Credit credit) {
        BigDecimal[] monthlyInstallments = calculateInstallments(credit);

        if (logger.isInfoEnabled()) {
            for (int i = 0; i < monthlyInstallments.length; i++) {
                String formattedInstallment = String.format("%,.2f", monthlyInstallments[i]);
                BigDecimal interestRate = getInterestRate(i + 1, String.valueOf(credit.vehicleType()))
                        .multiply(new BigDecimal("100"));
                DecimalFormat df = new DecimalFormat("#.#");
                String formattedPercentage = df.format(interestRate);

                logger.info("Year {}: Rp. {}/month, Interest Rate: {}%", i + 1, formattedInstallment, formattedPercentage);
            }
        }
    }

    public BigDecimal[] calculateInstallments(Credit credit) {
        BigDecimal downPayment = credit.loanAmount()
                .multiply(new BigDecimal(credit.downPaymentPercent()).divide(new BigDecimal("100"), 3, RoundingMode.HALF_UP))
                .setScale(0, RoundingMode.HALF_UP);
        logger.info("Down Payment : {}", downPayment);
        BigDecimal remainingLoan = credit.loanAmount().subtract(downPayment);
        BigDecimal[] installments = new BigDecimal[credit.tenure()];
        BigDecimal tenureMonths = BigDecimal.valueOf(12).multiply(BigDecimal.valueOf(credit.tenure()));

        int month = 0;
        BigDecimal interestRate;
        BigDecimal monthlyInterest;
        BigDecimal totalLoan = BigDecimal.ZERO;
        BigDecimal monthlyInstallment;
        BigDecimal yearlyInstallment = BigDecimal.ZERO;

        for (int year = 0; year < credit.tenure(); year++) {
            if (year > 0) {
                remainingLoan = totalLoan.subtract(yearlyInstallment);
            }

            interestRate = getInterestRate(year + 1, String.valueOf(credit.vehicleType()));

            monthlyInterest = remainingLoan.multiply(interestRate).setScale(2, RoundingMode.HALF_UP);
            totalLoan = remainingLoan.add(monthlyInterest);
            monthlyInstallment = getMonthlyInstallment(month, year, totalLoan, tenureMonths);
            yearlyInstallment = monthlyInstallment.multiply(BigDecimal.valueOf(12));
            month += 12;

            installments[year] = monthlyInstallment;
        }
        return installments;
    }

    public BigDecimal getMonthlyInstallment(int month, int year, BigDecimal totalLoan, BigDecimal tenureMonths) {
        BigDecimal monthlyInstallment;

        if (year == 0) {
            monthlyInstallment = totalLoan.divide(tenureMonths, 2, RoundingMode.HALF_UP);
        } else {
            BigDecimal tenureMonth = tenureMonths.subtract(BigDecimal.valueOf(month));
            monthlyInstallment = totalLoan.divide(tenureMonth, 2, RoundingMode.HALF_UP);
        }

        return monthlyInstallment;
    }

    public BigDecimal getInterestRate(int year, String vehicleType) {
        BigDecimal baseRate = Constants.MOBIL.equalsIgnoreCase(vehicleType) ?
                BigDecimal.valueOf(0.08) :
                BigDecimal.valueOf(0.09);

        if (year == 2) {
            return baseRate.add(BigDecimal.valueOf(0.001));
        } else if (year > 2) {
            return baseRate.add(BigDecimal.valueOf(0.001)).add(BigDecimal.valueOf((year - 2) * 0.005));
        } else {
            return baseRate;
        }
    }

    private void processLine(String line, int lineNumber) {
        logger.info("Line string : {}", line);
        String[] parts = line.split("\\|");

        if (parts.length != 6) {
            logger.warn("Invalid data format at line {}: {}", lineNumber, line);
            return;
        }

        try {
            Credit credit = new Credit(
                    parts[0].trim(),
                    parts[1].trim(),
                    Integer.parseInt(parts[2].trim()),
                    new BigDecimal(parts[3].trim()),
                    Integer.parseInt(parts[4].trim()),
                    parts[5].trim()
            );

            // Validation
            inputValidation(credit);

            logger.info("Processing Credit Data (Line {}): {}", lineNumber, credit);
            displayInstallments(credit);
        } catch (NumberFormatException e) {
            logger.error("Invalid number format at line {}: {}", lineNumber, e.getMessage());
        }
    }

    public void inputValidation(Credit credit) {
        int currentYear = Year.now().getValue()-1;

        if (!String.valueOf(credit.vehicleYear()).matches("\\d{4}")) {
            throw new IllegalArgumentException("Vehicle year must be a 4-digit number");
        }
        if (Constants.BARU.equalsIgnoreCase(credit.vehicleCondition()) && credit.vehicleYear() < currentYear) {
            throw new IllegalArgumentException("For new vehicles, year cannot be less than " + currentYear);
        }
        if (credit.loanAmount().compareTo(new BigDecimal("1000000000")) > 0) {
            throw new IllegalArgumentException("Loan amount must not exceed 1 billion");
        }
        if (credit.tenure() > 6 || credit.tenure() == 0) {
            throw new IllegalArgumentException("Tenure must not exceed 6 years");
        }
        if (credit.downPaymentPercent() == null || !credit.downPaymentPercent().matches("^(100|[1-9]?\\d)$")) {
            throw new IllegalArgumentException("Down payment must be a valid percentage between 0 and 100");
        }

        BigDecimal minDownPayment = Constants.BARU.equalsIgnoreCase(credit.vehicleCondition()) ?
                credit.loanAmount().multiply(new BigDecimal("0.35")) :
                credit.loanAmount().multiply(new BigDecimal("0.25"));
        BigDecimal downPayment = credit.loanAmount().multiply(new BigDecimal(credit.downPaymentPercent())
                .divide(new BigDecimal("100"), 3, RoundingMode.HALF_UP));
        if (downPayment.compareTo(minDownPayment) < 0) {
            throw new IllegalArgumentException("Down payment must be at least " + (Constants.BARU.equalsIgnoreCase(credit.vehicleCondition()) ? "35%" : "25%") + " of loan amount.");
        }
    }
}
