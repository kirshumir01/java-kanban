package ru.yandex.practicum.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setType() {
        this.type = TaskType.SUBTASK;
    }

    public TaskType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id + '\'' +
                ", type=" + type + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status + '\'' +
                ", description='" + description + '\'' +
                ", epicId=" + epicId + '\'' +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration +
                '}';
    }
}
