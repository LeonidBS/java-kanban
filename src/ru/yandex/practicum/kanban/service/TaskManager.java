package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public interface TaskManager {

    TreeMap<LocalDateTime, Task> getTimeSlotMap();

    int createTask(Task task);

    int createTask(Epic epic);

    int createTask(Subtask subtask);

    int updateTask(Task task);

    int updateTask(Epic epic);

    int updateTask(Subtask subtask);

    int deleteTask(int id);

    void clearTaskList();

    InMemoryHistoryManager getInMemoryHistoryManager();

    List<Task> retrieveCompleteList();

    List<Task> retrieveAllTasks();

    List<Task> retrieveAllSubtasks();

    List<Task> retrieveAllEpics();

    Task retrieveTaskById(int id);

    List<Subtask> retrieveSubtasks(int idEpic);

    String printAll();
}
