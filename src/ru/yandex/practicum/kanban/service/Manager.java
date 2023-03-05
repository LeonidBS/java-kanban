package ru.yandex.practicum.kanban.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.adapter.EpicAdapter;
import ru.yandex.practicum.kanban.service.adapter.SubtaskAdapter;
import ru.yandex.practicum.kanban.service.adapter.TaskAdapter;

public class Manager {
    private static final HttpTaskManager httpTaskManager = new HttpTaskManager();

    public static HttpTaskManager getDefault() {
        return httpTaskManager;
    }

    public static InMemoryTaskManager getInMemoryTask() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getFileBacked() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskAdapter());
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicAdapter());
        return gsonBuilder.create();
    }


}
