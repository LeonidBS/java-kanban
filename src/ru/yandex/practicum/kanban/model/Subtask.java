package ru.yandex.practicum.kanban.model;

public class Subtask extends Task {
    private int epicReference;

    public Subtask(int id, String name, String details, TaskStatus status, int epicReference) {
        super(id, name, details, status, TaskType.SUBTASK);
        this.epicReference = epicReference;
    }

    public Subtask(String name, String details, TaskStatus status, int epicReference) {
        super(name, details, status, TaskType.SUBTASK);
        this.epicReference = epicReference;
    }

    public int getEpicReference() {
        return epicReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicReference == subtask.epicReference;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31;
        hash += epicReference;
        return hash;
    }

    @Override
    public String toString() {
        return this.getTaskType() + ", ID: " + this.getId() + ";\n" + "NAME: " + this.getName() + ";\n"
                + "DESCRIPTION: " + this.getDetails() + ";\n"
                + "STATUS: " + this.getStatus() + ";\n" + "EPIC's ID: " + epicReference ;
    }

    public String toStringInFile() {
        return this.getId() + ","+ getTaskType() + ",\"" + this.getName() + "\","
                + this.getStatus() + ",\"" + this.getDetails() + "\"," + epicReference;
    }

}

