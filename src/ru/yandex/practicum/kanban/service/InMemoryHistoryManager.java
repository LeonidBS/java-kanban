package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Node;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.exceptions.IdPassingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewHistoryList = new ArrayList<>();
    private final Map<Integer, Node> tableNodeAddresses = new HashMap<>();
    private Node head = null;
    private Node tail = null;

    @Override
    public String historyToString() {
        List<Task> taskHistory = getTasks();
        StringBuilder stringHistoryForFile = new StringBuilder();
        for (int i = 0; i < taskHistory.size(); i++) {
            stringHistoryForFile.append(taskHistory.get(i).getId());
            if (i < taskHistory.size() - 1) {
                stringHistoryForFile.append(",");
            }
        }
        return stringHistoryForFile.toString();
    }

    @Override
    public void add(Task task) {
        if (tableNodeAddresses.get(task.getId()) != null) {
            removeNode(tableNodeAddresses.get(task.getId()));
        }
        tableNodeAddresses.put(task.getId(), linkLast(task));
    }

    @Override
    public int remove(int id) {
        Node node = tableNodeAddresses.get(id);
        if (node != null) {
            removeNode(node);
            tableNodeAddresses.remove(id);
            return id;
        }
        throw new IdPassingException("В истории не существует задачи с переданным ID: ", id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public String printHistory() {
        StringBuilder string = new StringBuilder("\nПечать истории просмотров\n");
        for (int i = 0; i < getHistory().size(); i++) {
            string.append("№").append(i + 1).append("\n");
            string.append(getHistory().get(i)).append("\n");
        }
        string.append(getHistory().size()).append("\n");
        return string.toString();
    }

    @Override
    public void clearHistory() {
        head = null;
        tail = null;
        tableNodeAddresses.clear();
    }

    public Node linkLast(Task task) {
        Node node = new Node(task, tail);
        if (head == null) {
            head = node;
        } else if (tail != null) {
            tail.next = node;
        }
        tail = node;
        return node;
    }

    public List<Task> getTasks() {
        viewHistoryList.clear();
        if (head != null) {
            Node node = head;
            if (head.equals(tail)) {
                viewHistoryList.add(node.task);
            } else {
                while (!node.equals(tail)) {
                    viewHistoryList.add(node.task);
                    node = node.next;
                }
                viewHistoryList.add(tail.task);
            }
        }
        return viewHistoryList;
    }

    public void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}
