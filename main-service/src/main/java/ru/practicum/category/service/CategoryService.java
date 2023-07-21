package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    void delete(Long catId);

    CategoryDto update(Long catId, CategoryDto categoryDto);

    Category getById(Long catId);

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getAllCategories(int from, int size);
}
