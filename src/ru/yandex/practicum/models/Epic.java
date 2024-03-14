package ru.yandex.practicum.models;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    private TaskType type;

    public Epic(String title, String description) {
        super(title, description);
        this.type = TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subTaskIdList;
    }

    public void setSubTaskIdList(ArrayList<Integer> subTaskIdList) {
        this.subTaskIdList = subTaskIdList;
    }

    public TaskType getType() {
        return type;
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
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", status='" + status +
                ", subTasks' ID='" + subTaskIdList + '}' + '\'';
    }
}
