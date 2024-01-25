package ru.yandex.practicum.managers;

import ru.yandex.practicum.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();
    int HISTORY_LIST_MAX_SIZE = 10;
    @Override
    public void addHistory(Task task) {
        if (history.size() >= HISTORY_LIST_MAX_SIZE) {
            history.removeFirst();
            history.add(task);
        } else {
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
