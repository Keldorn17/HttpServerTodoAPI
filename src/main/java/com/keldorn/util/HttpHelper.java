package com.keldorn.util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

public class HttpHelper {
    public static void writeResponse(String response, int responseCode, HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(responseCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
    }

    public static int getIdFromURI(HttpExchange exchange, int idSegmentIndex, String errorMsg) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        if (segments.length < idSegmentIndex + 1) {
            writeResponse(errorMsg, HttpURLConnection.HTTP_BAD_REQUEST, exchange);
            return -1;
        }
        return Integer.parseInt(segments[idSegmentIndex]);
    }
}
