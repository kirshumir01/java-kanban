package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.exceptions.ManagerTaskNotFoundException;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class TasksHandler extends AbstractHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final InputStream inputStream = exchange.getRequestBody();
        final OutputStream outputStream = exchange.getResponseBody();

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try (inputStream; outputStream) {
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
                        String response = gson.toJson(taskManager.getTaskById(id));
                        writeResponse(exchange, response);
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
                            taskManager.removeTaskById(id);
                            sendDeletedTaskContentResponseHeaders(exchange, id);
                        } else {
                            sendNotFoundIdInQueryStringResponseHeaders(exchange);
                        }
                    }
                    break;
                }
                case "POST": {
                    String request = readRequest(exchange);
                    Task task = gson.fromJson(request, Task.class);

                    // проверка задачи на пересечение с остальными задачами в sortedList
                    if (taskManager.isCrossingTasks(task)) {
                        sendIsCrossingTasksResponseHeaders(exchange);
                        break;
                    }

                    // ecли путь "/tasks"
                    if (Pattern.matches("^/tasks$", path)) {
                        String query = exchange.getRequestURI().getQuery();

                        if (query != null) {
                            String pathId = query.substring(3);
                            int id = parsePathId(pathId);
                            task.setId(id);
                            taskManager.updateTask(task);
                            sendUpdatedTaskContentResponseHeaders(exchange, id);
                        } else {
                            taskManager.addTask(task);
                            sendCreatedTaskContentResponseHeaders(exchange, task.getId());
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
            System.out.println("Получен некорректный идентификатор задачи");
            sendNotFoundRequestResponseHeaders(exchange);
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