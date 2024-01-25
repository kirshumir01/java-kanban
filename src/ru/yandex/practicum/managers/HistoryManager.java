package ru.yandex.practicum.managers;

import ru.yandex.practicum.model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> getHistory();
    void addHistory(Task task);
}
