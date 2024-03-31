package ru.yandex.practicum.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskType type;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setType() {
        this.type = TaskType.TASK;
    }

    public TaskType getType() {
        return type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration.toMinutes());
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && Objects.equals(description, task.description) &&
                Objects.equals(id, task.id) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id + '\'' +
                ", type=" + type + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration +
                '}';
    }
}
