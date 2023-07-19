package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.service.CheckPagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        try {
            Category category = repository.save(CategoryMapper.toCategory(categoryDto));
            log.info("Создана новая категория id={}, name={} для событий.", category.getId(), category.getName());
            return CategoryMapper.toCategoryDto(category);
        } catch (RuntimeException e) {
            throw new AlreadyExistsException("Категория с названием name=" + categoryDto.getName() + "уже существует");
        }
    }

    @Override
    public void delete(Long catId) {
        getById(catId);
        repository.deleteById(catId);
        log.info("Категория id={} удалена", catId);
    }

    @Override
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        categoryDto.setId(catId);
        getById(catId);
        try {
            Category category = repository.save(CategoryMapper.toCategory(categoryDto));
            log.info("Обновлена категория id={}, новое название: name={}.", category.getId(), category.getName());
            return CategoryMapper.toCategoryDto(category);
        } catch (RuntimeException e) {
            throw new AlreadyExistsException("Категория с названием name=" + categoryDto.getName() + "уже существует");
        }
    }

    @Override
    public Category getById(Long catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория id=" + catId + "не найдена."));
        log.info("Получена категория id={}", catId);
        return category;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.toCategoryDto(getById(catId));
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        List<Category> categories =
                repository.findAll(CheckPagination.getPageByParamsWithSort(from, size, "id"))
                        .stream().collect(Collectors.toList());
        log.info("Получены список всех категорий для событий ({} шт.)", categories.size());
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}
