package com.simon.expensetracker.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.simon.expensetracker.dto.request.ExpenseRequest;
import com.simon.expensetracker.dto.response.CategoryResponse;
import com.simon.expensetracker.dto.response.ExpenseResponse;
import com.simon.expensetracker.dto.response.ExpenseSummary;
import com.simon.expensetracker.entity.Expenses;
import com.simon.expensetracker.entity.User;
import com.simon.expensetracker.exception.CustomException;
import com.simon.expensetracker.repository.CatergoryRepository;
import com.simon.expensetracker.repository.ExpenseRepository;
import com.simon.expensetracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CatergoryRepository catergoryRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    }

    private ExpenseResponse mapToResponse(Expenses e) {
        return new ExpenseResponse(
                e.getId(),
                e.getAmount(),
                e.getDate(),
                e.getDescription(),
                new CategoryResponse(e.getCategory().getId(), e.getCategory().getName()));
    }

    public ExpenseResponse createExpense(ExpenseRequest request) throws Exception {
        User user = getCurrentUser();
        var category = catergoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomException("Category doesnt exist", HttpStatus.BAD_REQUEST));

        var expense = Expenses.builder()
                .amount(request.amount())
                .date(request.date())
                .description(request.description())
                .user(user)
                .category(category)
                .build();
        System.out.println("builder pattern " + expense.getId());
        expenseRepository.save(expense);
        return mapToResponse(expense);
    }

    public List<ExpenseResponse> getExpenses(Optional<LocalDateTime> from, Optional<LocalDateTime> to,
            Optional<Long> categoryId) throws Exception {
        var user = getCurrentUser();
        List<Expenses> expenses = expenseRepository.findAll().stream()
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .filter(e -> from.map(f -> !e.getDate().isBefore(f)).orElse(true))
                .filter(e -> to.map(t -> !e.getDate().isAfter(t)).orElse(true))
                .filter(e -> categoryId.map(cid -> e.getCategory().getId().equals(cid)).orElse(true))
                .collect(Collectors.toList());

        return expenses.stream().map(this::mapToResponse).collect(Collectors.toList());

    }

    public ExpenseResponse getExpenseById(Long id) throws Exception {
        var user = getCurrentUser();
        Expenses expense = expenseRepository.findById(id).filter(e -> e.getUser().getId().equals(user.getId())).orElseThrow(() -> new CustomException("Expense not found", HttpStatus.NOT_FOUND));
        return mapToResponse(expense);
    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) throws Exception {
        var user = getCurrentUser();
        Expenses expense = expenseRepository.findById(id).filter(e -> e.getUser().getId().equals(user.getId())).orElseThrow(() -> new CustomException("Expense not found", HttpStatus.NOT_FOUND));

        if (request.amount() != null) expense.setAmount(request.amount());
        
        if (request.date() != null) expense.setDate(request.date());
        
        if (request.description() != null) expense.setDescription(request.description());

        if (request.categoryId() != null) {
            var category = catergoryRepository.findById(request.categoryId()).orElseThrow(() -> new CustomException("Category not found", HttpStatus.NOT_FOUND));
            expense.setCategory(category);
        }

        expenseRepository.save(expense);
        return mapToResponse(expense);
    }

    public void deleteExpense(Long id) throws Exception {
        var user = getCurrentUser();
        Expenses expense = expenseRepository.findById(id).filter(e -> e.getUser().getId().equals(user.getId())).orElseThrow(() -> new CustomException("expense not found", HttpStatus.NOT_FOUND));
        expenseRepository.delete(expense);
    }

    public ExpenseSummary getExpenseSummary(Optional<LocalDateTime> from, Optional<LocalDateTime> to) throws Exception {
        List<ExpenseResponse> expenses = getExpenses(from, to, Optional.empty());
        BigDecimal total = expenses.stream()
                .map(ExpenseResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> byCategory = new HashMap<>();
        for (ExpenseResponse e : expenses) {
            byCategory.merge(e.category().name(), e.amount(), BigDecimal::add);
        }

        List<com.simon.expensetracker.dto.response.CategorySummary> categorySummaries = byCategory.entrySet().stream()
                .map(entry -> new com.simon.expensetracker.dto.response.CategorySummary(entry.getKey(), entry.getValue()))
                .toList();

        return new ExpenseSummary(total, categorySummaries);
    }
}
