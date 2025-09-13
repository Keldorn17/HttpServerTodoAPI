package com.keldorn.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

public class HttpHelper {
    public static void writeResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, message.getBytes().length);
        exchange.getResponseBody().write(message.getBytes());
    }

    public static void sendJsonError(HttpExchange exchange, int statusCode, String message) throws IOException {
        long timestamp = System.currentTimeMillis();
        String json = String.format(
                "{\"error\": {\"code\": %d, \"message\": \"%s\", \"timestamp\": %d}}",
                statusCode, message, timestamp
        );
        writeResponse(exchange, statusCode, json);
    }

    public static void sendJsonResult(HttpExchange exchange, int statusCode, String message) throws IOException {
        long timestamp = System.currentTimeMillis();
        String json = String.format(
                "{\"result\": {\"code\": %d, \"message\": \"%s\", \"timestamp\": %d}}",
                statusCode, message, timestamp
        );
        writeResponse(exchange, statusCode, json);
    }

    public static int getIdFromURI(HttpExchange exchange, int idSegmentIndex, String errorMsg) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        if (segments.length < idSegmentIndex + 1) {
            sendJsonError(exchange, HttpURLConnection.HTTP_BAD_REQUEST, errorMsg);
            return -1;
        }
        return Integer.parseInt(segments[idSegmentIndex]);
    }
}
