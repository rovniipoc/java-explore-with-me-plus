package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.RespCategoryDto;

import java.util.List;

public interface CategoryService {
    List<RespCategoryDto> getAllCategories(int from, int size);

    RespCategoryDto getCategoryById(long id);
}
