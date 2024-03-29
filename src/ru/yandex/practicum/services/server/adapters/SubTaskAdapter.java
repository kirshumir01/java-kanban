package ru.yandex.practicum.services.server.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.models.SubTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTaskAdapter extends TypeAdapter<SubTask> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, SubTask subTask) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("Class").value("SubTask");
        jsonWriter.name("title").value(subTask.getTitle());
        jsonWriter.name("description").value(subTask.getDescription());
        jsonWriter.name("id").value(subTask.getId());
        jsonWriter.name("status").value(subTask.getStatus().toString());
        jsonWriter.name("epic_id").value(subTask.getEpicId());
        jsonWriter.name("startTime").value(subTask.getStartTime().format(formatter));
        jsonWriter.name("duration").value(subTask.getDuration().toMinutes());
        jsonWriter.name("endTime").value(subTask.getEndTime().format(formatter));
        jsonWriter.endObject();
    }

    @Override
    public SubTask read(JsonReader jsonReader) {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SubTask subTask;

        if (jsonObject.get("startTime") == null) {
            subTask = new SubTask(
                    jsonObject.get("title").getAsString(),
                    jsonObject.get("description").getAsString(),
                    jsonObject.get("epic_id").getAsInt());
        } else {
            String startTimeString = jsonObject.get("startTime").getAsString();
            LocalDateTime startTime = LocalDateTime.parse(startTimeString, formatter);

            String durationString = jsonObject.get("duration").getAsString();
            Duration durationInMinutes = Duration.ofMinutes(Integer.parseInt(durationString));

            subTask = new SubTask(
                    jsonObject.get("title").getAsString(),
                    jsonObject.get("description").getAsString(),
                    jsonObject.get("epic_id").getAsInt(),
                    startTime,
                    durationInMinutes);
        }

        if (!jsonObject.get("id").getAsString().isEmpty()) {
            subTask.setId(jsonObject.get("id").getAsInt());
        }

        return subTask;
    }
}
