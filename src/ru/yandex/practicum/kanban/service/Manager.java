package ru.yandex.practicum.kanban.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.adapter.EpicAdapter;
import ru.yandex.practicum.kanban.service.adapter.SubtaskAdapter;
import ru.yandex.practicum.kanban.service.adapter.TaskAdapter;

import java.net.MalformedURLException;
import java.time.LocalDateTime;

public class Manager {
    private static HttpTaskManager httpTaskManager = new HttpTaskManager();
    private static InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private static FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    private static InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return httpTaskManager;
    }

    public static TaskManager getInMemoryTask() {
        return inMemoryTaskManager;
    }

    public static TaskManager getFileBacked() {
        return fileBackedTasksManager;
    }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskAdapter());
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicAdapter());
        return gsonBuilder.create();
    }


}
