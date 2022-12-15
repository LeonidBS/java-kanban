package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.service.TaskType;

public class ListTasksRow {
    private TaskType taskType;
    private Task task;

    public ListTasksRow(TaskType taskType, Task task) {
        this.taskType = taskType;
        this.task = task;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public String toString() {
        return this.getTask().toString();
    }
}
