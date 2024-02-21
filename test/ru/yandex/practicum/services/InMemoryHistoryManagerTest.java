package ru.yandex.practicum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.history.HistoryManager;
import ru.yandex.practicum.services.history.InMemoryHistoryManager;
import ru.yandex.practicum.services.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void addTest() {
        Task task1 = new Task("Task", "Task description");
        Epic epic2 = new Epic("Epic", "Epic description");
        SubTask subTask3 = new SubTask("SubTask", "SubTask description", 2);

        taskManager.addTask(task1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask3);

        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getEpicById(2);
        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(3);

        // проверить размер списка истории после повторных просмотров задач
        assertEquals(3, taskManager.getHistory().size(), "Список истории сохраняет повторные просмотры задач.");
    }

    @Test
    public void removeTest() {
        Task task1 = new Task("Task", "Task description");
        Epic epic2 = new Epic("Epic", "Epic description");
        SubTask subTask3 = new SubTask("SubTask", "SubTask description", 2);

        taskManager.addTask(task1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask3);

        historyManager.remove(task1.getId());
        historyManager.remove(epic2.getId());
        historyManager.remove(subTask3.getId());

        // проверить, что список истории пуст
        assertEquals(0, taskManager.getHistory().size(), "Список истории не пуст.");
    }

    @Test
    public void getHistoryTest() {
        Task task1 = new Task("Task", "Task description");
        Epic epic2 = new Epic("Epic", "Epic description");
        SubTask subTask3 = new SubTask("SubTask", "SubTask description", 2);

        taskManager.addTask(task1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask3);

        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubTaskById(3);

        // проверить, что список истории не пуст
        assertFalse(taskManager.getHistory().isEmpty(), "Список истории пуст.");

        taskManager.removeAllTask();
        taskManager.removeAllEpics();
        taskManager.removeAllSubTasks();

        // проверить, что список истории пуст
        assertTrue(taskManager.getHistory().isEmpty(), "Список истории не пуст.");
    }
}
