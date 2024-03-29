package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicsHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // получить метод
        String method = exchange.getRequestMethod();
        // разбить путь на части
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        // получить id эпика в Optional
        Optional<Integer> epicIdOptional;

        switch (method) {
            case "GET": {
                // ecли путь состоит из частей - "[]/[epics]"
                if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                    String response = gson.toJson(taskManager.getListOfEpics());
                    writeResponse(exchange, response, 200);
                // ecли путь состоит из частей - "[]/[epics]/[id]"
                } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
                    epicIdOptional = getEpicId(exchange);
                    if (epicIdOptional.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика", 404);
                    }
                    int epicId = epicIdOptional.get();

                    if (taskManager.getEpicById(epicId) != null) {
                        String response = taskManager.getListOfEpics()
                                .stream()
                                .filter(epic -> epic.getId() == epicId)
                                .map(epic -> gson.toJson(taskManager.getEpicById(epic.getId())))
                                .collect(Collectors.toList()).get(0);

                        writeResponse(exchange, response, 200);
                    } else {
                        writeResponse(exchange, "Эпик с идентификатором " + epicId + " отсутствует " +
                                "в менеджере задач", 404);
                        return;
                    }
                // ecли путь состоит из частей - "[]/[epics]/[id]/[subtasks]"
                } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
                    epicIdOptional = getEpicId(exchange);
                    if (epicIdOptional.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика", 404);
                    }
                    int epicId = epicIdOptional.get();

                    if (taskManager.getEpicById(epicId) != null) {
                        String response = taskManager.getListOfEpics()
                                .stream()
                                .filter(epic -> epic.getId() == epicId)
                                .map(epic -> gson.toJson(taskManager.getListOfSubTasksByEpic(epic.getId())))
                                .collect(Collectors.toList()).get(0);

                        writeResponse(exchange, response, 200);
                    } else {
                        writeResponse(exchange, "Эпик с идентификатором " + epicId + " отсутствует " +
                                "в менеджере задач", 404);
                        return;
                    }
                }
                break;
            }
            case "DELETE": {
                // ecли путь состоит из частей - "[]/[epics]"
                if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                    taskManager.removeAllEpics();
                    writeResponse(exchange, "Удаление всех эпиков выполнено успешно", 200);
                // ecли путь состоит из частей - "[]/[epics]/[id]"
                } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
                    epicIdOptional = getEpicId(exchange);
                    if (epicIdOptional.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика", 404);
                        return;
                    }
                    int epicId = epicIdOptional.get();

                    if (taskManager.getEpicById(epicId) != null) {
                        taskManager.removeEpicById(epicId);
                        writeResponse(exchange, "Удаление эпика с идентификатором " + epicId + " выполнено успешно", 200);
                    } else {
                        writeResponse(exchange, "Эпик с идентификатором " + epicId + " отсутствует " +
                                "в менеджере задач", 404);
                        return;
                    }
                // ecли путь состоит из частей - "[]/[epics]/[id]/[subtasks]"
                } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
                    epicIdOptional = getEpicId(exchange);
                    if (epicIdOptional.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика", 404);
                        return;
                    }
                    int epicId = epicIdOptional.get();

                    // удалить все подзадачи эпика
                    if (taskManager.getEpicById(epicId) != null) {
                        ArrayList<SubTask> subTasks = taskManager.getListOfSubTasksByEpic(epicId);

                        subTasks
                                .stream()
                                .forEach(subTask -> {
                                    taskManager.removeSubTaskById(subTask.getId());
                                });
                        writeResponse(exchange, "Удаление подзадач эпика с идентификатором " + epicId +
                                " выполнено успешно", 200);
                    } else {
                        writeResponse(exchange, "Эпик с идентификатором " + epicId + " отсутствует " +
                                "в менеджере задач", 404);
                        return;
                    }
                }
                break;
            }
            case "POST": {
                String request = readRequest(exchange);

                try {
                    Epic epic = gson.fromJson(request, Epic.class);
                    // ecли путь состоит из частей - "[]/[epics]"
                    if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                        taskManager.addEpic(epic);
                        writeResponse(exchange, "Эпик успешно добавлен, эпику присвоен идентификатор " +
                                epic.getId(), 201);
                    // ecли путь состоит из частей - "[]/[epics]/[id]"
                    } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
                        epicIdOptional = getEpicId(exchange);
                        if (epicIdOptional.isEmpty()) {
                            writeResponse(exchange, "Некорректный идентификатор эпика", 404);
                            return;
                        }
                        int epicId = epicIdOptional.get();

                        if (taskManager.getEpicById(epicId) != null) {
                            epic.setId(epicId);
                            taskManager.updateEpic(epic);
                            writeResponse(exchange, "Эпик с идентификатором " + epicId + " успешно обновлен", 201);
                        } else {
                            writeResponse(exchange, "Эпик с идентификатором " + epicId + " отсутствует " +
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
                writeResponse(exchange, "Эндпоинт " + method + " не существует", 404);
                break;
            }
        }
    }

    // получение id эпика в Optional
    private Optional<Integer> getEpicId(HttpExchange exchange) {
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