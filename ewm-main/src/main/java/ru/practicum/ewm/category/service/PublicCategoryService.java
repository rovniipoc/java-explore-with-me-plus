package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDtoResp;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDtoResp> getAllCategories(int from, int size);

    CategoryDtoResp getCategoryById(long id);
}
