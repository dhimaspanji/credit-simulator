package com.project.creditsimulator.model;

import java.math.BigDecimal;

public record Credit(
        String vehicleType,
        String vehicleCondition,
        int vehicleYear,
        BigDecimal loanAmount,
        int tenure,
        BigDecimal downPayment
) {
}
