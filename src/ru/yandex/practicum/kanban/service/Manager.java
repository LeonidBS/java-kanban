package ru.yandex.practicum.kanban.service;

public class Manager {
    private static InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private static FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    private static InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
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
