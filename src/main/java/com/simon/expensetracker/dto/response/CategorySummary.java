package com.simon.expensetracker.dto.response;

import java.math.BigDecimal;

public record CategorySummary(String category, BigDecimal amount) {}
