package ru.yandex.practicum.services.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.filemanager.FileBackedTaskManager;
import ru.yandex.practicum.services.server.adapters.*;
import ru.yandex.practicum.services.server.handler.*;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager manager;
    private Gson gson;

    // создать конструктор
    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;

        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        // создать эндпоинты на каждый из пяти путей:
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));

        gson = getGson();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .setPrettyPrinting();

        return gsonBuilder.create();
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        // параметр delay – максимальное время ожидания в секундах до завершения обмена
        httpServer.stop(5);
        System.out.println("HTTP-сервер остановлен!");
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(new File(("resources/history.csv")));
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

        httpTaskServer.start();
        // httpTaskServer.stop();
    }
}
