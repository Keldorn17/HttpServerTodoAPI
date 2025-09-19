package com.keldorn.util.json;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

public class JsonResponse {
    public static void writeResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, message.getBytes().length);
        exchange.getResponseBody().write(message.getBytes());
    }

    public static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        writeResponse(exchange, statusCode, wrapMessage("error", statusCode, message));
    }

    public static void sendUnknownEndpoint(HttpExchange exchange) throws IOException {
        sendError(exchange, HttpURLConnection.HTTP_NOT_FOUND, "This endpoint does not exists.");
    }

    public static void sendUnsupportedMethod(HttpExchange exchange) throws IOException {
        sendError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Unsupported Method");
    }

    public static void sendMalformedJson(HttpExchange exchange) throws IOException {
        sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                "Malformed JSON in request body. Please check syntax and field names.");
    }

    public static void sendResult(HttpExchange exchange, int statusCode, String message) throws IOException {
        writeResponse(exchange, statusCode, wrapMessage("result", statusCode, message));
    }

    private static String wrapMessage(String type, int statusCode, String message) {
        long timestamp = System.currentTimeMillis();
        return String.format("{\"%s\": {\"code\": %d, \"message\": \"%s\", \"timestamp\": %d}}",
                type, statusCode, message, timestamp);
    }
}
