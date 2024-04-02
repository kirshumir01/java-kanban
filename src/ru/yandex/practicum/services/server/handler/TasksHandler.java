package ru.yandex.practicum.services.server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.exceptions.ManagerTaskNotFoundException;
import ru.yandex.practicum.services.server.HttpTaskServer;
import ru.yandex.practicum.services.taskmanager.TaskManager;

import java.io.IOException;
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
                            System.out.println("Задача с идентификатором " + id + " удалена");
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
                    Task task = gson.fromJson(request, Task.class);

                    // проверка задачи на пересечение с остальными задачами в sortedList
                    if (taskManager.isCrossingTasks(task)) {
                        System.out.println("Задача пересекается по времени с существующими задачами");
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
                            System.out.println("Задача c идентификатором " + task.getId() + " успешно обновлена");
                            sendCreatedTaskContentResponseHeaders(exchange);
                        } else {
                            taskManager.addTask(task);
                            System.out.println("Задача успешно добавлена, задаче присвоен идентификатор " + task.getId());
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
            System.out.println("Получен некорректный идентификатор задачи");
            sendNotFoundRequestResponseHeaders(exchange);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            sendInternalServerErrorResponseHeaders(exchange);
            exchange.close();
        }
    }
}