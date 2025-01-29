package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.NewComment;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentShortDto createComment(Long userId, Long eventId, NewComment newComment) {
        User user = checkUserExist(userId);
        Event event = checkEventExist(eventId);
        Comment comment = CommentMapper.fromNewCommentToComment(newComment, user, event);
        return CommentMapper.toCommentShortDto(commentRepository.save(comment));
    }

    private Event checkEventExist(Long id) {
        Optional<Event> maybeEvent = eventRepository.findById(id);
        if (maybeEvent.isPresent()) {
            return maybeEvent.get();
        } else {
            throw new NotFoundException("События с id = " + id + " не существует");
        }
    }

    private User checkUserExist(Long id) {
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isPresent()) {
            return maybeUser.get();
        } else {
            throw new NotFoundException("Пользователя с id = " + id + " не существует");
        }
    }
}
