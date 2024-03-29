package ru.yandex.practicum.services;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.models.TaskStatus;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @Test
    void addTaskTest() {
        Task task = new Task("Task", "Task description");
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getListOfTasks();

        // проверить запись в список и сравнить генерируемый id
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Количество задач в списке неверное.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addEpicTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getListOfEpics();

        // проверить запись в список и сравнить генерируемый id
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Количество эпиков в списке неверное.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addSubTaskTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        taskManager.addSubTask(subTask);

        final SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getListOfSubTasks();

        // проверить запись в список и сравнить генерируемый id
        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Количество подзадач в списке неверное.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getAllTasksTest() {
        Task task = new Task("Task", "Task description");
        taskManager.addTask(task);

        assertNotNull(taskManager.getListOfTasks(), "Список задач пуст.");
        assertEquals(task, taskManager.getListOfTasks().get(0), "Задачи не совпадают.");
    }

    @Test
    void getAllEpicsTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        assertNotNull(taskManager.getListOfEpics(), "Список эпиков пуст.");
        assertEquals(epic, taskManager.getListOfEpics().get(0), "Эпики не совпадают.");
    }

    @Test
    void getAllSubTasksTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        taskManager.addSubTask(subTask);

        assertNotNull(taskManager.getListOfSubTasks(), "Список подзадач пуст.");
        assertEquals(subTask, taskManager.getListOfSubTasks().get(0), "Подзадачи не совпадают.");
    }

    @Test
    public void getTaskByIdTest() {
        Task task = new Task("Task", "Task description");
        taskManager.addTask(task);

        Task taskByCustomId = taskManager.getTaskById(2);
        assertNull(taskByCustomId, "Задача не соответствует.");

        taskByCustomId = taskManager.getTaskById(task.getId());
        assertEquals(task, taskByCustomId, "Задача не соответствует.");
    }

    @Test
    public void getEpicByIdTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        Epic epicByCustomId = taskManager.getEpicById(2);
        assertNull(epicByCustomId, "Задача не соответствует.");

        epicByCustomId = taskManager.getEpicById(epic.getId());
        assertEquals(epic, epicByCustomId, "Задача не соответствует.");
    }

    @Test
    public void getSubTaskByIdAndGetSubTaskByEpicIdTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        taskManager.addSubTask(subTask);

        SubTask subTaskByCustomId = taskManager.getSubTaskById(3);
        assertNull(subTaskByCustomId, "Задача не соответствует.");

        subTaskByCustomId = taskManager.getSubTaskById(subTask.getId());
        assertEquals(subTask, subTaskByCustomId, "Задача не соответствует.");

        assertEquals(epic.getSubtasksId().getFirst(), subTaskByCustomId.getId(),
                "id подзадачи в эпике не соответствует.");
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("Task", "Task description");
        taskManager.addTask(task);

        Task newTask = new Task("Task", "Updated task");
        newTask.setId(1);
        taskManager.updateTask(newTask);

        final Task updatedTask = taskManager.getTaskById(task.getId());

        assertNotNull(updatedTask, "Задача не найдена.");
        assertNotEquals(task, updatedTask, "Задачи соответствуют.");
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        Epic newEpic = new Epic("Epic", "Updated epic");
        newEpic.setId(1);
        taskManager.updateEpic(newEpic);

        final Epic updatedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(updatedEpic, "Эпик не найден.");
        assertNotEquals(epic, updatedEpic, "Эпики соответствуют.");
    }

    @Test
    public void updateSubTaskTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("SubTask", "SubTask description", 1);
        taskManager.addSubTask(subTask);

        SubTask newSubTask = new SubTask("SubTask", "Updated subtask", 1);
        newSubTask.setId(2);
        taskManager.updateSubTask(newSubTask);

        final SubTask updatedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(updatedSubTask, "Подзадача не найдена.");
        assertNotEquals(subTask, updatedSubTask, "Подзадачи не соответствуют.");
    }

    @Test
    public void checkEpicStatusTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask1", "NEW", 1);
        SubTask subTask2 = new SubTask("SubTask2", "IN_PROGRESS", 1);
        SubTask subTask3 = new SubTask("SubTask3", "DONE", 1);

        subTask1.setStatus(TaskStatus.NEW);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        subTask3.setStatus(TaskStatus.DONE);

        taskManager.addSubTask(subTask1);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика не соответствует.");

        taskManager.addSubTask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика не соответствует.");

        taskManager.addSubTask(subTask3);
        assertNotEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика не соответствует.");

        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.checkEpicStatus(epic.getId());
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика не соответствует.");
    }

    @Test
    public void removeAllTaskssTest() {
        Task task1 = new Task("Task1", "Task description");
        Task task2 = new Task("Task2", "Task description");

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getListOfTasks().size(), "Размер списка не соответствует.");

        taskManager.removeAllTasks();

        assertEquals(0, taskManager.getListOfTasks().size(), "Список не очищен.");
    }

    @Test
    public void removeAllEpicsTest() {
        Epic epic1 = new Epic("Epic1", "Epic description");
        Epic epic2 = new Epic("Epic2", "Epic description");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        assertEquals(2, taskManager.getListOfEpics().size(), "Размер списка не соответствует.");

        taskManager.removeAllEpics();

        assertEquals(0, taskManager.getListOfEpics().size(), "Список не очищен.");
    }

    @Test
    public void removeAllSubTasksAndCheckEpicContentTest() {
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask1 = new SubTask("SubTask1", "SubTask description", 1);
        SubTask subTask2 = new SubTask("SubTask2", "SubTask description", 1);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(2, taskManager.getListOfSubTasks().size(), "Размер списка не соответствует.");

        taskManager.removeAllSubTasks();

        assertEquals(0, taskManager.getListOfSubTasks().size(), "Список подзадач не очищен.");
        assertEquals(0, epic.getSubtasksId().size(), "Список id подзадач не очищен.");
    }

    @Test
    public void removeTaskByIdTest() {
        Task task = new Task("Task", "Task description");
        taskManager.addTask(task);

        taskManager.removeTaskById(task.getId());
        assertEquals(0, taskManager.getListOfTasks().size(), "Задача с заданным id не удалена.");
    }

    @Test
    public void removeEpicByIdTest() {
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask1 = new SubTask("SubTask1", "SubTask description", 1);
        SubTask subTask2 = new SubTask("SubTask2", "SubTask description", 1);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.removeEpicById(epic.getId());
        assertEquals(0, taskManager.getListOfTasks().size(), "Эпик с заданным id не удалена.");
        assertEquals(0, taskManager.getListOfSubTasks().size(), "Идентификаторы подзадач эпика не удалены.");
    }

    @Test
    public void removeSubTasksByIdTest() {
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask = new SubTask("SubTask", "SubTask description", 1);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        taskManager.removeSubTaskById(subTask.getId());
        assertEquals(0, taskManager.getListOfSubTasks().size(), "Подзадача с заданным id не удалена.");
        assertEquals(0, epic.getSubtasksId().size(), "Идентификаторы подзадач эпика не удалены.");
    }

    @Test
    public void isCrossingTasksCheckTasks() {
        Task task = new Task("Task", "Task description", LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        Epic epic = new Epic("Epic", "Epic description");

        SubTask subTask1 = new SubTask("Subtask1", "Subtask1 description", 2, LocalDateTime.of(2024, 01, 02, 00, 00),
                Duration.ofMinutes(15));
        SubTask subTask2 = new SubTask("Subtask2", "Subtask1 description", 2, LocalDateTime.of(2024, 01, 02, 01, 00),
                Duration.ofMinutes(15));

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(task.getStartTime(), taskManager.getTaskById(task.getId()).getStartTime(),
                "Время начала задачи не соответствует.");
        assertEquals(subTask1.getStartTime(), taskManager.getSubTaskById(subTask1.getId()).getStartTime(),
                "Время начала подзадачи не соответствует.");
        assertEquals(subTask2.getStartTime(), taskManager.getSubTaskById(subTask2.getId()).getStartTime(),
                "Время начала подзадачи не соответствует.");

        assertEquals(task, taskManager.getPrioritizedTasks().first(),
                "Задача с самым ранним временем начала не первая в списке.");
        assertEquals(subTask1.getStartTime(), epic.getStartTime(),
                "Время начала эпика не соответствует времени начала самой ранней подзадачи.");
        assertEquals(subTask2.getEndTime(), epic.getEndTime(),
                "Время завершения эпика не соответствует времени начала самой поздней подзадачи.");

        taskManager.removeSubTaskById(subTask2.getId());

        assertEquals(subTask1.getEndTime(), epic.getEndTime(), "Время завершения эпика не обновлено.");
    }
}
