package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.http.KVTaskClient;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient kvTaskClient;

    public HttpTaskManager() {
        URL url;
        try {
            url = new URL("http://localhost:8078");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public Path getPath() {
        return super.getPath();
    }

    @Override
    public void setPath(Path path) {
        super.setPath(path);
    }

    @Override
    public void save() {
        String stringToSave = retrieveCompleteList().stream()
                .map(Task::toStringInFile)
                .collect(Collectors.joining("/n"));
        stringToSave += "/n" + Manager.getDefaultHistory().historyToString();
        kvTaskClient.put(kvTaskClient.getApiToken(), stringToSave);
    }

    @Override
    public void loadFromStorage() {
        String[] splitedLoadedString = kvTaskClient.load(kvTaskClient.getApiToken()).split("/n");

        Map<Boolean, List<String>> taskListFromFile = Arrays.stream(splitedLoadedString)
                .filter(line -> line.length() > 1)
                .filter(line -> !(line.contains("id,type,name,status,details,special")))
                .collect(Collectors.partitioningBy(line -> line.matches("[^a-zA-Z]+")));

        for (String taskLine : taskListFromFile.get(false)) {
            switch (fromString(taskLine).getTaskType()) {
                case SIMPLE_TASK:
                    putTaskToMapFormFile(fromString(taskLine));
                    break;
                case EPIC:
                    putTaskToMapFormFile((Epic) fromString(taskLine));
                    break;
                case SUBTASK:
                    putTaskToMapFormFile((Subtask) fromString(taskLine));
                    break;
            }
        }
        if (!taskListFromFile.get(true).isEmpty()) {
            String[] historyString = taskListFromFile.get(true).get(0).split(",");
            for (int i = 0; i < historyString.length; i++) {
                Task task = retrieveTaskById(Integer.parseInt(historyString[i]));
                Manager.getDefaultHistory().add(task);
            }
        }
    }

    @Override
    public int createTask(Task task) {
        return super.createTask(task);
    }

    @Override
    public int createTask(Epic epic) {
        return super.createTask(epic);
    }

    @Override
    public int createTask(Subtask subtask) {
        return super.createTask(subtask);
    }

    @Override
    public int updateTask(Task task) {
        return super.updateTask(task);
    }

    @Override
    public int updateTask(Epic epic) {
        return super.updateTask(epic);
    }

    @Override
    public int updateTask(Subtask subtask) {
        return super.updateTask(subtask);
    }

    @Override
    public int deleteTask(int id) {
        return super.deleteTask(id);
    }

    @Override
    public void clearTaskList() {
        super.clearTaskList();
    }

    @Override
    public List<Task> retrieveCompleteList() {
        return super.retrieveCompleteList();
    }

    @Override
    public List<Task> retrieveAllTasks() {
        return super.retrieveAllTasks();
    }

    @Override
    public List<Task> retrieveAllSubtasks() {
        return super.retrieveAllSubtasks();
    }

    @Override
    public List<Task> retrieveAllEpics() {
        return super.retrieveAllEpics();
    }

    @Override
    public Task retrieveTaskById(int id) {
        return super.retrieveTaskById(id);
    }

    @Override
    public List<Subtask> retrieveSubtasks(int idEpic) {
        return super.retrieveSubtasks(idEpic);
    }
}
