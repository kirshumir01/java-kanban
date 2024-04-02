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

    protected void sendCreatedTaskContentResponseHeaders(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, -1);
    }

    protected void sendDeletedTaskContentResponseHeaders(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(204, -1);
    }

    protected void sendErrorRequestResponseHeaders(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, -1);
    }

    protected void sendNotFoundRequestResponseHeaders(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, -1);
    }

    protected void sendIsCrossingTasksResponseHeaders(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, -1);
    }

    protected void sendInternalServerErrorResponseHeaders(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, -1);
    }
}
