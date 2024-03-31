package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.TaskStatus;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicsHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET": {
                    // ecли путь "/epics"
                    if (Pattern.matches("^/epics$", path)) {
                        String response = gson.toJson(taskManager.getListOfEpics());
                        writeResponse(exchange, response);
                        break;
                    }

                    // ecли путь "/epics/{id}"
                    if (Pattern.matches("^/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/epics/", "");
                        int id = parsePathId(pathId);

                        if (id != -1 && taskManager.getEpicById(id) != null) {
                            String response = gson.toJson(taskManager.getEpicById(id));
                            writeResponse(exchange, response);
                        } else {
                            System.out.println("Получен некорректный идентификатор эпика: " + pathId);
                            exchange.sendResponseHeaders(404, 0);
                        }
                        break;
                    }

                    // ecли путь "/epics/{id}/subtasks"
                    if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        String pathId = path.replaceFirst("/epics/", "")
                                .replaceFirst("/subtasks", "");
                        int id = parsePathId(pathId);

                        if (id != -1 && taskManager.getEpicById(id) != null) {
                            String response = gson.toJson(taskManager.getListOfSubTasksByEpic(id));
                            writeResponse(exchange, response);
                        } else {
                            System.out.println("Получен некорректный идентификатор эпика: " + pathId);
                            exchange.sendResponseHeaders(404, 0);
                        }
                        break;
                    }
                    break;
                }
                case "DELETE": {
                    // ecли путь "/epics?id=[id]"
                    if (Pattern.matches("^/epics$", path)) {
                        String query = exchange.getRequestURI().getQuery();

                        if (query != null) {
                            String pathId = query.substring(3);
                            int id = parsePathId(pathId);

                            if (id != -1 && taskManager.getEpicById(id) != null) {
                                taskManager.removeEpicById(id);
                                System.out.println("Эпик с идентификатором " + id + " удален");
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                System.out.println("Получен некорректный идентификатор эпика: " + pathId);
                                exchange.sendResponseHeaders(404, 0);
                            }
                        } else {
                            System.out.println("В строке запроса отсутствуют параметры");
                            exchange.sendResponseHeaders(404, 0);
                        }
                    }
                    break;
                }
                case "POST": {
                    String request = readRequest(exchange);
                    Epic epic = gson.fromJson(request, Epic.class);
                    epic.setStatus(TaskStatus.NEW);

                    // ecли путь "/epics"
                    if (Pattern.matches("^/epics$", path)) {
                        taskManager.addEpic(epic);
                        System.out.println("Эпик успешно добавлен, эпику присвоен идентификатор " + epic.getId());
                        exchange.sendResponseHeaders(201, 0);
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
            exchange.close();
        }
    }

    private int parsePathId(String pathId) {
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    // обработать запрос клиента
    private String readRequest(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    // сформировать ответ сервера
    private void writeResponse(HttpExchange exchange, String responseString) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
    }
}