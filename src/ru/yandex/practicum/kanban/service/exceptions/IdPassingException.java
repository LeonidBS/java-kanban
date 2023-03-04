package ru.yandex.practicum.kanban.service.exceptions;

public class IdPassingException extends RuntimeException {
    private final int requestedID;

    public IdPassingException(String message, int requestedID) {
        super(message);
        this.requestedID = requestedID;
    }

    public String getDetailedMessage() {
        return getMessage() + requestedID;
    }

}
