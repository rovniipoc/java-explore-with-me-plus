package ru.practicum.ewm.request.dto;

public enum RequestStatus {
    PENDING,     // Ожидает подтверждения
    CONFIRMED,   // Подтверждена
    REJECTED,    // Отклонена
    CANCELED     // Отменена пользователем
}
