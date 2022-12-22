package ru.yandex.practicum.kanban.service;

public class Manager {
    private static InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private static InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();


    public static TaskManager getDefault() {
        return inMemoryTaskManager;
        }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }

}
