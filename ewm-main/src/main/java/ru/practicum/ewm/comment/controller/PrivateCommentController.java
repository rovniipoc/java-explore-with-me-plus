package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.NewComment;
import ru.practicum.ewm.comment.service.PrivateCommentService;
import ru.practicum.ewm.validation.CreateGroup;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/")
@Validated
public class PrivateCommentController {

    private final PrivateCommentService privateCommentService;

    @PostMapping("{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody @Validated(CreateGroup.class) NewComment newComment) {
        log.info("Поступил запрос Post /users/{}/events/{}/comments на создание комментария с телом: {}", userId, eventId, newComment);
        CommentShortDto response = privateCommentService.createComment(userId, eventId, newComment);
        log.info("Сформирован ответ Post /users/{}/events/{}/comments с телом: {}", userId, eventId, response);
        return response;
    }

}
