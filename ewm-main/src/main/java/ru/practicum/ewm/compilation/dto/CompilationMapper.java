package ru.practicum.ewm.compilation.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(EventMapper.toEventShortDto(compilation.getEvents()));
        return compilationDto;
    }

    public static List<CompilationDto> toCompilationDto(Iterable<Compilation> compilations) {
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            result.add(toCompilationDto(compilation));
        }
        return result;
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setEvents(new HashSet<Event>());
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }
}