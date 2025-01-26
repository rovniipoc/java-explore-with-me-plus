package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final PublicCompilationService publicCompilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("GET-запрос к эндпоинту: '/compilations/{}' на получение compilation", compId);
        CompilationDto response = publicCompilationService.getCompilationById(compId);
        log.info("Сформирован ответ GET '/compilations/{}' с телом: {}", compId, response);
        return response;
    }

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("GET-запрос к эндпоинту: '/compilations' на получение compilations");
        List<CompilationDto> response = publicCompilationService.getAllCompilations(pinned, from, size);
        log.info("Сформирован ответ GET '/compilations' с телом: {}", response);
        return response;
    }

}
