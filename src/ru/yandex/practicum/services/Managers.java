package ru.yandex.practicum.services;

import ru.yandex.practicum.services.filemanager.FileBackedTaskManager;
import ru.yandex.practicum.services.history.HistoryManager;
import ru.yandex.practicum.services.history.InMemoryHistoryManager;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.File;

public class Managers {
    public static final File file = new File("resources/history.csv");

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}