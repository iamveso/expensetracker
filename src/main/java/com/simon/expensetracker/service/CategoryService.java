package com.simon.expensetracker.service;

import com.simon.expensetracker.dto.response.CategoryResponse;
import com.simon.expensetracker.entity.Category;
import com.simon.expensetracker.repository.CatergoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CatergoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        var category = categoryRepository.findAll();
        return category.stream().map(e -> new CategoryResponse(e.getId(), e.getName())).collect(Collectors.toList());
    }

    public CategoryResponse createCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category already exists: " + name);
        }
        Category category = Category.builder().name(name).build();
        categoryRepository.save(category);
        return new CategoryResponse(category.getId(), category.getName());
    }
}
