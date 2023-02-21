package ru.yandex.practicum.kanban.service;

public class Manager {
    private static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private static final FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    private static final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return inMemoryTaskManager;
    }

    public static TaskManager getFileBacked() {
        return fileBackedTasksManager;
    }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }


}
