package com.keldorn.util.http;

import com.keldorn.dto.user.UriCompactAddress;
import com.keldorn.util.json.JsonResponse;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.OptionalInt;

public class UriSegmentReader {
    public static OptionalInt getIdFromURI(HttpExchange exchange, int idSegmentIndex) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceAll("/+$", "");
        String[] segments = path.split("/");
        if (segments.length < idSegmentIndex + 1) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    "Internal server error: idSegmentIndex is out of bounds");
            return OptionalInt.empty();
        }
        OptionalInt response = OptionalInt.empty();
        try {
            response = OptionalInt.of(Integer.parseInt(segments[idSegmentIndex]));
        } catch (NumberFormatException e) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    "The id parameter is not a valid integer.");
        }
        return response;
    }

    public static UriCompactAddress getUriCompactAddress(HttpExchange exchange, int idSegmentIndex) throws IOException {
        String path = exchange.getRequestURI().getPath().replaceAll("/+$", "");
        String[] segments = path.split("/");
        if (segments.length < idSegmentIndex + 1) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                    "Internal server error: idSegmentIndex is out of bounds");
            return null;
        }
        int response;
        try {
            response = Integer.parseInt(segments[idSegmentIndex]);
        } catch (NumberFormatException e) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_BAD_REQUEST,
                    "The id parameter is not a valid integer.");
            return null;
        }
        return new UriCompactAddress(response, path.replace("/%d".formatted(response), "/{id}"));
    }
}
