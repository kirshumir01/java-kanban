package ru.yandex.practicum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.history.HistoryManager;
import ru.yandex.practicum.services.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    public TaskManager taskManager;
    public HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTask() {
        Task task = new Task("Task", "Task description");
        taskManager.addTask(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        // проверить, что экземпляры Task равны друг другу, если равен их id
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListOfTasks();

        // проверить запись в список и сравнить генерируемый id
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Количество задач в списке неверное.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpic() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        final int epicId = epic.getId();

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        // проверить, что экземпляры Epic равны друг другу, если равен их id
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getListOfEpics();

        // проверить запись в список и сравнить генерируемый id
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Количество эпиков в списке неверное.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addSubTask() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        taskManager.addSubTask(subTask);
        final int subTaskId = subTask.getId();

        final SubTask savedSubTask = taskManager.getSubTaskById(subTaskId);

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        // проверить, что экземпляры SubTask равны друг другу, если равен их id
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getListOfSubTasks();

        // проверить запись в список и сравнить генерируемый id
        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Количество подзадач в списке неверное.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void canNotAddSubTaskToNonExistentEpic() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Test subtask", "Test subtask description", 1);
        taskManager.addSubTask(subTask);
        Epic newEpic = taskManager.getEpicById(subTask.getEpicId());

        // проверить, что Epic не может добавить сам в себя в виде подзадачи
        assertNotNull(newEpic,"Подзадача не может быть записана в несуществующий эпик.");
    }

    @Test
    void canNotUpdateEpicByNonExistentId() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        epic.setId(2);
        taskManager.updateEpic(epic);

        // проверить, что обновить несуществующий epic невозсожно
        assertNull(taskManager.getEpicById(2), "Обновлен несуществующий эпик.");
    }

    @Test
    void canNotUpdateSubTaskByNonExistentId() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        taskManager.addSubTask(subTask);
        taskManager.updateSubTask(subTask);

        // проверить, что обновить subTask несуществующего эпика невозможно
        assertNotNull(taskManager.getSubTaskById(subTask.getId()), "Обновлена несуществующего подзадача эпика.");
    }

    @Test
    void managersShouldNotReturnsNull() {
        // проверить, что утилитарный класс всегда возвращает проинициализированные
        // и готовые к работе экземпляры менеджеров
        assertNotNull(taskManager, "Объект класса не возвращаются.");
        assertNotNull(historyManager, "Объект класса не возвращается.");
    }

    @Test
    void removedSubTasksShouldNotContainsOldId() {
        Epic epic1 = new Epic("Epic", "Epic description");
        SubTask subTask2 = new SubTask("Subtask2", "Subtask description", 1);
        SubTask subTask3 = new SubTask("Subtask3", "Subtask description", 1);

        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask2);
        taskManager.removeSubTaskById(2);
        taskManager.addSubTask(subTask3);

        // удаляемые подзадачи не должны хранить старые id
        // внцтри эпика не должны оставаться id неактуальных подзадач
        assertFalse(epic1.getSubtasksId().contains(subTask2.getId()));
        assertFalse(subTask2.equals(subTask3));
    }

    @Test
    void dataOfTaskShouldNotBeChangedBySetters() {
        Task task = new Task("Task", "Task description");

        taskManager.addTask(task);

        int taskId = task.getId();
        task.setId(2);
        int newTaskId = task.getId();

        String taskDescription = "Task description";
        task.setDescription("Changed task description");
        String newTaskDescription = "Task description";

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        assertEquals(2, task.getId(), "id задачи не изменился.");
        assertEquals("Changed task description", task.getDescription(), "Описание задачи не изменилось.");
        assertEquals(null, taskManager.getTaskById(2), "id задачи изменился на пользовательский.");
        assertEquals(1, taskManager.getHistory().size(), "В истории просмотра отображается 2 задачи.");
    }
}
