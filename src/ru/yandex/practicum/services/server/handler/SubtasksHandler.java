package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubtasksHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // получить метод
        String method = exchange.getRequestMethod();
        // разбить путь на части
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        // получить id подзадачи в Optional
        Optional<Integer> subTaskIdOptional;

        switch (method) {
            case "GET": {
                // ecли путь состоит из частей - "[]/[subtasks]"
                if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                    String response = gson.toJson(taskManager.getListOfSubTasks());
                    writeResponse(exchange, response, 200);
                // ecли путь состоит из частей - "[]/[subtasks]/[id]"
                } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
                    subTaskIdOptional = getSubTaskId(exchange);
                    if (subTaskIdOptional.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор подзадачи", 404);
                    }
                    int subTaskId = subTaskIdOptional.get();

                    if (taskManager.getSubTaskById(subTaskId) != null) {
                        String response = taskManager.getListOfSubTasks()
                                .stream()
                                .filter(subTask -> subTask.getId() == subTaskId)
                                .map(subTask -> gson.toJson(taskManager.getSubTaskById(subTask.getId())))
                                .collect(Collectors.toList()).get(0);

                        writeResponse(exchange, response, 200);
                    } else {
                        writeResponse(exchange, "Подзадача с идентификатором " + subTaskId + " отсутствует " +
                                "в менеджере задач", 404);
                        return;
                    }
                }
                break;
            }
            case "DELETE": {
                // ecли путь состоит из частей - "[]/[subtasks]"
                if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                    taskManager.removeAllSubTasks();
                    writeResponse(exchange, "Удаление всех подзадач выполнено успешно", 200);
                 // ecли путь состоит из частей - "[]/[subtasks]/[id]"
                } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
                    subTaskIdOptional = getSubTaskId(exchange);
                    if (subTaskIdOptional.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор подзадачи", 404);
                        return;
                    }
                    int subTaskId = subTaskIdOptional.get();

                    if (taskManager.getSubTaskById(subTaskId) != null) {
                        taskManager.removeSubTaskById(subTaskId);
                        writeResponse(exchange, "Удаление подзадачи с идентификатором " + subTaskId +
                                " выполнено успешно", 200);
                    } else {
                        writeResponse(exchange, "Задача с идентификатором " + subTaskId +
                                " отсутствует в менеджере задач", 404);
                        return;
                    }
                }
                break;
            }
            case "POST": {
                String request = readRequest(exchange);

                try {
                    SubTask subTask = gson.fromJson(request, SubTask.class);
                    // ecли путь состоит из частей - "[]/[subtasks]"
                    if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
                        // проверка подзадачи на пересечение с остальными задачами в sortedList;
                        if (taskManager.isCrossingTasks(subTask)) {
                            writeResponse(exchange, "Подзадача пересекается по времени с существующими задачами", 406);
                            return;
                        }

                        taskManager.addSubTask(subTask);
                        writeResponse(exchange, "Подзадача успешно добавлена, подзадаче присвоен идентификатор " +
                                subTask.getId(), 201);
                    // ecли путь состоит из частей - "[]/[subtasks]/[id]"
                    } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
                        // проверка подзадачи на пересечение с остальными задачами в sortedList
                        // за исключением обновляемой
                        if (taskManager.isCrossingTasks(subTask) && taskManager.getListOfSubTasks().contains(subTask)) {
                            writeResponse(exchange, "Подзадача пересекается по времени с существующими задачами", 406);
                            return;
                        }

                        subTaskIdOptional = getSubTaskId(exchange);
                        if (subTaskIdOptional.isEmpty()) {
                            writeResponse(exchange, "Некорректный идентификатор подзадачи", 404);
                            return;
                        }
                        int subTaskId = subTaskIdOptional.get();

                        if (taskManager.getSubTaskById(subTaskId) != null) {
                            subTask.setId(subTaskId);
                            taskManager.updateSubTask(subTask);
                            writeResponse(exchange, "Подзадача с идентификатором " + subTaskId +
                                    " успешно обновлена", 201);
                        } else {
                            writeResponse(exchange, "Задача с идентификатором " + subTaskId + " отсутствует " +
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

    // получение id подзадачи в Optional
    private Optional<Integer> getSubTaskId(HttpExchange exchange) {
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