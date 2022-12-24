package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);
    List<Task> getHistory();
    String printHistory();

}
