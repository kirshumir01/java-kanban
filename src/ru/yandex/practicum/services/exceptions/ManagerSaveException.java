package ru.yandex.practicum.services.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException (String message) {
        super(message);
    }
}
