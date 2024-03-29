package ru.yandex.practicum.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.filemanager.FileBackedTaskManager;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager taskManager = new FileBackedTaskManager(new File("resources/test.csv"));
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    private final Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubTasks();
        taskServer.start();
    }

    @Test
    public void addTaskByPostRequest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        task.setId(1);

        String taskToJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListOfTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются.");
        assertEquals(1, tasksFromManager.size(), "Количество задач не соответствует.");
        assertEquals("Task", tasksFromManager.get(0).getTitle(), "Наименование задачи не соответствует.");
    }

    @Test
    public void addEpicByPostRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        epic.setId(1);

        String epicToJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getListOfEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются.");
        assertEquals(1, epicsFromManager.size(), "Количество эпиков не соответствует.");
        assertEquals("Epic", epicsFromManager.get(0).getTitle(), "Наименование эпика не соответствует.");
    }

    @Test
    public void addSubTaskByPostRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        subTask.setId(1);

        String subTaskToJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = taskManager.getListOfSubTasks();

        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются.");
        assertEquals(1, subTasksFromManager.size(), "Количество подзадач не соответствует.");
        assertEquals("Subtask", subTasksFromManager.get(0).getTitle(), "Наименование подзадачи не соответствует.");
    }

    @Test
    public void getAllTasksByGetRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Task description",
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Task description",
                LocalDateTime.of(2024, 01, 01, 01, 00), Duration.ofMinutes(15));
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        ArrayList<Task> tasksFromManager = gson.fromJson(response.body(),
                new TasksArrayListTypeToken().getType());

        assertEquals(task1.getId(), tasksFromManager.get(0).getId(), "id задачи не соответствует.");
        assertEquals(task2.getId(), tasksFromManager.get(1).getId(), "id задачи не соответствует.");
        assertEquals("Task1", tasksFromManager.get(0).getTitle(), "Наименование задачи не соответствует.");
        assertEquals("Task2", tasksFromManager.get(1).getTitle(), "Наименование задачи не соответствует.");
    }

    @Test
    public void getAllEpicsByGetRequest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Epic2", "Epic description");
        taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        ArrayList<Epic> epicsFromManager = gson.fromJson(response.body(),
                new EpicsArrayListTypeToken().getType());

        assertEquals(epic1.getId(), epicsFromManager.get(0).getId(), "id эпика не соответствует.");
        assertEquals(epic2.getId(), epicsFromManager.get(1).getId(), "id эпика не соответствует.");
        assertEquals("Epic1", epicsFromManager.get(0).getTitle(), "Наименование эпика не соответствует.");
        assertEquals("Epic2", epicsFromManager.get(1).getTitle(), "Наименование эпика не соответствует.");

    }

    @Test
    public void getAllSubtasksByGetRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask1", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask2", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 01, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        ArrayList<SubTask> subTasksFromManager = gson.fromJson(response.body(),
                new SubTasksArrayListTypeToken().getType());

        assertEquals(subTask1.getId(), subTasksFromManager.get(0).getId(), "id подзадачи не соответствует.");
        assertEquals(subTask2.getId(), subTasksFromManager.get(1).getId(), "id подзадачи не соответствует.");
        assertEquals("Subtask1", subTasksFromManager.get(0).getTitle(), "Наименование подзадачи не соответствует.");
        assertEquals("Subtask2", subTasksFromManager.get(1).getTitle(), "Наименование подзадачи не соответствует.");
    }

    @Test
    public void getTaskByIdByGetRequest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description", LocalDateTime.of(2024, 01, 01, 01, 00), Duration.ofMinutes(15));
        taskManager.addTask(task);
        int taskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        final Task taskFromManager = gson.fromJson(response.body(), new TypeToken<Task>() {}.getType());

        assertEquals(taskId, taskFromManager.getId(), "id задачи не соответствует.");
        assertEquals("Task", taskFromManager.getTitle(), "Наименование задачи не соответствует.");
    }

    @Test
    public void getEpicByIdByGetRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        int epicId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        final Epic epicFromManager = gson.fromJson(response.body(), new TypeToken<Epic>() {}.getType());

        assertEquals(epicId, epicFromManager.getId(), "id эпика не соответствует.");
        assertEquals("Epic", epicFromManager.getTitle(), "Наименование эпика не соответствует.");
    }

    @Test
    public void getSubtaskByIdByGetRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask);
        int subTaskId = subTask.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        final SubTask subTaskFromManager = gson.fromJson(response.body(), new TypeToken<SubTask>() {}.getType());

        assertEquals(subTaskId, subTaskFromManager.getId(), "id подзадачи не соответствует.");
        assertEquals("Subtask", subTaskFromManager.getTitle(), "Наименование подзадачи не соответствует.");
    }

    @Test
    public void getListOfSubtasksByEpicIdByGetRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask1", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask2", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 01, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask2);

        int epicId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        ArrayList<SubTask> subTasksFromManager = gson.fromJson(response.body(),
                new SubTasksArrayListTypeToken().getType());

        assertEquals(subTask1.getId(), subTasksFromManager.get(0).getId(), "id подзадачи не соответствует.");
        assertEquals(subTask2.getId(), subTasksFromManager.get(1).getId(), "id подзадачи не соответствует.");
        assertEquals("Subtask1", subTasksFromManager.get(0).getTitle(), "Наименование подзадачи не соответствует.");
        assertEquals("Subtask2", subTasksFromManager.get(1).getTitle(), "Наименование подзадачи не соответствует.");
    }

    @Test
    public void updateTaskByPostRequest() throws IOException, InterruptedException {
        Task task = new Task("Task", "Task description",
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addTask(task);
        Task newTask = new Task("Task", "Updated task",
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        newTask.setId(task.getId());

        String taskToJson = gson.toJson(newTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListOfTasks();

        assertEquals("Updated task", tasksFromManager.get(0).getDescription(), "Описание задачи не соответствует.");
    }

    @Test
    public void updateEpicByPostRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        Epic newEpic = new Epic("Epic", "Updated epic");
        newEpic.setId(epic.getId());

        String epicToJson = gson.toJson(newEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getListOfEpics();

        assertEquals("Updated epic", epicsFromManager.get(0).getDescription(), "Описание эпика не соответствует.");
    }

    @Test
    public void updateSubtaskByPostRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask);
        SubTask newSubTask = new SubTask("Subtask", "Updated subtask", 1,
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        newSubTask.setId(subTask.getId());

        String subTaskToJson = gson.toJson(newSubTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = taskManager.getListOfSubTasks();

        assertEquals("Updated subtask", subTasksFromManager.get(0).getDescription(),
                "Описание подзадачи не соответствует.");
    }

    @Test
    public void removeAllTasksByDeleteRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Task description",
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Task description",
                LocalDateTime.of(2024, 01, 01, 01, 00), Duration.ofMinutes(15));
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(taskManager.getListOfTasks().isEmpty(), "Задачи не удалены.");
    }

    @Test
    public void removeAllEpicsByDeleteRequest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Epic2", "Epic description");
        taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(taskManager.getListOfEpics().isEmpty(), "Эпики не удалены.");
    }

    @Test
    public void removeAllSubtasksByDeleteRequest() throws IOException, InterruptedException {
        SubTask subTask1 = new SubTask("Subtask1", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask2", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 01, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertTrue(taskManager.getListOfSubTasks().isEmpty(), "Подзадачи не удалены.");
    }

    @Test
    public void removeTaskByIdByDeleteRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Task description",
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Task description",
                LocalDateTime.of(2024, 01, 01, 01, 00), Duration.ofMinutes(15));
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getListOfTasks().contains(task1), "Задача не удалена.");
    }

    @Test
    public void removeEpicByIdByDeleteRequest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Epic description");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Epic2", "Epic description");
        taskManager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getListOfEpics().contains(epic1), "Эпик не удален.");
    }

    @Test
    public void removeSubtaskByIdByDeleteRequest() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask1", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 00, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask2", "Subtask description", 1,
                LocalDateTime.of(2024, 01, 01, 01, 00), Duration.ofMinutes(15));
        taskManager.addSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask1.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertFalse(taskManager.getListOfSubTasks().contains(subTask1), "Подзадача не удалена.");
    }

    @Test
    public void getPrioritizedTasksByGetRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Task description", LocalDateTime.of(2024, 01, 01, 01, 00),
                Duration.ofMinutes(15));
        Task task2 = new Task("Task2", "Task description", LocalDateTime.of(2024, 01, 01, 02, 00),
                Duration.ofMinutes(15));
        Task task3 = new Task("Task3", "Task description", LocalDateTime.of(2024, 01, 01, 03, 00),
                Duration.ofMinutes(15));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        TreeSet<Task> sortedTasks = taskManager.getPrioritizedTasks();
        String sortedTasksToJson = gson.toJson(sortedTasks);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(sortedTasksToJson, response.body(), "Список задач не соответствует.");
    }

    @Test
    public void getHistoryByGetRequest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Task description", LocalDateTime.of(2024, 01, 01, 01, 00),
                Duration.ofMinutes(15));
        Task task2 = new Task("Task2", "Task description", LocalDateTime.of(2024, 01, 01, 02, 00),
                Duration.ofMinutes(15));
        Task task3 = new Task("Task3", "Task description", LocalDateTime.of(2024, 01, 01, 03, 00),
                Duration.ofMinutes(15));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);

        List<Task> history = taskManager.getHistory();
        String historyToJson = gson.toJson(history);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(historyToJson, response.body(), "Список истории не соответствует.");
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    class TasksArrayListTypeToken extends TypeToken<ArrayList<Task>> {
    }

    class EpicsArrayListTypeToken extends TypeToken<ArrayList<Epic>> {
    }

    class SubTasksArrayListTypeToken extends TypeToken<ArrayList<SubTask>> {
    }
}
