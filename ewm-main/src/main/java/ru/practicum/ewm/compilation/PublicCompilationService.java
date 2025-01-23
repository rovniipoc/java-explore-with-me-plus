package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;

public interface PublicCompilationService {
    CompilationDto getCompilationById(long id);
}
