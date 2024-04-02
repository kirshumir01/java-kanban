package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class PrioritizedHandler extends AbstractHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET": {
                    // ecли путь "/tasks"
                    if (Pattern.matches("^/prioritized$", path)) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        writeResponse(exchange, response);
                    }
                    break;
                }
                default: {
                    System.out.println("Обработка эндпоинта " + method + " не предусмотрена программой");
                    exchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            sendInternalServerErrorResponseHeaders(exchange);
            exchange.close();
        }
    }
}
