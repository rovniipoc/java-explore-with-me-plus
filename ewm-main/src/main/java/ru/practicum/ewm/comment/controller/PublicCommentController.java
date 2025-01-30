package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.service.PublicCommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Slf4j
@RequiredArgsConstructor
public class PublicCommentController {
    private final PublicCommentService publicCommentService;

    @GetMapping("{eventId}")
    public List<CommentShortDto> getAllCommentsById(@PathVariable long eventId) {
        log.info("Поступил запрос Get /comments/{eventId} на получение всех comments у event с id = {}", eventId);
        List<CommentShortDto> commentsDto = publicCommentService.getAllByEventId(eventId);
        log.info("Сформирован ответ Get /comments/{eventId} с телом: {}", commentsDto);
        return commentsDto;
    }
}
