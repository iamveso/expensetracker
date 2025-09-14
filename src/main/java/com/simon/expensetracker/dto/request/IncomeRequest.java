package com.simon.expensetracker.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IncomeRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDateTime date,
        String description
) {}
