package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.service.TaskStatus;
import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private ArrayList<Integer> subtaskReferences;

    public Epic(int id, String name, String details, TaskStatus status,
                ArrayList<Integer> subtaskReferences) {
        super(id, name, details, status);
        this.subtaskReferences = subtaskReferences;
    }

    public Epic(String name, String details, TaskStatus status,
                ArrayList<Integer> subtaskReferences) {
        super(name, details, status);
        this.subtaskReferences = subtaskReferences;
    }

    public ArrayList<Integer> getSubtaskReferences() {
        if (subtaskReferences != null) {
            return subtaskReferences;
        } else {
            ArrayList<Integer> subtaskReferences = new ArrayList<>();
            return subtaskReferences;
        }
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
    
    public TaskStatus epicStatusBySubtask (HashMap<Integer, Subtask> subtasks) {
        int NumberStatusNew = 0;
        int NumberStatusInProcess = 0;
        int NumberStatusDone = 0;
        for (Integer subtaskReference : subtaskReferences) {
            Subtask subtask = subtasks.get(subtaskReference);
            if (subtask.getStatus() == TaskStatus.NEW) {
                NumberStatusNew++;
            } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                NumberStatusInProcess++;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                NumberStatusDone++;
            }
        }
        if (NumberStatusNew == subtaskReferences.size() || subtaskReferences.size() == 0) {
            return TaskStatus.NEW;
        } else if (NumberStatusInProcess > 0) {
            return TaskStatus.IN_PROGRESS;
        } else {
            return TaskStatus.DONE;
        }
    }

    @Override
    public String toString() {
        return this.type + ", ID: " + this.getId() + ";\n" + "NAME: " + this.getName() + ";\n"
                + "DESCRIPTION: " + this.getDetails() + ";\n"
                + "STATUS: " + this.getStatus() + ";\n" + "SUBTASKS: " + subtaskReferences;
    }
}
