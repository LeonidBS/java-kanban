package ru.yandex.practicum.kanban.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private ArrayList<Integer> subtaskReferences;

    public Epic(int id, String name, String details, TaskStatus status,
                ArrayList<Integer> subtaskReferences) {
        super(id, name, details, status, TaskType.EPIC);
        this.subtaskReferences = subtaskReferences;
    }

    public Epic(String name, String details, TaskStatus status) {
        super(name, details, status, TaskType.EPIC);
        this.subtaskReferences = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskReferences() {
        return subtaskReferences;
    }

    public void setSubtaskReferences(ArrayList<Integer> subtaskReferences) {
        this.subtaskReferences = subtaskReferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
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

    public TaskStatus epicStatusBySubtask(HashMap<Integer, Subtask> subtasks) {
        int numberStatusNew = 0;
        int numberStatusInProcess = 0;

        for (Integer subtaskReference : subtaskReferences) {
            Subtask subtask = subtasks.get(subtaskReference);
            if (subtask.getStatus() == TaskStatus.NEW) {
                numberStatusNew++;
            } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                numberStatusInProcess++;
            }
        }
        if (numberStatusNew == subtaskReferences.size() || subtaskReferences.size() == 0) {
            return TaskStatus.NEW;
        } else if (numberStatusInProcess > 0) {
            return TaskStatus.IN_PROGRESS;
        } else {
            return TaskStatus.DONE;
        }
    }

    @Override
    public String toString() {
        return this.getTaskType() + ", ID: " + this.getId() + ";\n" + "NAME: " + this.getName() + ";\n"
                + "DESCRIPTION: " + this.getDetails() + ";\n"
                + "STATUS: " + this.getStatus() + ";\n" + "SUBTASKS: " + subtaskReferences;
    }

    public String toStringInFile() {
        return this.getId() + "," + getTaskType() + ",\"" + this.getName() + "\"," + this.getStatus() +
                ",\"" + this.getDetails() + "\",\"subtaskReferences:" + subtaskReferences + "\"";
    }
}
