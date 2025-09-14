package com.simon.expensetracker.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record ExpenseRequest(
        @NotNull(message = "Amount is required") BigDecimal amount,
        @NotNull(message = "Date is required") LocalDateTime date,
        String description,
        @NotNull(message = "Category ID is required") Long categoryId
) {}
