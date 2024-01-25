package ru.yandex.practicum.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private final TaskManager taskManager = new InMemoryTaskManager();
    private final HistoryManager historyManager = new InMemoryHistoryManager();

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

    // проверить, что Epic не может добавить сам в себя в виде подзадачи
    @Test
    void canNotAddSubTaskToNonExistentEpic() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Test subtask", "Test subtask description", 1);
        taskManager.addSubTask(subTask);
        Epic newEpic = taskManager.getEpicById(subTask.getEpicId());

        assertNotNull(newEpic,"Подзадача не может быть записана в несуществующий эпик.");
    }

    @Test
    void canNotUpdateEpicByNonExistentId() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        epic.setId(2);
        taskManager.updateEpic(epic);

        assertNull(taskManager.getEpicById(2), "Обновлен несуществующий эпик.");
    }

    @Test
    void canNotUpdateSubTaskByNonExistentId() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        taskManager.addSubTask(subTask);
        taskManager.updateSubTask(subTask);

        assertNotNull(taskManager.getSubTaskById(subTask.getId()), "Обновлена несуществующего подзадача эпика.");
    }

    // проверить, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    @Test
    void managersShouldNotReturnsNull() {
        assertNotNull(taskManager, "Объект класса не возвращаются.");
        assertNotNull(historyManager, "Объект класса не возвращается.");
    }

    @Test
    void getHistory() {
        Task task = new Task("Task", "Task description");
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask = new SubTask("Subtask", "Subtask description", 2);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());

        ArrayList<Task> history = taskManager.getHistory();

        assertEquals(3, history.size(), "Список истории пуст или некорректно записан.");
        assertEquals(1, history.get(0).getId(), "Task не добавлен в историю.");
        assertEquals(2, history.get(1).getId(), "Epic не добавлен в историю.");
        assertEquals(3, history.get(2).getId(), "SubTask не добавлен в историю.");
    }
}
