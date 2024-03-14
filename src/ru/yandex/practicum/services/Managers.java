package ru.yandex.practicum.services;

import ru.yandex.practicum.services.history.HistoryManager;
import ru.yandex.practicum.services.history.InMemoryHistoryManager;
import ru.yandex.practicum.services.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.services.taskmanager.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
