package ru.yandex.practicum.kanban.exceptions;

import ru.yandex.practicum.kanban.model.Task;

import java.util.Objects;

public class Node {

    public Task task;
    public Node next;
    public Node prev;

    public Node(Task task, Node prev) {
        this.task = task;
        this.next = null;
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return Objects.equals(task, node.task) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
    }
}