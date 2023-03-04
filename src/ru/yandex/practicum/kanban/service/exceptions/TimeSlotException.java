package ru.yandex.practicum.kanban.service.exceptions;


import java.time.LocalDateTime;

public class TimeSlotException extends RuntimeException {
    private final LocalDateTime startTime;
    private final int duration;

    public TimeSlotException(String message, LocalDateTime startTime, int duration) {
        super(message);
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getDetailedMessage() {
        return getMessage() + "время старта:" + startTime + ", продолжительность:" + duration;
    }
}