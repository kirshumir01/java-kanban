package ru.yandex.practicum.services.exceptions;

import ru.yandex.practicum.models.TaskType;

public class ManagerTaskNotFoundException extends RuntimeException {
    public ManagerTaskNotFoundException(String message) {
        super(message);
    }
    public ManagerTaskNotFoundException(final TaskType type) {
        this("Задача типа " + type.toString() + " не найдена в менеджере");
    }
}
