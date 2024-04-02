package ru.yandex.practicum.services.server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class AbstractHandler implements HttpHandler {

    protected int parsePathId(String pathId) {
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    protected String readRequest(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void writeResponse(HttpExchange exchange, String responseString) throws IOException {
        byte[] response = responseString.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
    }

    protected void sendCreatedTaskContentResponseHeaders(HttpExchange exchange, int id) throws IOException {
        System.out.println("Задача успешно добавлена, задаче присвоен идентификатор " + id);
        exchange.sendResponseHeaders(201, -1);
    }

    protected void sendUpdatedTaskContentResponseHeaders(HttpExchange exchange, int id) throws IOException {
        System.out.println("Задача c идентификатором " + id + " успешно обновлена");
        exchange.sendResponseHeaders(201, -1);
    }

    protected void sendDeletedTaskContentResponseHeaders(HttpExchange exchange, int id) throws IOException {
        System.out.println("Задача с идентификатором " + id + " удалена");
        exchange.sendResponseHeaders(204, -1);
    }

    protected void sendErrorRequestResponseHeaders(HttpExchange exchange) throws IOException {
        System.out.println("Формат полученных данных не соответствует");
        exchange.sendResponseHeaders(400, -1);
    }

    protected void sendNotFoundRequestResponseHeaders(HttpExchange exchange) throws IOException {
        System.out.println("Получен некорректный идентификатор");
        exchange.sendResponseHeaders(404, -1);
    }

    protected void sendNotFoundIdInQueryStringResponseHeaders(HttpExchange exchange) throws IOException {
        System.out.println("В строке запроса отсутствуют параметры");
        exchange.sendResponseHeaders(404, -1);
    }

    protected void sendNotFoundEndpointResponseHeaders(HttpExchange exchange, String method) throws IOException {
        System.out.println("Обработка эндпоинта " + method + " не предусмотрена программой");
        exchange.sendResponseHeaders(404, -1);
    }

    protected void sendIsCrossingTasksResponseHeaders(HttpExchange exchange) throws IOException {
        System.out.println("Время выполнения задачи пересекается со временем существующих задач");
        exchange.sendResponseHeaders(406, -1);
    }

    protected void sendInternalServerErrorResponseHeaders(HttpExchange exchange) throws IOException {
        System.out.println("Ошибка при обработке запроса");
        exchange.sendResponseHeaders(500, -1);
    }
}
