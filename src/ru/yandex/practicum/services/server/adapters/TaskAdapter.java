package ru.yandex.practicum.services.server.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.models.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskAdapter extends TypeAdapter<Task> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("Class").value("Task");
        jsonWriter.name("title").value(task.getTitle());
        jsonWriter.name("description").value(task.getDescription());
        jsonWriter.name("id").value(task.getId());
        jsonWriter.name("status").value(task.getStatus().toString());
        jsonWriter.name("startTime").value(task.getStartTime().format(formatter));
        jsonWriter.name("duration").value(task.getDuration().toMinutes());
        jsonWriter.name("endTime").value(task.getEndTime().format(formatter));
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task;

        if (jsonObject.get("startTime") == null) {
            task = new Task(
                    jsonObject.get("title").getAsString(),
                    jsonObject.get("description").getAsString());
        } else {
            String startTimeString = jsonObject.get("startTime").getAsString();
            LocalDateTime startTime = LocalDateTime.parse(startTimeString, formatter);

            String durationString = jsonObject.get("duration").getAsString();
            Duration durationInMinutes = Duration.ofMinutes(Integer.parseInt(durationString));

            task = new Task(
                    jsonObject.get("title").getAsString(),
                    jsonObject.get("description").getAsString(),
                    startTime,
                    durationInMinutes);
        }

        if (!jsonObject.get("id").getAsString().isEmpty()) {
            task.setId(jsonObject.get("id").getAsInt());
        }

        return task;
    }
}