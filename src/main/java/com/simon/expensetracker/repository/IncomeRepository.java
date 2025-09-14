package com.simon.expensetracker.repository;

import com.simon.expensetracker.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserId(UUID userId);
    List<Income> findByUserIdAndDateBetween(UUID id, LocalDateTime from, LocalDateTime to);
}
