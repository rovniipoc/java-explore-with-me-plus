package ru.practicum.ewm.request.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
