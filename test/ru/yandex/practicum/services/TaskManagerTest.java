package ru.yandex.practicum.services;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.models.TaskStatus;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

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
    public void getSubTaskByIdTest() {
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        taskManager.addSubTask(subTask);

        SubTask subTaskByCustomId = taskManager.getSubTaskById(2);
        assertNull(subTaskByCustomId, "Задача не соответствует.");

        subTaskByCustomId = taskManager.getSubTaskById(subTask.getId());
        assertEquals(subTask, subTaskByCustomId, "Задача не соответствует.");
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("Task", "Task description");
        taskManager.addTask(task);

        task = new Task("Task", "Updated task");
        taskManager.updateTask(task);

        final Task updatedTask = taskManager.getTaskById(task.getId());

        assertNotNull(updatedTask, "Задача не найдена.");
        assertEquals(task, updatedTask, "Задачи не соответствуют.");
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        epic = new Epic("Epic", "Updated epic");
        taskManager.updateEpic(epic);

        final Epic updatedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(updatedEpic, "Эпик не найден.");
        assertEquals(epic, updatedEpic, "Эпики не соответствуют.");
    }

    @Test
    public void updateSubTaskTest() {
        SubTask subTask = new SubTask("SubTask", "SubTask description", 1);
        taskManager.addSubTask(subTask);

        subTask = new SubTask("SubTask", "Updated subtask", 1);
        taskManager.updateSubTask(subTask);

        final SubTask updatedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(updatedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, updatedSubTask, "Подзадачи не соответствуют.");
    }

    @Test
    public void checkEpicStatusTest() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask1", "NEW", 1);
        SubTask subTask2 = new SubTask("SubTask2", "IN_PROGRESS", 1);
        SubTask subTask3 = new SubTask("SubTask3", "DONE", 1);

        taskManager.addSubTask(subTask1);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статусы не совпадают");

        taskManager.addSubTask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статусы не совпадают");

        taskManager.addSubTask(subTask3);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статусы не совпадают");

        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статусы не совпадают");
    }

    @Test
    public void removeAllTasksTest() {
        Task task1 = new Task("Task1", "Task description");
        Task task2 = new Task("Task2", "Task description");

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getListOfTasks().size(), "Размер списка не соответствует.");

        taskManager.removeAllTask();

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

        assertEquals(0, taskManager.getListOfSubTasks().size(), "Список не очищен.");
        assertEquals(0, taskManager.getListOfEpics().get(1).getSubtasksId().size(), "Список id подзадач не очищен.");
    }

    @Test
    public void removeTaskByIdTest() {
        Task task = new Task("Task", "Task description");
        taskManager.addTask(task);

        taskManager.removeTaskById(2);
        assertEquals(task, taskManager.getListOfTasks().get(0), "Удалена задача с несуществующим id.");

        taskManager.removeTaskById(task.getId());
        assertEquals(null, taskManager.getListOfTasks().get(0), "Задача с заданным id не удалена.");
    }

    @Test
    public void removeEpicByIdTest() {
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask1 = new SubTask("SubTask1", "SubTask description", 1);
        SubTask subTask2 = new SubTask("SubTask2", "SubTask description", 1);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.removeEpicById(2);
        assertEquals(epic, taskManager.getListOfEpics().get(0), "Удален эпик с несуществующим id.");

        taskManager.removeEpicById(epic.getId());
        assertEquals(null, taskManager.getListOfTasks().get(0), "Эпик с заданным id не удалена.");
        assertEquals(0, taskManager.getListOfSubTasks().size(), "Идентификаторы подзадач эпика не удалены.");
    }

    @Test
    public void removeSubTasksByIdTest() {
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask = new SubTask("SubTask", "SubTask description", 1);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        taskManager.removeSubTaskById(4);
        assertEquals(subTask, taskManager.getListOfSubTasks().get(0), "Удалена подзадача с несуществующим id.");

        taskManager.removeEpicById(epic.getId());
        assertEquals(null, taskManager.getListOfTasks().get(0), "Эпик с заданным id не удалена.");
        assertEquals(0, taskManager.getListOfSubTasks().size(), "Идентификаторы подзадач эпика не удалены.");
    }




    Set<Task> getPrioritizedTasks();

    boolean isCrossingTasks(Task task);

    ArrayList<SubTask> getListOfSubTasksByEpic(int id);


    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubTaskById(int id);

    List<Task> getHistory();




}
