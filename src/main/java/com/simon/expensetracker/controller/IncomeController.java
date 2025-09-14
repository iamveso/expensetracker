package com.simon.expensetracker.controller;

import com.simon.expensetracker.dto.request.IncomeRequest;
import com.simon.expensetracker.dto.response.IncomeResponse;
import com.simon.expensetracker.service.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeResponse> createIncome(@Valid @RequestBody IncomeRequest request) throws Exception {
        return ResponseEntity.ok(incomeService.createIncome(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponse> getIncomeById(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(incomeService.getIncomeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeRequest request)
            throws Exception {
        return ResponseEntity.ok(incomeService.updateIncome(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) throws Exception {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getAllIncomes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) throws Exception {
        return ResponseEntity.ok(
                incomeService.getIncomes(Optional.ofNullable(fromDate), Optional.ofNullable(toDate)));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getIncomeSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Map<String, BigDecimal> groupedByMonth = incomeService.getIncomeSummary(Optional.ofNullable(fromDate),
                Optional.ofNullable(toDate));

        BigDecimal total = groupedByMonth.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.put("totalIncome", total);
        response.put("incomeByMonth", groupedByMonth);

        return ResponseEntity.ok(response);
    }

}
