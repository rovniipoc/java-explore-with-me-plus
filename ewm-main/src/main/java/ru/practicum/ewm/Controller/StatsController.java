package ru.practicum.ewm.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.EndpointHitInputDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEndpointHit(@Validated @RequestBody EndpointHitInputDto endpointHitInputDto) {
        log.info("Поступил запрос Post /hit на создание EndpointHit с телом: {}", endpointHitInputDto);
        statsClient.addHit(endpointHitInputDto);
        log.info("Обработан запрос Post /hit на создание EndpointHit с телом: {}", endpointHitInputDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        log.info("Поступил запрос Get /stats на получение List<ViewStatsOutputDto> с параметрами: start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, unique);
        System.out.println(response);
        log.info("Сформирован ответ Get /stats с телом: {}", response);
        return response;
    }
}
