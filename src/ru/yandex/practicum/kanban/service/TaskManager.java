package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {  // README includes some comments about this code implementation
    private int id = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int getId() {
        id++;
        return id;
    }

    public int createTask(Task task) {
        if (task != null) {
            if (task.getClass() == Task.class) {
                for (Task taskFromTasks : tasks.values()) {
                    if (task.equals(taskFromTasks)) {
                        return 0;
                    }
                }
                task.setId(getId());
                task.setTaskType(TaskType.SIMPLE_TASK);
                tasks.put(task.getId(), task);
                return task.getId();
            } else if (task.getClass() == Epic.class) {
                Epic epic = (Epic) task;
                for (Epic epicFromEpics : epics.values()) {
                    if (epic.equals(epicFromEpics)) {
                        return 0;
                    }
                }
                epic.setId(getId());
                epic.setTaskType(TaskType.EPIC);
                epics.put(epic.getId(), epic);
                return epic.getId();
            } else if (task.getClass() == Subtask.class) {
                Subtask subtask = (Subtask) task;
                for (Subtask subtaskFromTasks : subtasks.values()) {
                    if (subtask.equals(subtaskFromTasks)) {
                        return 0;
                    }
                }
                for (Integer id: epics.keySet()) {
                    if (subtask.getEpicReference() == id) {
                        subtask.setId(getId());
                        subtask.setTaskType(TaskType.SUBTASK);
                        subtasks.put(subtask.getId(), subtask);
                        Epic epic = epics.getOrDefault(id, null);
                        if (epic.getStatus() == TaskStatus.DONE) {
                            epic.setStatus(TaskStatus.IN_PROGRESS);
                        }
                        ArrayList<Integer> subtaskReferences = epic.getSubtaskReferences();
                        subtaskReferences.add(subtask.getId());
                        epic.setSubtaskReferences(subtaskReferences);
                        epics.put(epic.getId(), epic);
                        return subtask.getId();
                    }
                }
            }
        }
        return 0;
    }

    public boolean updateTask(Task task) {
        if (task != null) {
            if (task.getClass() == Task.class) {
                for (Task taskFromHash : tasks.values()) {
                    if (task.hashCode() == taskFromHash.hashCode()) {
                        task.setTaskType(TaskType.SIMPLE_TASK);
                        tasks.put(task.getId(), task);
                        return true;
                    }
                }
            } else if (task.getClass() == Subtask.class) {
                Subtask subtask = (Subtask) task;
                for (Subtask subtaskFromHash : subtasks.values()) {
                    if (subtask.hashCode() == subtaskFromHash.hashCode()) {
                        subtask.setTaskType(TaskType.SUBTASK);
                        subtasks.put(subtask.getId(), subtask);
                        Epic epic = epics.get(subtask.getEpicReference());
                        if (!(epic.epicStatusBySubtask(subtasks).equals(epic.getStatus()))) {
                            epic.setStatus(epic.epicStatusBySubtask(subtasks));
                            epic.setTaskType(TaskType.EPIC);
                            epics.put(epic.getId(), epic);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
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

    public ArrayList<Task> obtainCompleteList() {
       ArrayList<Task> listOfTasks = new ArrayList<>();

       for (Task task : tasks.values()) {
            listOfTasks.add(task);
       }
       for (Epic epic : epics.values()) {
           listOfTasks.add(epic);
           ArrayList<Integer> subtaskReferences = epic.getSubtaskReferences();
           for (Integer subtaskReference : subtaskReferences) {
               listOfTasks.add(subtasks.get(subtaskReference));
           }
       }
        return listOfTasks;
    }

    public Task obtainTaskById(int id) {
       ArrayList<Task> listOfTasks = obtainCompleteList();

       Task taskReturned = new Task();
        for (Task task : listOfTasks) {
             if (task.getId() == id) {
                 taskReturned = task;
            }
        }
        return taskReturned;
    }

    public ArrayList<Subtask> obtainSubtasks(int idEpic) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        Epic epic = epics.getOrDefault(idEpic, null);

        if (epic != null) {
            for (int id : subtasks.keySet()) {
                if (subtasks.get(id).getEpicReference() == idEpic) {
                    subtasksByEpic.add(subtasks.get(id));
                }
            }
        }
        return subtasksByEpic;
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

    public String printAll() {
        ArrayList<Task> listOfTasks = obtainCompleteList();
        StringBuilder string = new StringBuilder("\nПечать всех задач\n");

        for (Task task : listOfTasks) {
            string.append(task).append("\n");
        }
        return string.toString();
    }

    public String printSubtasksByEpic(int idEpic) {
        ArrayList<Subtask> listOfTasks = obtainSubtasks(idEpic);
        StringBuilder string = new StringBuilder("\nПечать подзадач по эпику\n");

        for (Subtask subtask : listOfTasks) {
            string.append(subtask).append("\n");
        }
        return string.toString();
    }
}
