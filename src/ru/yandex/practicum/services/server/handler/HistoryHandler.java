package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class HistoryHandler extends AbstractHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) {
        final InputStream inputStream = exchange.getRequestBody();
        final OutputStream outputStream = exchange.getResponseBody();

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try (inputStream; outputStream) {
            switch (method) {
                case "GET": {
                    // ecли путь "/tasks"
                    if (Pattern.matches("^/history$", path)) {
                        String response = gson.toJson(taskManager.getHistory());
                        writeResponse(exchange, response);
                    }
                    break;
                }
                default: {
                    sendNotFoundEndpointResponseHeaders(exchange, method);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } catch (Throwable exception) {
            Throwable[] suppressedExceptions = exception.getSuppressed();

            for (int i = 0; i < suppressedExceptions.length; i++) {
                System.out.println("Подавленные исключения:");
                System.out.println(i + ". " + suppressedExceptions[i]);
            }
        }
    }
}
