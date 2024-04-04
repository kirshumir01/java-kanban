package ru.yandex.practicum.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIdList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subTaskIdList;
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskIdList, epic.subTaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIdList);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id + '\'' +
                ", type=" + type + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status + '\'' +
                ", subTasks' ID='" + subTaskIdList + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration +
                '}';
    }
}
