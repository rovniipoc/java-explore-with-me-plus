package ru.practicum.ewm.event.dto;

import ru.practicum.ewm.category.dto.Category;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.user.dto.User;
import ru.practicum.ewm.user.dto.UserMapper;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toEvent(NewEventDto dto, User initiator, Category category) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setPaid(Boolean.TRUE.equals(dto.getPaid()));
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
        event.setRequestModeration(Boolean.TRUE.equals(dto.getRequestModeration()));
        event.setTitle(dto.getTitle());
        event.setState(EventState.PENDING); // при создании — PENDING
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(initiator);
        event.setCategory(category);
        return event;
    }

    public static void updateEventFromUserRequest(Event event, UpdateEventUserRequest dto, Category category) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getCategory() != null) {
            event.setCategory(category);
        }
        //Пример
        if ("CANCEL_REVIEW".equals(dto.getStateAction())) {
            event.setState(EventState.CANCELED);
        } else if ("SEND_TO_REVIEW".equals(dto.getStateAction())) {
            event.setState(EventState.PENDING);
        }
    }

    public static EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setRequestModeration(event.isRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(confirmedRequests != null ? confirmedRequests : 0);
        dto.setViews(views != null ? views : 0);
        return dto;
    }

    public static EventShortDto toEventShortDto(Event event, Long confirmedRequests, Long views) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.isPaid());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(confirmedRequests != null ? confirmedRequests : 0);
        dto.setViews(views != null ? views : 0);
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        return dto;
    }
}
