package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Node;
import ru.yandex.practicum.kanban.model.Task;
import java.util.*;

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
    public List<Integer> historyFromString(String value) {
        List<Integer> historyList = new ArrayList<>();
        String[] historyString = value.split(",");
        for (int i = 0; i < historyString.length; i++) {
            historyList.add(Integer.parseInt(historyString[i]));
            Task task = Manager.getDefault().retrieveTaskById(historyList.get(i));
            add(task);
        }
                return historyList;
    }

    @Override
    public void add(Task task) {
        if  (tableNodeAddresses.getOrDefault(task.getId(), null) != null) {
            removeNode(tableNodeAddresses.get(task.getId()));
        }
            tableNodeAddresses.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        Node node = tableNodeAddresses.getOrDefault(id, null);
        if (node != null) {
            removeNode(node);
            tableNodeAddresses.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
       return  getTasks();
    }

    @Override
    public String printHistory() {
        StringBuilder string = new StringBuilder("\nПечать истории просмотров\n");
        for (int i = 0; i < getHistory().size(); i++) {
            string.append("№").append(i+1).append("\n");
           string.append(getHistory().get(i)).append("\n");
        }
        string.append(getHistory().size()).append("\n");
        return string.toString();
    }

    @Override
    public void clearHistory() {
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
        node = null;
    }
}
