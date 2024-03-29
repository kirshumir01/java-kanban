package ru.yandex.practicum.services.server.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.models.Epic;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class EpicAdapter extends TypeAdapter<Epic> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {

        // если в менеджер добавлен новый эпик, которому не присвоены подзадачи,
        // обнулить (не оставлять пустыми) поля времени и списка id подзадач
        if (epic.getStartTime() == null || epic.getSubtasksId().isEmpty()) {
            jsonWriter.beginObject();
            jsonWriter.name("Class").value("Epic");
            jsonWriter.name("title").value(epic.getTitle());
            jsonWriter.name("description").value(epic.getDescription());
            jsonWriter.name("id").value(epic.getId());
            jsonWriter.name("status").value(epic.getStatus().toString());
            jsonWriter.name("subTasksId").value("null");
            jsonWriter.name("startTime").value("null");
            jsonWriter.name("duration").value("null");
            jsonWriter.name("endTime").value("null");
            jsonWriter.endObject();
        } else {
            jsonWriter.beginObject();
            jsonWriter.name("Class").value("Epic");
            jsonWriter.name("title").value(epic.getTitle());
            jsonWriter.name("description").value(epic.getDescription());
            jsonWriter.name("id").value(epic.getId());
            jsonWriter.name("status").value(epic.getStatus().toString());
            jsonWriter.name("subTasksId");
            jsonWriter.beginArray();
            epic.getSubtasksId().stream().forEach(subTaskId -> {
                try {
                    jsonWriter.value(subTaskId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            jsonWriter.endArray();
            jsonWriter.name("startTime").value(epic.getStartTime().format(formatter));
            jsonWriter.name("duration").value(epic.getDuration().toMinutes());
            jsonWriter.name("endTime").value(epic.getEndTime().format(formatter));
            jsonWriter.endObject();
        }
    }

    @Override
    public Epic read(JsonReader jsonReader) {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epic;

        epic = new Epic(
                jsonObject.get("title").getAsString(),
                jsonObject.get("description").getAsString());

        if (!jsonObject.get("id").getAsString().isEmpty()) {
            epic.setId(jsonObject.get("id").getAsInt());
        }

        return epic;
    }
}