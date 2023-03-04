package ru.yandex.practicum.kanban.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String details;
    private TaskStatus status;
    private TaskType type;
    private LocalDateTime startTime;
    private int duration;
    private LocalDateTime endTime;

    public Task(String name, String details, TaskType type, LocalDateTime startTime, int duration) {
        this.name = name;
        this.details = details;
        this.status = TaskStatus.NEW;
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String details, TaskType type, int duration) {
        this.name = name;
        this.details = details;
        this.status = TaskStatus.NEW;
        this.type = type;
        this.duration = duration;
    }

    public Task(String name, String details, TaskType type) {
        this.name = name;
        this.details = details;
        this.status = TaskStatus.NEW;
        this.type = type;
    }

    public Task(String name, String details, int duration) {
        this.name = name;
        this.details = details;
        this.status = TaskStatus.NEW;
        this.type = TaskType.SIMPLE_TASK;
        this.duration = duration;
    }

    public Task(String name, String details, LocalDateTime startTime, int duration) {
        this.name = name;
        this.details = details;
        this.status = TaskStatus.NEW;
        this.type = TaskType.SIMPLE_TASK;
        this.startTime = startTime;
        this.duration = duration;
      //  this.endTime = startTime.plusMinutes(duration);
    }

    public Task() {
    }

    public Task(int id, String name, String details, TaskStatus status,
                LocalDateTime startTime, int duration) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.status = status;
        this.type = TaskType.SIMPLE_TASK;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plusMinutes(duration);
    }

    public Task(int id, String name, String details, TaskStatus status, TaskType type) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.status = status;
        this.type = type;
    }


    public Task(int id, String name, String details, TaskStatus status,
                TaskType type, LocalDateTime startTime, int duration) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.status = status;
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String details, TaskStatus status, TaskType type,
                LocalDateTime startTime, LocalDateTime endTime, int duration) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.status = status;
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = endTime;
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

    public TaskType getTaskType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setTaskType(TaskType type) {
        this.type = type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(details, task.details) && type == task.type
                && status == task.status && Objects.equals(startTime, task.startTime)
                && duration == task.duration;
    }

    public boolean equalsWOStatus(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(details, task.details) && Objects.equals(type, task.type);
    }

    @Override
    public String toString() {
        if (startTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
            return type + ", ID: " + id + ";\n" + "NAME: " + name + ";\n"
                    + "DESCRIPTION: " + details + ";\n"
                    + "STATUS: " + status + ";\n"
                    + "Время старта: " + startTime.format(formatter) + ";\n"
                    + "Продолжительность: " + duration + ";\n"
                    + "Время завершения: " + endTime.format(formatter) + ";\n";
        } else {
            return type + ", ID: " + id + ";\n" + "NAME: " + name + ";\n"
                    + "DESCRIPTION: " + details + ";\n"
                    + "STATUS: " + status + ";\n";
        }
    }

    @Override
    public int hashCode() {   //Status does not count
        int hash = 17;
        if (type != null) {
            hash = hash + type.hashCode();
        }
        hash *= 31;
        hash += id;
        return hash;
    }

    public String toStringInFile() {
        return id + "," + type + ",\"" + name
                + "\"," + status + ",\"" + details + "\","
                + startTime + "," + endTime + "," + duration;
    }
}
