package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    public int createTask(Task task);
    public int createTask(Epic epic);
    public int createTask(Subtask subtask);
    public boolean updateTask(Task task);
    public boolean updateTask(Subtask subtask);
    public boolean deleteTask(int id);
    public void clearTaskList();
    public ArrayList<Task> obtainCompleteList();
    public Task obtainTaskById(int id);
    public ArrayList<Subtask> obtainSubtasks(int idEpic);
    public String printTasks();
    public String printEpics();
    public String printSubtasks();
    public String printAll();
    public String printSubtasksByEpic(int idEpic);

}
