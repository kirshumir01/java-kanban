package ru.yandex.practicum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
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

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubTaskById(subTask3.getId());

        taskManager.getHistoryManager().remove(task1.getId());
        taskManager.getHistoryManager().remove(epic2.getId());
        taskManager.getHistoryManager().remove(subTask3.getId());

        // проверить, что список истории пуст
        assertEquals(0, taskManager.getHistoryManager().getHistory().size(), "Список истории не пуст.");
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
        assertFalse(taskManager.getHistoryManager().getHistory().isEmpty(), "Список истории пуст.");

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubTasks();

        // проверить, что список истории пуст
        assertTrue(taskManager.getHistoryManager().getHistory().isEmpty(), "Список истории не пуст.");
    }

    @Test
    public void shouldReturnEmptyHistoryTest() {
        List<Task> history = new ArrayList<>();

        assertEquals(history, taskManager.getHistoryManager().getHistory(), "Возвращается непустая история.");
    }

    @Test
    public void duplicateTasksViewsTest() {
        Task task = new Task("Task", "Task description");

        taskManager.addTask(task);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);

        assertEquals(1, taskManager.getHistoryManager().getHistory().size(), "Дублирующиеся просмотры задач не исключаются из истории.");
    }

    @Test
    public void removeFromBeginningOfHistoryTest() {
        Task task1 = new Task("Task1", "Task description");
        Task task2 = new Task("Task2", "Task description");
        Task task3 = new Task("Task3", "Task description");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        taskManager.getHistoryManager().remove(task1.getId());

        assertEquals(new ArrayList<>(List.of(task2, task3)), taskManager.getHistoryManager().getHistory(),
                "Истории просмотров не соответствуют.");
    }

    @Test
    public void removeFromMiddleOfHistoryTest() {
        Task task1 = new Task("Task1", "Task description");
        Task task2 = new Task("Task2", "Task description");
        Task task3 = new Task("Task3", "Task description");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        taskManager.getHistoryManager().remove(task2.getId());

        assertEquals(new ArrayList<>(List.of(task1, task3)), taskManager.getHistoryManager().getHistory(), "Задача не удалена из истории просмотров.");
        assertFalse(taskManager.getHistoryManager().getHistory().contains(task2), "Задача не удалена из истории просмотров.");
    }

    @Test
    public void removeFromEndOfHistoryTest() {
        Task task1 = new Task("Task1", "Task description");
        Task task2 = new Task("Task2", "Task description");
        Task task3 = new Task("Task3", "Task description");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        taskManager.getHistoryManager().remove(task3.getId());

        assertEquals(new ArrayList<>(List.of(task1, task2)), taskManager.getHistoryManager().getHistory(),
                "Истории просмотров не соответствуют.");
    }
}
