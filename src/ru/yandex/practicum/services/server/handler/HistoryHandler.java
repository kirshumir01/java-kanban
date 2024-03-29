package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HistoryHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // получить метод
        String method = exchange.getRequestMethod();
        // разбить путь на части
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String response;

        switch (method) {
            case "GET": {
                if (pathParts.length == 2 && pathParts[1].equals("history")) {
                    response = gson.toJson(taskManager.getHistory());
                    writeResponse(exchange, response, 200);
                }
                break;
            }
            default:
                writeResponse(exchange, "Обработка эндпоинта " + method + " не предусмотрена программой", 404);
                break;
        }
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
