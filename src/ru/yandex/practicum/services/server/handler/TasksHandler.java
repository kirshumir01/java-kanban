package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class TasksHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // получить метод
        String method = exchange.getRequestMethod();
        // разбить путь на части
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        // получить id задачи в Optional
        Optional<Integer> taskIdOptional;

        switch (method) {
            case "GET": {
                // ecли путь состоит из частей - "[]/[tasks]"
                if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                    String response = gson.toJson(taskManager.getListOfTasks());
                    writeResponse(exchange, response, 200);
                // ecли путь состоит из частей - "[]/[tasks]/[id]"
                } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                    taskIdOptional = getTaskId(exchange);
                    if (taskIdOptional.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор задачи", 404);
                    }
                    int taskId = taskIdOptional.get();

                    if (taskManager.getTaskById(taskId) != null) {
                        String response = taskManager.getListOfTasks()
                                .stream()
                                .filter(task -> task.getId() == taskId)
                                .map(task -> gson.toJson(taskManager.getTaskById(task.getId())))
                                .collect(Collectors.toList()).get(0);

                        writeResponse(exchange, response, 200);
                    } else {
                        writeResponse(exchange, "Задача с идентификатором " + taskId + " отсутствует " +
                                "в менеджере задач", 404);
                        return;
                    }
                }
                break;
            }
            case "DELETE": {
                // ecли путь состоит из частей - "[]/[tasks]"
                if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                    taskManager.removeAllTasks();
                    writeResponse(exchange, "Удаление всех задач выполнено успешно", 200);
                // ecли путь состоит из частей - "[]/[tasks]/[id]"
                } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                    taskIdOptional = getTaskId(exchange);
                    if (taskIdOptional.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор задачи", 404);
                        return;
                    }
                    int taskId = taskIdOptional.get();

                    if (taskManager.getTaskById(taskId) != null) {
                        taskManager.removeTaskById(taskId);
                        writeResponse(exchange, "Удаление задачи с идентификатором " + taskId + " выполнено успешно",
                                200);
                    } else {
                        writeResponse(exchange, "Задача с идентификатором " + taskId + " отсутствует в менеджере задач",
                                404);
                        return;
                    }
                }
                break;
            }
            case "POST": {
                String request = readRequest(exchange);

                try {
                    Task task = gson.fromJson(request, Task.class);
                    // ecли путь состоит из частей - "[]/[tasks]"
                    if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                        // проверка задачи на пересечение с остальными задачами в sortedList
                        if (taskManager.isCrossingTasks(task)) {
                            writeResponse(exchange, "Задача пересекается по времени с существующими задачами", 406);
                            return;
                        }

                        taskManager.addTask(task);
                        writeResponse(exchange, "Задача успешно добавлена, задаче присвоен идентификатор " +
                                task.getId(), 201);
                    // ecли путь состоит из частей - "[]/[tasks]/[id]"
                    } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                        // проверка задачи на пересечение с остальными задачами в sortedList
                        // за исключением обновляемой
                        if (taskManager.isCrossingTasks(task) && taskManager.getListOfTasks().contains(task)) {
                            writeResponse(exchange, "Задача пересекается по времени с существующими задачами", 406);
                            return;
                        }

                        taskIdOptional = getTaskId(exchange);
                        if (taskIdOptional.isEmpty()) {
                            writeResponse(exchange, "Некорректный идентификатор задачи", 404);
                            return;
                        }
                        int taskId = taskIdOptional.get();

                        if (taskManager.getTaskById(taskId) != null) {
                            task.setId(taskId);
                            taskManager.updateTask(task);
                            writeResponse(exchange, "Задача с идентификатором " + taskId + " успешно обновлена", 201);
                        } else {
                            writeResponse(exchange, "Задача с идентификатором " + taskId + " отсутствует " +
                                    "в менеджере задач", 404);
                            return;
                        }
                    }
                } catch (JsonSyntaxException e) {
                    writeResponse(exchange, "Некорректный формат JSON-объекта", 404);
                }
                break;
            }
            default: {
                writeResponse(exchange, "Обработка эндпоинта " + method + " не предусмотрена программой", 404);
                break;
            }
        }
    }

    // получение id задачи в Optional
    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    // обработать запрос клиента
    private String readRequest(HttpExchange exchange) throws IOException {
        // получить входящий поток байтов
        InputStream inputStream = exchange.getRequestBody();
        // получить данные запроса в виде массива байтов и конвертировать их в строку
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    // сформировать ответ сервера
    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}