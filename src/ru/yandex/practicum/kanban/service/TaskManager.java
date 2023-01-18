package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import java.util.ArrayList;

public interface TaskManager {

    int createTask(Task task);
    int createTask(Epic epic);
    int createTask(Subtask subtask);
    boolean updateTask(Task task);
    boolean updateTask(Subtask subtask);
    boolean deleteTask(int id);
    void clearTaskList();
    ArrayList<Task> retrieveCompleteList();
    Task retrieveTaskById(int id);
    ArrayList<Subtask> retrieveSubtasks(int idEpic);
    String printTasks();
    String printEpics();
    String printSubtasks();
    String printAll();
    String printSubtasksByEpic(int idEpic);

}
