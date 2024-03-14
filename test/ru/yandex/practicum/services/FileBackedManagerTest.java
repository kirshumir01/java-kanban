package ru.yandex.practicum.services;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.filemanager.FileBackedTaskManager;
import ru.yandex.practicum.services.history.HistoryManager;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedManagerTest extends InMemoryTaskManagerTest {

    private File file = new File("resources/test.csv");

    public TaskManager taskManager;
    public HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(file);
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void readHistoryFromEmptyFileTest() {
        Task task = new Task("Task", "Task description");
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask = new SubTask("Subtask", "Subtask description", 2);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        FileBackedTaskManager fromFileManager = FileBackedTaskManager.loadFromFile(new File("resources/test.csv"));

        Assertions.assertEquals(taskManager.getHistory(), fromFileManager.getHistory(),
                "Содержимое истории не соответствует.");
        Assertions.assertEquals(taskManager.getTaskById(1).toString(), fromFileManager.getTaskById(1).toString(),
                "Содержимое task не соответствует.");
        Assertions.assertEquals(taskManager.getEpicById(2).toString(), fromFileManager.getEpicById(2).toString(),
                "Содержимое epic не соответствует.");
        Assertions.assertEquals(taskManager.getSubTaskById(3).toString(), fromFileManager.getSubTaskById(3).toString(),
                "Содержимое epic не соответствует.");
    }

    @Test
    public void readHistoryFromFileTest(){
        Task task = new Task("Task", "Task description");
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask = new SubTask("Subtask", "Subtask description", 2);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubTaskById(3);

        FileBackedTaskManager fromFileManager = FileBackedTaskManager.loadFromFile(new File("resources/test.csv"));

        Assertions.assertEquals(taskManager.getHistory(), fromFileManager.getHistory(),
                "Содержимое истории не соответствует.");
        Assertions.assertEquals(taskManager.getTaskById(1).toString(), fromFileManager.getTaskById(1).toString(),
                "Содержимое task не соответствует.");
        Assertions.assertEquals(taskManager.getEpicById(2).toString(), fromFileManager.getEpicById(2).toString(),
                "Содержимое epic не соответствует.");
        Assertions.assertEquals(taskManager.getSubTaskById(3).toString(), fromFileManager.getSubTaskById(3).toString(),
                "Содержимое epic не соответствует.");
    }

    @Test
    public void saveToFileFest() {
        Task task = new Task("Task", "Task description");
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask = new SubTask("Subtask", "Subtask description", 2);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        FileBackedTaskManager fromFileManager = FileBackedTaskManager.loadFromFile(new File("resources/test.csv"));

        ArrayList<Task> tasks = new ArrayList<>();

        tasks.add(fromFileManager.getTaskById(task.getId()));
        tasks.add(fromFileManager.getEpicById(epic.getId()));
        tasks.add(fromFileManager.getSubTaskById(subTask.getId()));

        Assertions.assertFalse(tasks.isEmpty());
        Assertions.assertTrue(tasks.contains(task));
        Assertions.assertTrue(tasks.contains(epic));
        Assertions.assertTrue(tasks.contains(subTask));
        Assertions.assertEquals(taskManager.getListOfTasks(), fromFileManager.getListOfTasks(),
                "Task'и не соответствуют.");
        Assertions.assertEquals(taskManager.getListOfEpics(), fromFileManager.getListOfEpics(),
                "Epic'и не соответствуют.");
        Assertions.assertEquals(taskManager.getListOfSubTasks(), fromFileManager.getListOfSubTasks(),
                "SubTask'и не соответствуют.");
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