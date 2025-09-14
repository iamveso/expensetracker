package com.simon.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simon.expensetracker.entity.Category;
import java.util.Optional;


public interface CatergoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
}
