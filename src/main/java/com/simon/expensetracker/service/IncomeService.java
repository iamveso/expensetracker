package com.simon.expensetracker.service;

import com.simon.expensetracker.dto.request.IncomeRequest;
import com.simon.expensetracker.dto.response.IncomeResponse;
import com.simon.expensetracker.entity.Income;
import com.simon.expensetracker.entity.User;
import com.simon.expensetracker.exception.CustomException;
import com.simon.expensetracker.repository.IncomeRepository;
import com.simon.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    }

    public IncomeResponse createIncome(IncomeRequest request) throws Exception {
        User user = getCurrentUser();
        Income income = Income.builder()
                .amount(request.amount())
                .date(request.date())
                .description(request.description())
                .user(user)
                .build();
        Income saved = incomeRepository.save(income);
        return mapToResponse(saved);
    }

    public List<IncomeResponse> getIncomes() throws Exception {
        User user = getCurrentUser();
        return incomeRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public IncomeResponse getIncomeById(Long id) throws Exception {
        User user = getCurrentUser();
        Income income = incomeRepository.findById(id)
                .filter(i -> i.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new CustomException("Income not found", HttpStatus.NOT_FOUND));
        return mapToResponse(income);
    }

    public IncomeResponse updateIncome(Long id, IncomeRequest request) throws Exception {
        User user = getCurrentUser();
        Income income = incomeRepository.findById(id)
                .filter(i -> i.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new CustomException("Income not found", HttpStatus.NOT_FOUND));

        income.setAmount(request.amount());
        income.setDate(request.date());
        income.setDescription(request.description());

        Income updated = incomeRepository.save(income);
        return mapToResponse(updated);
    }

    public void deleteIncome(Long id) throws Exception {
        User user = getCurrentUser();
        Income income = incomeRepository.findById(id)
                .filter(i -> i.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new CustomException("Income not found", HttpStatus.NOT_FOUND));
        incomeRepository.delete(income);
    }

    public List<IncomeResponse> getIncomes(Optional<LocalDateTime> fromDate, Optional<LocalDateTime> toDate) throws Exception {
        User user = getCurrentUser();

        if (fromDate.isPresent() && toDate.isPresent()) {
            return incomeRepository.findByUserIdAndDateBetween(user.getId(), fromDate.get(), toDate.get())
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return incomeRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getIncomeSummary(Optional<LocalDateTime> fromDate, Optional<LocalDateTime> toDate) throws Exception {
        User user = getCurrentUser();
        List<Income> incomes;

        if (fromDate.isPresent() && toDate.isPresent()) {
            incomes = incomeRepository.findByUserIdAndDateBetween(user.getId(), fromDate.get(), toDate.get());
        } else {
            incomes = incomeRepository.findByUserId(user.getId());
        }

        // Group by year-month (e.g. "2025-09") and sum amounts
        return incomes.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getDate().getYear() + "-" + String.format("%02d", i.getDate().getMonthValue()),
                        Collectors.mapping(Income::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    private IncomeResponse mapToResponse(Income income) {
        return new IncomeResponse(
                income.getId(),
                income.getAmount(),
                income.getDate(),
                income.getDescription());
    }
}
