package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.services.exceptions.ManagerTaskNotFoundException;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class SubtasksHandler extends AbstractHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager) {
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
                    // ecли путь "/subtasks"
                    if (Pattern.matches("^/subtasks$", path)) {
                        String response = gson.toJson(taskManager.getListOfSubTasks());
                        writeResponse(exchange, response);
                        break;
                    }

                    // ecли путь "/subtasks/{id}"
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        String response = gson.toJson(taskManager.getSubTaskById(id));
                        writeResponse(exchange, response);
                        break;
                    }
                    break;
                }
                case "DELETE": {
                    // ecли путь "/subtasks?id=[id]"
                    if (Pattern.matches("^/subtasks$", path)) {
                        String query = exchange.getRequestURI().getQuery();

                        if (query != null) {
                            String pathId = query.substring(3);
                            int id = parsePathId(pathId);
                            taskManager.removeSubTaskById(id);
                            System.out.println("Подзадача с идентификатором " + id + " удалена");
                            sendDeletedTaskContentResponseHeaders(exchange);
                        } else {
                            System.out.println("В строке запроса отсутствуют параметры");
                            exchange.sendResponseHeaders(404, 0);
                        }
                    }
                    break;
                }
                case "POST": {
                    String request = readRequest(exchange);
                    SubTask subTask = gson.fromJson(request, SubTask.class);

                    // проверка задачи на пересечение с остальными задачами в sortedList
                    if (taskManager.isCrossingTasks(subTask)) {
                        System.out.println("Подзадача пересекается по времени с существующими задачами");
                        sendIsCrossingTasksResponseHeaders(exchange);
                        break;
                    }

                    // ecли путь "/subtasks?id=[id]"
                    if (Pattern.matches("^/subtasks$", path)) {
                        String query = exchange.getRequestURI().getQuery();

                        if (query != null) {
                            String pathId = query.substring(3);
                            int id = parsePathId(pathId);
                            subTask.setId(id);
                            taskManager.updateSubTask(subTask);
                            System.out.println("Подзадача c идентификатором " + subTask.getId() + " успешно обновлена");
                            sendCreatedTaskContentResponseHeaders(exchange);
                        } else {
                            taskManager.addSubTask(subTask);
                            System.out.println("Подзадача успешно добавлена, подзадаче присвоен идентификатор "
                                    + subTask.getId());
                            sendCreatedTaskContentResponseHeaders(exchange);
                        }
                    }
                    break;
                }
                default: {
                    System.out.println("Обработка эндпоинта " + method + " не предусмотрена программой");
                    exchange.sendResponseHeaders(404, 0);
                }
            }
        } catch (NumberFormatException exception) {
            sendErrorRequestResponseHeaders(exchange);
        } catch (ManagerTaskNotFoundException exception) {
            System.out.println("Получен некорректный идентификатор эпика");
            sendNotFoundRequestResponseHeaders(exchange);
        } finally {
            sendInternalServerErrorResponseHeaders(exchange);
            exchange.close();
        }
    }
}