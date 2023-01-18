package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {  // README includes some comments about this code implementation
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int getId() {
        id++;
        return id;
    }

    @Override
    public int createTask(Task task) {
        task.setId(getId());
        task.setTaskType(TaskType.SIMPLE_TASK);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createTask(Epic epic) {
        epic.setId(getId());
        epic.setTaskType(TaskType.EPIC);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createTask(Subtask subtask) {
        for (Integer id : epics.keySet()) {
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
        return 0;
    }




    @Override
    public boolean updateTask(Task task) {
        for (Task taskFromHash : tasks.values()) {
            if (task.hashCode() == taskFromHash.hashCode()) {
                task.setTaskType(TaskType.SIMPLE_TASK);
                tasks.put(task.getId(), task);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateTask(Subtask subtask) {
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
        return false;
    }

    @Override
    public boolean deleteTask(int id) {
        for (Integer idFromTasks : tasks.keySet()) {
            if (id == idFromTasks) {
                tasks.remove(id);
                Manager.getDefaultHistory().remove(id);
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
                Manager.getDefaultHistory().remove(id);
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
                        Manager.getDefaultHistory().remove(id);
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

    @Override
    public void clearTaskList() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        Manager.getDefaultHistory().clearHistory();
    }

    @Override
    public ArrayList<Task> retrieveCompleteList() {
       ArrayList<Task> listOfTasks = new ArrayList<>(tasks.values());

       for (Epic epic : epics.values()) {
           listOfTasks.add(epic);
           ArrayList<Integer> subtaskReferences = epic.getSubtaskReferences();
           for (Integer subtaskReference : subtaskReferences) {
               listOfTasks.add(subtasks.get(subtaskReference));
           }
       }
        return listOfTasks;
    }

    @Override
    public Task retrieveTaskById(int id) {
       ArrayList<Task> listOfTasks = retrieveCompleteList();

       Task taskReturned = new Task();
        for (Task task : listOfTasks) {
             if (task.getId() == id) {
                 taskReturned = task;
            }
        }
        if (taskReturned.getId() != 0)  {
            Manager.getDefaultHistory().add(taskReturned);
        }
        return taskReturned;
    }

    @Override
    public ArrayList<Subtask> retrieveSubtasks(int idEpic) {
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

    @Override
     public String printTasks() {
         StringBuilder string = new StringBuilder("\nПечать только простых задач\n");

         for (Task task : tasks.values()) {
             string.append(task).append("\n");
         }
         return string.toString();
    }

    @Override
    public String printEpics() {
        StringBuilder string = new StringBuilder("\nПечать только эпиков без подзадач\n");

        for (Epic epic : epics.values()) {
            string.append(epic).append("\n");
        }
        return string.toString();
    }

    @Override
    public String printSubtasks() {
        StringBuilder string = new StringBuilder("\nПечать только подзадач без эпиков\n");

        for (Subtask subtask : subtasks.values()) {
            string.append(subtask).append("\n");
        }
        return string.toString();
    }

    @Override
    public String printAll() {
        ArrayList<Task> listOfTasks = retrieveCompleteList();
        StringBuilder string = new StringBuilder("\nПечать всех задач\n");

        for (Task task : listOfTasks) {
            string.append(task).append("\n");
        }
        return string.toString();
    }

    @Override
    public String printSubtasksByEpic(int idEpic) {
        ArrayList<Subtask> listOfTasks = retrieveSubtasks(idEpic);
        StringBuilder string = new StringBuilder("\nПечать подзадач по эпику\n");

        for (Subtask subtask : listOfTasks) {
            string.append(subtask).append("\n");
        }
        return string.toString();
    }
}
