package com.keldorn.util.http;

import com.google.gson.JsonSyntaxException;
import com.keldorn.util.json.JsonResponse;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Function;

public class ControllerUtils {
    public static <T> void sendIfExists(HttpExchange exchange, T entity, Function<T, String> mapper, String errorMsg) throws IOException {
        if (entity == null) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_NOT_FOUND, errorMsg);
        } else {
            JsonResponse.writeResponse(exchange, HttpURLConnection.HTTP_OK, mapper.apply(entity));
        }
    }

    public static <T> void deleteIfExists(HttpExchange exchange, T entity, Consumer<T> deleteAction, String errorMsg, String response) throws IOException {
        if (entity == null) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_NOT_FOUND, errorMsg);
        } else {
            deleteAction.accept(entity);
            JsonResponse.sendResult(exchange, HttpURLConnection.HTTP_OK, response);
        }
    }

    public static OptionalInt getTodoId(HttpExchange exchange) throws IOException {
        return UriSegmentReader.getIdFromURI(exchange, 2);
    }

    public static String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes());
    }

    public static void updateService(HttpExchange exchange, ServiceAction action) throws IOException {
        try {
            String response = action.run();
            JsonResponse.writeResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (JsonSyntaxException e) {
            JsonResponse.sendMalformedJson(exchange);
        } catch (InvocationTargetException | IllegalAccessException e) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    "Internal server error: " + e.getMessage());
        } catch (Exception e) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    "Unexpected error: " + e.getMessage());
        }
    }

    @FunctionalInterface
    public interface ServiceAction {
        String run() throws Exception;
    }
}
