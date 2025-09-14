package com.simon.expensetracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IncomeResponse(
        Long id,
        BigDecimal amount,
        LocalDateTime date,
        String description
) {}
