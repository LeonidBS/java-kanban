package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {  // README includes some comments about this code implementation
    protected int id = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int getId() {
        id++;
        return id;
    }

    public boolean createTask(Task task) {
        if (task != null) {
            if (task.getClass() == Task.class) {
                for (Task taskFromTasks : tasks.values()) {
                    if (task.equals(taskFromTasks)) {
                        return false;
                    }
                }
                tasks.put(id, task);
                return true;
            } else if (task.getClass() == Epic.class) {
                Epic epic = (Epic) task;
                for (Epic epicFromEpics : epics.values()) {
                    if (epic.equals(epicFromEpics)) {
                        return false;
                    }
                }
                epics.put(id, epic);
                return true;
            } else if (task.getClass() == Subtask.class) {
                Subtask subtask = (Subtask) task;
                for (Subtask subtaskFromTasks : subtasks.values()) {
                    if (subtask.equals(subtaskFromTasks)) {
                        return false;
                    }
                }
                for (Integer id: epics.keySet()) {
                    if (subtask.getEpicReference() == id) {
                        subtasks.put(subtask.getId(), subtask);
                        Epic epic = epics.getOrDefault(id, null);
                        if (epic.getStatus() == TaskStatus.DONE) {
                            epic.setStatus(TaskStatus.IN_PROGRESS);
                        }
                        ArrayList<Integer> subtaskReferences = epic.getSubtaskReferences();
                        subtaskReferences.add(subtask.getId());
                        epic.setSubtaskReferences(subtaskReferences);
                        epics.put(epic.getId(), epic);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean updateTask(Task task) {
        if (task != null) {
            if (task.getClass() == Task.class) {
                for (Task taskFromHash : tasks.values()) {
                    if (task.hashCode() == taskFromHash.hashCode()) {
                        tasks.put(task.getId(), task);
                        return true;
                    }
                }
            } else if (task.getClass() == Subtask.class) {
                Subtask subtask = (Subtask) task;
                for (Subtask subtaskFromHash : subtasks.values()) {
                    if (subtask.hashCode() == subtaskFromHash.hashCode()) {
                        subtasks.put(subtask.getId(), subtask);
                        Epic epic = epics.get(subtask.getEpicReference());
                        if (!(epic.epicStatusBySubtask(subtasks).equals(epic.getStatus()))) {
                            epic.setStatus(epic.epicStatusBySubtask(subtasks));
                            epics.put(epic.getId(), epic);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ListTasksRow obtainTaskById(int id) {
        ListOfTasks listOfTasks = obtainCompleteList();

        for (ListTasksRow listTasksRow : listOfTasks.getListOfTasks()) {
            if (listTasksRow.getTask().getId() == id) {
                return listTasksRow;
            }
        }
        return null;
    }

    public boolean deleteTask(int id) {
        for (Integer idFromTasks : tasks.keySet()) {
            if (id == idFromTasks) {
                tasks.remove(id);
                return true;
            }
        }
        for (Integer idFromEpics : epics.keySet()) {
            if (id == idFromEpics) {
                ArrayList<Integer> subtaskReferences = epics.get(id).getSubtaskReferences();
                for (Integer subtaskReference : subtaskReferences) {
                    subtasks.remove(subtaskReference);
                }
                epics.remove(idFromEpics);
                return true;
            }
        }
        for (Integer idFromSubtasks : subtasks.keySet()) {
            if (id == idFromSubtasks) {
                int epicReference = subtasks.get(id).getEpicReference();
                subtasks.remove(id);
                Epic epic = epics.get(epicReference);
                ArrayList<Integer> epicSubtaskReferences = epic.getSubtaskReferences();
                for (int i = 0; i < epicSubtaskReferences.size(); i++) {
                    if (epicSubtaskReferences.get(i) == id) {
                        epicSubtaskReferences.remove(i);
                    }
                }
                if (!(epic.epicStatusBySubtask(subtasks).equals(epic.getStatus()))) {
                    epic.setStatus(epic.epicStatusBySubtask(subtasks));
                    epics.put(epic.getId(), epic);
                }
                return true;
            }
        }
        return false;
    }

    public void clearTaskList() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public ListOfTasks obtainCompleteList() {
        ListOfTasks listOfTasks = new ListOfTasks();

        for (Task task : tasks.values()) {
            ListTasksRow listTasksRow = new ListTasksRow(TaskType.SIMPLE_TASK, task);
            listOfTasks.addListOfTasks(listTasksRow);
        }
        for (Epic epic : epics.values()) {
            ListTasksRow listTasksRow = new ListTasksRow(TaskType.EPIC, epic);
            listOfTasks.addListOfTasks(listTasksRow);
            ArrayList<Integer> subtaskReferences = epic.getSubtaskReferences();
            for (Integer subtaskReference : subtaskReferences) {
                Subtask subtask = subtasks.get(subtaskReference);
                ListTasksRow listSubtaskRow = new ListTasksRow(TaskType.SUBTASK, subtask);
                listOfTasks.addListOfTasks(listSubtaskRow);
            }
        }
        return listOfTasks;
    }

    public ListOfTasks obtainSubtasks(int idEpic) {
        ListOfTasks listOfTasks = new ListOfTasks();

        Epic epic = epics.getOrDefault(idEpic, null);
        if (epic != null) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicReference() == idEpic) {
                    ListTasksRow listTasksRow = new ListTasksRow(TaskType.SUBTASK, subtask);
                    listOfTasks.addListOfTasks(listTasksRow);
                }
            }
        }
        return listOfTasks;
    }

     public String printTasks() {
         StringBuilder string = new StringBuilder("\nПечать только простых задач\n");
         for (Task task : tasks.values()) {
             string.append(task).append("\n");
         }
         return string.toString();
    }

    public String printEpics() {
        StringBuilder string = new StringBuilder("\nПечать только эпиков без подзадач\n");
        for (Epic epic : epics.values()) {
            string.append(epic).append("\n");
        }
        return string.toString();
    }

    public String printSubtasks() {
        StringBuilder string = new StringBuilder("\nПечать только подзадач без эпиков\n");
        for (Subtask subtask : subtasks.values()) {
            string.append(subtask).append("\n");
        }
        return string.toString();
    }
}
