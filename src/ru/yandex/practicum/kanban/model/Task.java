package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.service.TaskStatus;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String details;
    private TaskStatus status;


    public Task(int id, String name, String details, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(details, task.details) && status == task.status;
    }

    @Override
    public String toString() {
        return "SIMPLE TASK:\n" + "ID: " + id + ";\n" + "NAME: " + name + ";\n"
                + "DESCRIPTION: " + details + ";\n"
                + "STATUS: " + status;
    }

    @Override
    public int hashCode() {   //Status does not count
        int hash = 17;
        if (name != null) {
           hash = hash + name.hashCode();
        }
        hash = hash * 31;
        if (details != null) {
            hash = hash + details.hashCode();
        }
        hash = hash * 31;
        hash +=id;
        return hash;


    }
}
