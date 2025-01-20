package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Validated @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Поступил запрос Post /admin/compilations на создание Compilation с телом {}", newCompilationDto);
        CompilationDto response = compilationService.createCompilation(newCompilationDto);
        log.info("Сформирован ответ Post /admin/compilations с телом: {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public CompilationDto updateCompilation(@RequestBody UpdateCompilationRequest updateCompilationRequest,
                                            @PathVariable Long id) {
        log.info("Поступил запрос Patch /admin/compilations/{} на обновление Compilation с телом {}", id, updateCompilationRequest);
        CompilationDto response = compilationService.updateCompilation(updateCompilationRequest, id);
        log.info("Сформирован ответ Patch /admin/compilations/{} с телом: {}", id, response);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long id) {
        log.info("Поступил запрос Delete /admin/compilations/{} на удаление Compilation с id = {}", id, id);
        compilationService.deleteCompilationById(id);
        log.info("Выполнен запрос Delete /admin/compilations/{} на удаление Compilation с id = {}", id, id);
    }

}