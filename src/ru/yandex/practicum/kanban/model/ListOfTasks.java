package ru.yandex.practicum.kanban.model;

import ru.yandex.practicum.kanban.service.TaskType;
import java.util.ArrayList;

public class ListOfTasks {
    private ArrayList<ListTasksRow> listOfTasks = new ArrayList<>();

    @Override
    public String toString() {  // implemented counting the arranging
        StringBuilder string = new StringBuilder("Список запрошенных задач:\n");
        for (ListTasksRow taskFromList : listOfTasks) {
            if (taskFromList.getTaskType() == TaskType.SIMPLE_TASK) {
                Task task = taskFromList.getTask();
                string.append(task).append(";\n");
            } else if (taskFromList.getTaskType() == TaskType.EPIC) {
                Epic epic = (Epic) taskFromList.getTask();
                string.append("\n").append(epic).append(";\n");
            } else if (taskFromList.getTaskType() == TaskType.SUBTASK) {
                Subtask subtask = (Subtask) taskFromList.getTask();
                string.append(subtask).append(";\n");
            }
        }
        return string.toString();
    }

    public void addListOfTasks(ListTasksRow listTasksRow) {
        this.listOfTasks.add(listTasksRow);
    }

    public ArrayList<ListTasksRow> getListOfTasks() {
        return listOfTasks;
    }
}

