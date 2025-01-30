package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.NewComment;

public interface PrivateCommentService {

    CommentShortDto createComment(Long userId, Long eventId, NewComment newComment);
}
