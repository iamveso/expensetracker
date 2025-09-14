package com.simon.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simon.expensetracker.entity.Expenses;

public interface ExpenseRepository extends JpaRepository<Expenses, Long>{
    
}
