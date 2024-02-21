package ru.yandex.practicum.services.history;

import ru.yandex.practicum.models.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> getHistory();
    void addHistory(Task task);
    // определить метод для удаления задачи из история просмотра
    void remove (int id);
}
