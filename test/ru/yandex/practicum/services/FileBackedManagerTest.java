package ru.yandex.practicum.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.filemanager.FileBackedTaskManager;
import ru.yandex.practicum.services.history.HistoryManager;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileBackedManagerTest {

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
        Task task = new Task("Task", "Task description", LocalDateTime.of(2024, 01, 01, 00, 00),
                Duration.ofMinutes(15));
        Epic epic = new Epic("Epic", "Epic description");
        SubTask subTask = new SubTask("Subtask", "Subtask description", 2, LocalDateTime.of(2024, 01, 01, 01, 00),
                Duration.ofMinutes(15));

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        FileBackedTaskManager fromFileManager = FileBackedTaskManager.loadFromFile(new File("resources/test.csv"));

        Assertions.assertTrue(taskManager.getHistory().isEmpty());
        Assertions.assertTrue(fromFileManager.getHistory().isEmpty());
        Assertions.assertEquals(taskManager.getHistory(), fromFileManager.getHistory(),
                "Содержимое истории не соответствует.");
        Assertions.assertEquals(taskManager.getTaskById(1), fromFileManager.getTaskById(1),
                "Содержимое task не соответствует.");
        Assertions.assertEquals(taskManager.getEpicById(2), fromFileManager.getEpicById(2),
                "Содержимое epic не соответствует.");
        Assertions.assertEquals(taskManager.getSubTaskById(3), fromFileManager.getSubTaskById(3),
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
        Assertions.assertEquals(taskManager.getTaskById(1), fromFileManager.getTaskById(1),
                "Содержимое task не соответствует.");
        Assertions.assertEquals(taskManager.getEpicById(2), fromFileManager.getEpicById(2),
                "Содержимое epic не соответствует.");
        Assertions.assertEquals(taskManager.getSubTaskById(3), fromFileManager.getSubTaskById(3),
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

    /*
    @AfterEach
    public void clearTestCSVFile() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */
}