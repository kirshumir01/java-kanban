package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.models.TaskStatus;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TasksHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
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
                    // ecли путь "/tasks"
                    if (Pattern.matches("^/tasks$", path)) {
                        String response = gson.toJson(taskManager.getListOfTasks());
                        writeResponse(exchange, response);
                        break;
                    }

                    // ecли путь "/tasks/{id}"
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);

                        if (id != -1 && taskManager.getTaskById(id) != null) {
                            String response = gson.toJson(taskManager.getTaskById(id));
                            writeResponse(exchange, response);
                        } else {
                            System.out.println("Получен некорректный идентификатор задачи: " + pathId);
                            exchange.sendResponseHeaders(404, 0);
                        }
                        break;
                    }
                    break;
                }
                case "DELETE": {
                    // ecли путь "/tasks?id=[id]"
                    if (Pattern.matches("^/tasks$", path)) {
                        String query = exchange.getRequestURI().getQuery();

                        if (query != null) {
                            String pathId = query.substring(3);
                            int id = parsePathId(pathId);

                            if (id != -1 && taskManager.getTaskById(id) != null) {
                                taskManager.removeTaskById(id);
                                System.out.println("Задача с идентификатором " + id + " удалена");
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                System.out.println("Получен некорректный идентификатор задачи: " + pathId);
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
                    Task task = gson.fromJson(request, Task.class);
                    task.setStatus(TaskStatus.NEW);

                    // проверка задачи на пересечение с остальными задачами в sortedList
                    if (taskManager.isCrossingTasks(task)) {
                        System.out.println("Задача пересекается по времени с существующими задачами");
                        exchange.sendResponseHeaders(406, 0);
                        break;
                    }

                    // ecли путь "/tasks"
                    if (Pattern.matches("^/tasks$", path)) {
                        String query = exchange.getRequestURI().getQuery();

                        if (query != null) {
                            String pathId = query.substring(3);
                            int id = parsePathId(pathId);

                            if (id != -1 && taskManager.getTaskById(id) != null) {
                                taskManager.updateTask(task);
                                System.out.println("Задача c идентификатором " + task.getId() + " успешно обновлена");
                                exchange.sendResponseHeaders(201, 0);
                            } else {
                                System.out.println("Задача с идентификатором " + pathId + " отсутствует");
                                exchange.sendResponseHeaders(404, 0);
                            }
                        } else {
                            taskManager.addTask(task);
                            System.out.println("Задача успешно добавлена, задаче присвоен идентификатор " + task.getId());
                            exchange.sendResponseHeaders(201, 0);
                        }
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