package ru.practicum.ewm.category.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDtoResp;
import ru.practicum.ewm.category.service.PublicCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/categories")
public class PublicCategoryController {
    public final PublicCategoryService publicCategoryService;

    @GetMapping
    public List<CategoryDtoResp> getAllCategories(@RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        log.info("GET-запрос к эндпоинту: '/categories' на получение categories");
        return publicCategoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDtoResp getCategoriesById(@PathVariable long catId) {
        return publicCategoryService.getCategoryById(catId);
    }
}
