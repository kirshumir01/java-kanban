package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.services.exceptions.ManagerTaskNotFoundException;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class EpicsHandler extends AbstractHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try (exchange) {
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
                        String pathId = path.substring(7);
                        int id = parsePathId(pathId);
                        String response = gson.toJson(taskManager.getEpicById(id));
                        writeResponse(exchange, response);
                        break;
                    }

                    // ecли путь "/epics/{id}/subtasks"
                    if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
                        String pathId = path.substring(7, (path.length() - 9));
                        int id = parsePathId(pathId);
                        String response = gson.toJson(taskManager.getListOfSubTasksByEpic(id));
                        writeResponse(exchange, response);
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
                            taskManager.removeEpicById(id);
                            sendDeletedTaskContentResponseHeaders(exchange, id);
                        } else if (query == null) {
                            taskManager.removeAllEpics();
                            sendDeletedAllTasksContentResponseHeaders(exchange);
                        } else {
                            sendNotFoundIdInQueryStringResponseHeaders(exchange);
                        }
                    }
                    break;
                }
                case "POST": {
                    String request = readRequest(exchange);
                    Epic epic = gson.fromJson(request, Epic.class);

                    // ecли путь "/epics"
                    if (Pattern.matches("^/epics$", path)) {
                        if (epic.getId() != null) {
                            taskManager.updateEpic(epic);
                            sendUpdatedTaskContentResponseHeaders(exchange, epic.getId());
                        } else {
                            taskManager.addEpic(epic);
                            sendCreatedTaskContentResponseHeaders(exchange, epic.getId());
                        }
                    }
                    break;
                }
                default: {
                    sendNotFoundEndpointResponseHeaders(exchange, method);
                }
            }
        } catch (NumberFormatException exception) {
            sendErrorRequestResponseHeaders(exchange);
        } catch (ManagerTaskNotFoundException exception) {
            sendNotFoundRequestResponseHeaders(exchange);
        } catch (Throwable exception) {
            exception.printStackTrace();
            sendInternalServerErrorResponseHeaders(exchange);
        }
    }
}