package ru.yandex.practicum.kanban.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private final int epicReference;

    public Subtask(int id, String name, String details, TaskStatus status,
                   int epicReference, LocalDateTime startTime, int duration) {
        super(id, name, details, status, TaskType.SUBTASK, startTime, duration);
        this.epicReference = epicReference;
    }

    public Subtask(int id, String name, String details, TaskStatus status,
                   int epicReference, LocalDateTime startTime,
                   LocalDateTime endTime, int duration) {
        super(id, name, details, status, TaskType.SUBTASK, startTime, endTime, duration);
        this.epicReference = epicReference;
    }

    public Subtask(String name, String details, int epicReference, int duration) {
        super(name, details, TaskType.SUBTASK, duration);
        this.epicReference = epicReference;
    }

    public Subtask(String name, String details, int epicReference, LocalDateTime startTime, int duration) {
        super(name, details, TaskType.SUBTASK, startTime, duration);
        this.epicReference = epicReference;
    }


    public int getEpicReference() {
        return epicReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }
        Subtask subtask = (Subtask) o;
        return epicReference == subtask.epicReference;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31;
        hash += (epicReference + 1) * 15;
        return hash;
    }

    @Override
    public String toString() {
        if (this.getEndTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
            return this.getTaskType() + ", ID: " + this.getId() + ";\n" + "NAME: " + this.getName() + ";\n"
                    + "DESCRIPTION: " + this.getDetails() + ";\n"
                    + "STATUS: " + this.getStatus() + ";\n"
                    + "EPIC's ID: " + epicReference + ";\n"
                    + "Время старта: " + this.getStartTime().format(formatter) + ";\n"
                    + "Продолжительность: " + this.getDuration() + ";\n"
                    + "Время завершения: " + this.getEndTime().format(formatter) + ";\n";
        } else {
            return this.getTaskType() + ", ID: " + this.getId() + ";\n" + "NAME: " + this.getName() + ";\n"
                    + "DESCRIPTION: " + this.getDetails() + ";\n"
                    + "STATUS: " + this.getStatus() + ";\n"
                    + "EPIC's ID: " + epicReference + ";\n";
        }
    }

    public String toStringInFile() {
        return this.getId() + "," + getTaskType() + ",\"" + this.getName() + "\","
                + this.getStatus() + ",\"" + this.getDetails() + "\"," + epicReference + ","
                + this.getStartTime() + "," + this.getEndTime() + "," + this.getDuration();
    }
}

