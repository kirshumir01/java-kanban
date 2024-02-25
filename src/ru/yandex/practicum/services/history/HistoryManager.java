package ru.yandex.practicum.services.history;

import ru.yandex.practicum.models.Task;

import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();
    void add(Task task);
    // определить метод для удаления задачи из история просмотра
    void remove (int id);
}
