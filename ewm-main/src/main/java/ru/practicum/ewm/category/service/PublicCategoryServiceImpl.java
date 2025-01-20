package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDtoResp;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDtoResp> getAllCategories(int from, int size) {
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from, size)).toList();

        List<CategoryDtoResp> categoriesDto = categories.stream()
                .map(CategoryMapper::mapToCategoryDtoResp)
                .collect(Collectors.toList());
        log.info("получен список categoryDtoResp from = " + from + " size " + size);
        return categoriesDto;
    }

    @Override
    public CategoryDtoResp getCategoryById(long id) {
        CategoryDtoResp categoryDtoResp = CategoryMapper
                .mapToCategoryDtoResp(categoryRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Категория " + id + "не найдена")));
        log.info("получен categoryDtoResp с ID = {}", categoryDtoResp.getId());
        return categoryDtoResp;
    }
}