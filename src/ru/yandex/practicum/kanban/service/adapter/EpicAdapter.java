package ru.yandex.practicum.kanban.service.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicAdapter extends TypeAdapter<Epic> {

    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id");
        jsonWriter.value((epic.getId()));
        jsonWriter.name("name");
        jsonWriter.value(epic.getName());
        jsonWriter.name("details");
        jsonWriter.value(epic.getDetails());
        jsonWriter.name("status");
        jsonWriter.value(epic.getStatus().toString());
        jsonWriter.name("type");
        jsonWriter.value(epic.getTaskType().toString());

        jsonWriter.name("subtaskReferences");
        jsonWriter.beginArray();
        for (Integer subtaskReference : epic.getSubtaskReferences()) {
            jsonWriter.value(subtaskReference);
        }
        jsonWriter.endArray();

        if (epic.getStartTime() != null && epic.getDuration() != 0) {
            jsonWriter.name("startTime");
            jsonWriter.value(epic.getStartTime().toString());
            jsonWriter.name("endTime");
            jsonWriter.value(epic.getEndTime().toString());
            jsonWriter.name("duration");
            jsonWriter.value(epic.getDuration());
        }
        jsonWriter.endObject();
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        boolean isNExistTask = false;
        int id = 0;
        List<Integer> subtaskReferences = new ArrayList<>();
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
                case "subtaskReferences":
                    jsonReader.beginArray();
                    while (jsonReader.hasNext()) {
                        subtaskReferences.add(jsonReader.nextInt());
                    }
                    jsonReader.endArray();
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

        boolean isConstructorForNewTask1 = !isNExistTask && name != null && details != null;
        if (isConstructorForNewTask1) {
            return new Epic(name, details);
        }

        boolean isConstructorForExistTask1 = isNExistTask && name != null && details != null
                && startTime != null && duration != 0 && endTime != null && status != null
                && type == TaskType.EPIC;
        if (isConstructorForExistTask1) {
            return new Epic(id, name, details, status, subtaskReferences, startTime, endTime, duration);
        }

        boolean isConstructorForExistTask2 = isNExistTask && name != null && details != null
                 && status != null && (type == null || type == TaskType.EPIC);
        if (isConstructorForExistTask2) {
            return new Epic(id, name, details, status, subtaskReferences);
        }

        return new Epic();

    }

}
