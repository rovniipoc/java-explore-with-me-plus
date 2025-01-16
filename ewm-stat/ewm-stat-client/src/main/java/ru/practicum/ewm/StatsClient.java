package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
            .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
            .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
            .build()
        );
    }

    public ResponseEntity<Object> addHit(EndpointHitInputDto hitDto) {
        log.info("Отправлен Post /hit запрос на сервер с данными " + hitDto);
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters;
        if (uris != null) {
            parameters = Map.of(
                "start", start.toString(),
                "end", end.toString(),
                "uris", String.join(",", uris),
                "unique", unique
            );
            log.info("Отправлен Get /stats запрос на сервер с данными " + parameters);
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        } else {
            parameters = Map.of(
                "start", start.toString(),
                "end", end.toString(),
                "unique", unique
            );
            log.info("Отправлен Get /stats запрос на сервер с данными " + parameters);
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        }
    }
}
