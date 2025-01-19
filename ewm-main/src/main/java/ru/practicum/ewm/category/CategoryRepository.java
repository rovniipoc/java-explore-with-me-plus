package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.category.dto.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
