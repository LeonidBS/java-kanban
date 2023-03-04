package ru.yandex.practicum.kanban.service.exceptions;

public class SubtaskCreationException extends RuntimeException {
    private final int epicID;

    public SubtaskCreationException(String message, int requestedID) {
        super(message);
        this.epicID = requestedID;
    }

    public String getDetailedMessage() {
        return getMessage() + epicID;
    }

}