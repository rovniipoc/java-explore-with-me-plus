package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.RespCategoryDto;
import ru.practicum.ewm.category.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    @Override
    public List<RespCategoryDto> getAllCategories(int from, int size) {
        return null;
    }

    @Override
    public RespCategoryDto getCategoryById(long id) {
        return null;
    }
}
