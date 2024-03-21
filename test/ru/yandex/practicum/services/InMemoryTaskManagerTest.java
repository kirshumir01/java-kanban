package ru.yandex.practicum.services;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.services.taskmanager.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}
