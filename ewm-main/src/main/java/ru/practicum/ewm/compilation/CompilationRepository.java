package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.compilation.dto.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

}
