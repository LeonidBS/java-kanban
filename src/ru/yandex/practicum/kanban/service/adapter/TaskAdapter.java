package ru.yandex.practicum.kanban.service.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

import java.io.IOException;
import java.time.LocalDateTime;

public class TaskAdapter extends TypeAdapter<Task> {

    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id");
        jsonWriter.value((task.getId()));
        jsonWriter.name("name");
        jsonWriter.value(task.getName());
        jsonWriter.name("details");
        jsonWriter.value(task.getDetails());
        jsonWriter.name("status");
        jsonWriter.value(task.getStatus().toString());
        jsonWriter.name("type");
        jsonWriter.value(task.getTaskType().toString());
        jsonWriter.name("startTime");
        jsonWriter.value(task.getStartTime().toString());

        if (task.getEndTime() != null) {
            jsonWriter.name("endTime");
            jsonWriter.value(task.getEndTime().toString());
        }

        jsonWriter.name("duration");
        jsonWriter.value(task.getDuration());
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        boolean isNExistTask = false;
        int id = 0;
        String name = null;
        String details = null;
        TaskStatus status = null;
        TaskType type = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        int duration = 0;

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "id":
                    id = jsonReader.nextInt();
                    isNExistTask = true;
                    break;
                case "name":
                    name = jsonReader.nextString();
                    break;
                case "details":
                    details = jsonReader.nextString();
                    break;
                case "status":
                    status = TaskStatus.valueOf(jsonReader.nextString());
                    break;
                case "type":
                    type = TaskType.valueOf(jsonReader.nextString());
                    break;
                case "startTime":
                    startTime = LocalDateTime.parse(jsonReader.nextString());
                    break;
                case "endTime":
                    endTime = LocalDateTime.parse(jsonReader.nextString());
                    break;
                case "duration":
                    duration = jsonReader.nextInt();
                    break;
            }
        }

        jsonReader.endObject();

        boolean isConstructorForNewTask1 = !isNExistTask && name != null && details != null
                && startTime != null && duration != 0;
        if (isConstructorForNewTask1) {
            return new Task(name, details, startTime, duration);
        }
        boolean isConstructorForNewTask2 = !isNExistTask && name != null && details != null
                && startTime == null && duration != 0;
        if (isConstructorForNewTask2) {
            return new Task(name, details, duration);
        }

        boolean isConstructorForExistTask1 = isNExistTask && name != null && details != null
                && startTime != null && duration != 0 && endTime != null && status != null
                && type == TaskType.SIMPLE_TASK;
        if (isConstructorForExistTask1) {
            return new Task(id, name, details, status, type, startTime, endTime, duration);
        }
        boolean isConstructorForExistTask2 = isNExistTask && name != null && details != null
                && startTime != null && duration != 0 && endTime == null && status != null
                && (type == null || type == TaskType.SIMPLE_TASK);
        if (isConstructorForExistTask2) {
            return new Task(id, name, details, status, startTime, duration);
        }
        return new Task();
    }

}


