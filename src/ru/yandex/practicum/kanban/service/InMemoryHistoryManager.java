package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public List<Task> viewHistoryList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (viewHistoryList.size() < 10) {
            viewHistoryList.add(task);
        } else {
            List<Task> nineTasks = viewHistoryList.subList(1,10);
            for (int i = 0; i < 9; i++) {
                viewHistoryList.set(i, nineTasks.get(i));
            }
            viewHistoryList.set(9, task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return viewHistoryList;
    }

    @Override
    public String printHistory() {
        StringBuilder string = new StringBuilder("\nПечать истории просмотров\n");
        for (int i = 0; i < viewHistoryList.size(); i++) {
            string.append("№").append(i+1).append("\n");
           string.append(viewHistoryList.get(i)).append("\n");
        }
        string.append(viewHistoryList.size()).append("\n");
        return string.toString();
    }
}
