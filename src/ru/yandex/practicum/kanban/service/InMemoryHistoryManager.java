package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Node;
import ru.yandex.practicum.kanban.model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewHistoryList = new ArrayList<>();
    private final Map<Integer, Integer> tableNodeAddresses = new HashMap<>();
    private Node head = null;
    private Node tail = null;
    private int size = 1;
    private Node[] tableNodes = new Node[10];


    @Override
    public void add(Task task) {
        if  (tableNodeAddresses.getOrDefault(task.getId(), null) != null) {
            removeNode(tableNodes[tableNodeAddresses.get(task.getId())]);
            tableNodes[tableNodeAddresses.get(task.getId())] = linkLast(task);
        } else {
            tableNodeAddresses.put(task.getId(), size);
            if (size == tableNodes.length - 1) {
                resize();
            }
            tableNodes[size] = linkLast(task);
            size++;
        }
    }

    @Override
    public void remove(int id) {
        Node node = tableNodes[tableNodeAddresses.getOrDefault(id, 0)];
        if (node != null) {
            tableNodes[tableNodeAddresses.get(id)] = null;
            tableNodeAddresses.remove(id);
            removeNode(node);
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
        for (Node node : tableNodes) {
            node = null;
        }
        tableNodes = new Node[10];
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

    public void resize() {
         tableNodes = Arrays.copyOf(tableNodes, tableNodes.length * tableNodes.length);
    }
}
