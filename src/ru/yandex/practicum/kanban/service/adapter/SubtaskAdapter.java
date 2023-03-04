package ru.yandex.practicum.kanban.service.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

import java.io.IOException;
import java.time.LocalDateTime;

public class SubtaskAdapter extends TypeAdapter<Subtask> {

    @Override
    public void write(JsonWriter jsonWriter, Subtask subtask) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id");
        jsonWriter.value((subtask.getId()));
        jsonWriter.name("name");
        jsonWriter.value(subtask.getName());
        jsonWriter.name("details");
        jsonWriter.value(subtask.getDetails());
        jsonWriter.name("status");
        jsonWriter.value(subtask.getStatus().toString());
        jsonWriter.name("type");
        jsonWriter.value(subtask.getTaskType().toString());
        jsonWriter.name("epicReference");
        jsonWriter.value(subtask.getEpicReference());
        jsonWriter.name("startTime");
        jsonWriter.value(subtask.getStartTime().toString());

        if (subtask.getEndTime() != null) {
            jsonWriter.name("endTime");
            jsonWriter.value(subtask.getEndTime().toString());
        }

        jsonWriter.name("duration");
        jsonWriter.value(subtask.getDuration());
        jsonWriter.endObject();
    }

    @Override
    public Subtask read(JsonReader jsonReader) throws IOException {
        boolean isNExistTask = false;
        int id = 0;
        int epicReference = 0;
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
                case "epicReference":
                    epicReference = jsonReader.nextInt();
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
                && epicReference != 0 && startTime != null && duration != 0;
        if (isConstructorForNewTask1) {
            return new Subtask(name, details, epicReference, startTime, duration);
        }
        boolean isConstructorForNewTask2 = !isNExistTask && name != null && details != null
                && epicReference != 0 && startTime == null && duration != 0;
        if (isConstructorForNewTask2) {
            return new Subtask(name, details, epicReference, duration);
        }

        boolean isConstructorForExistTask1 = isNExistTask && name != null && details != null
                && startTime != null && duration != 0 && endTime != null && status != null
                && type == TaskType.SUBTASK && epicReference != 0;
        if (isConstructorForExistTask1) {
            return new Subtask(id, name, details, status, epicReference, startTime, endTime, duration);
        }
        boolean isConstructorForExistTask2 = isNExistTask && name != null && details != null
                && startTime != null && duration != 0 && endTime == null && status != null
                && (type == null || type == TaskType.SUBTASK && epicReference != 0);
        if (isConstructorForExistTask2) {
            return new Subtask(id, name, details, status, epicReference, startTime, duration);
        }
        return new Subtask(0);
    }

}
