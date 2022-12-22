package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Task;
import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {

    public void add(Task task);
    public List<Task> getHistory();
    public String printHistory();

}
