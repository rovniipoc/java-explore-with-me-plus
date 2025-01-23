package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationServiceImpl implements PublicCompilationService{
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long id) {
        CompilationDto compilationDto = CompilationMapper
            .toCompilationDto(compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с " + id + "не найдена")));
        log.info("получен CompilationDto с ID = {}", compilationDto.getId());
        return compilationDto;
    }
}
