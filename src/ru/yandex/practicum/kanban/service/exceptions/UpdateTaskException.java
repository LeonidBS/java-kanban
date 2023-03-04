package ru.yandex.practicum.kanban.service.exceptions;

import ru.yandex.practicum.kanban.model.Task;

public class UpdateTaskException extends RuntimeException {
    private final Task originalTask;
    private final Task updatedTask;

    public UpdateTaskException(String message, Task originalTask, Task updatedTask) {
        super(message);
        this.originalTask = originalTask;
        this.updatedTask = updatedTask;
    }

    public String getDetailedMessage() {
        return getMessage() + "\n" + originalTask.toString() + "\n" + updatedTask.toString();
    }

}