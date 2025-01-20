package ru.practicum.ewm.category.mapper;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryDtoResp;
import ru.practicum.ewm.category.model.Category;

public class CategoryMapper {
    public static Category mapToCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDtoResp mapToCategoryDtoResp(Category category) {
        CategoryDtoResp categoryDtoResp = new CategoryDtoResp();
        categoryDtoResp.setId(category.getId());
        categoryDtoResp.setName(category.getName());
        return categoryDtoResp;
    }
}
