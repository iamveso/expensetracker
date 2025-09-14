package com.simon.expensetracker.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ExpenseSummary(
    BigDecimal totalExpenses,
    List<CategorySummary> expensesByCategory
) {}
