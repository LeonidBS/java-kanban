package ru.yandex.practicum.kanban.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Integer> subtaskReferences;

    public Epic(int id, String name, String details, TaskStatus status,
                List<Integer> subtaskReferences,
                LocalDateTime startTime, int duration) {
        super(id, name, details, status, TaskType.EPIC, startTime, duration);
        this.subtaskReferences = subtaskReferences;
    }

    public Epic(int id, String name, String details, TaskStatus status,
                List<Integer> subtaskReferences, LocalDateTime startTime,
                LocalDateTime endTime, int duration) {
        super(id, name, details, status, TaskType.EPIC, startTime, endTime, duration);
        this.subtaskReferences = subtaskReferences;
    }

    public Epic(int id, String name, String details, TaskStatus status,
                List<Integer> subtaskReferences) {
        super(id, name, details, status, TaskType.EPIC);
        this.subtaskReferences = subtaskReferences;
    }

    public Epic(String name, String details) {
        super(name, details, TaskType.EPIC);
        this.subtaskReferences = new ArrayList<>();
    }

    public Epic(String name, String details, LocalDateTime startTime, int duration) {
        super(name, details, TaskType.EPIC, startTime, duration);
        this.subtaskReferences = new ArrayList<>();
    }

    public Epic() {
    }

    public List<Integer> getSubtaskReferences() {
        return subtaskReferences;
    }

    public void setSubtaskReferences(List<Integer> subtaskReferences) {
        this.subtaskReferences = subtaskReferences;
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
        Epic epic = (Epic) o;
        return subtaskReferences.equals(epic.subtaskReferences);
    }

    @Override
    public boolean equalsWOStatus(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equalsWOStatus(o)) {
            return false;
        }
        Epic epic = (Epic) o;
        return subtaskReferences.equals(epic.subtaskReferences);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31;
        hash += subtaskReferences.hashCode();
        return hash;
    }

    public TaskStatus statusBySubtask(HashMap<Integer, Subtask> subtasks) {
        Map<TaskStatus, Long> statusMap = subtasks.values().stream()
                .filter(subtask -> subtaskReferences.contains(subtask.getId()))
                .collect(Collectors.groupingBy(Subtask::getStatus, Collectors.counting()));

        long numberStatusNew = statusMap.getOrDefault(TaskStatus.NEW, 0L);
        long numberStatusInProcess = statusMap.getOrDefault(TaskStatus.IN_PROGRESS, 0L);

        boolean isNEW = numberStatusNew == subtaskReferences.size() || subtaskReferences.size() == 0;
        boolean isIN_PROGRESS = numberStatusInProcess > 0 || (numberStatusNew < subtaskReferences.size()
                && numberStatusNew > 0);

        if (isNEW) {
            return TaskStatus.NEW;
        } else if (isIN_PROGRESS) {
            return TaskStatus.IN_PROGRESS;
        } else {
            return TaskStatus.DONE;
        }
    }

    public Optional<LocalDateTime> startTimeBySubtask(HashMap<Integer, Subtask> subtasks) {
        return subtasks.values().stream()
                .filter(subtask -> subtaskReferences.contains(subtask.getId()))
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo);
    }

    public Optional<LocalDateTime> endTimeBySubtask(HashMap<Integer, Subtask> subtasks) {
        return subtasks.values().stream()
                .filter(subtask -> subtaskReferences.contains(subtask.getId()))
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo);
    }

    public int durationBySubtask(HashMap<Integer, Subtask> subtasks) {
        return subtasks.values().stream()
                .filter(subtask -> subtaskReferences.contains(subtask.getId()))
                .mapToInt(Task::getDuration)
                .sum();
    }

    @Override
    public String toString() {
        if (!subtaskReferences.isEmpty() && getEndTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
            return this.getTaskType() + ", ID: " + this.getId()
                    + ";\n" + "NAME: " + this.getName() + ";\n"
                    + "DESCRIPTION: " + this.getDetails() + ";\n"
                    + "STATUS: " + this.getStatus() + ";\n"
                    + "SUBTASKS: " + subtaskReferences + ";\n"
                    + "Время старта: " + this.getStartTime().format(formatter) + ";\n"
                    + "Продолжительность: " + this.getDuration() + ";\n"
                    + "Время завершения: " + this.getEndTime().format(formatter) + ";\n";
        } else {
            return this.getTaskType() + ", ID: " + this.getId()
                    + ";\n" + "NAME: " + this.getName() + ";\n"
                    + "DESCRIPTION: " + this.getDetails() + ";\n"
                    + "STATUS: " + this.getStatus() + ";\n"
                    + "SUBTASKS: " + subtaskReferences + ";\n";
        }
    }

    public String toStringInFile() {
        if (subtaskReferences.isEmpty()) {
            return this.getId() + "," + getTaskType() + ",\"" + this.getName()
                    + "\"," + this.getStatus() + ",\"" + this.getDetails()
                    + "\",\"subtaskReferences:" + subtaskReferences + "\"";
        } else {
            return this.getId() + "," + getTaskType() + ",\"" + this.getName()
                    + "\"," + this.getStatus() + ",\"" + this.getDetails()
                    + "\",\"subtaskReferences:" + subtaskReferences + "\","
                    + this.getStartTime() + "," + this.getEndTime() + "," + this.getDuration();
        }
    }
}
