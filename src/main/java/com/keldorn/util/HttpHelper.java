package com.keldorn.util;

import com.keldorn.dto.UriCompactAddress;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.OptionalInt;

public class HttpHelper {
    public static void writeResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, message.getBytes().length);
        exchange.getResponseBody().write(message.getBytes());
    }

    public static void sendJsonError(HttpExchange exchange, int statusCode, String message) throws IOException {
        writeResponse(exchange, statusCode, wrapMessage("error", statusCode, message));
    }

    public static void sendJsonUnknownEndpoint(HttpExchange exchange) throws IOException {
        sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, "This endpoint does not exists.");
    }

    public static void sendJsonUnsupportedMethod(HttpExchange exchange) throws IOException {
        sendJsonError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Unsupported Method");
    }

    public static void sendJsonMalformedJson(HttpExchange exchange) throws IOException {
        sendJsonError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                "Malformed JSON in request body. Please check syntax and field names.");
    }

    public static void sendJsonResult(HttpExchange exchange, int statusCode, String message) throws IOException {
        writeResponse(exchange, statusCode, wrapMessage("result", statusCode, message));
    }

    public static OptionalInt getIdFromURI(HttpExchange exchange, int idSegmentIndex, String errorMsg) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceAll("/+$", "");
        String[] segments = path.split("/");
        if (segments.length < idSegmentIndex + 1) {
            sendJsonError(exchange, HttpURLConnection.HTTP_BAD_REQUEST, errorMsg);
            return OptionalInt.empty();
        }
        OptionalInt response = OptionalInt.empty();
        try {
            response = OptionalInt.of(Integer.parseInt(segments[idSegmentIndex]));
        } catch (NumberFormatException e) {
            sendJsonError(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "The id parameter is not a valid integer.");
        }
        return response;
    }

    public static UriCompactAddress getUriCompactAddress(HttpExchange exchange, int idSegmentIndex, String errorMsg) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceAll("/+$", "");
        String[] segments = path.split("/");
        if (segments.length < idSegmentIndex + 1) {
            sendJsonError(exchange, HttpURLConnection.HTTP_BAD_REQUEST, errorMsg);
            return null;
        }
        int response;
        try {
            response = Integer.parseInt(segments[idSegmentIndex]);
        } catch (NumberFormatException e) {
            sendJsonError(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "The id parameter is not a valid integer.");
            return null;
        }
        return new UriCompactAddress(response, path.replace("/%d".formatted(response), "/{id}"));
    }

    private static String wrapMessage(String type, int statusCode, String message) {
        long timestamp = System.currentTimeMillis();
        return String.format("{\"%s\": {\"code\": %d, \"message\": \"%s\", \"timestamp\": %d}}",
                type, statusCode, message, timestamp);
    }
}
