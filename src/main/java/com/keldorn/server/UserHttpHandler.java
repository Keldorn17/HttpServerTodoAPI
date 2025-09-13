package com.keldorn.server;

import com.keldorn.entity.User;
import com.keldorn.exceptions.InvalidEmailException;
import com.keldorn.repository.UserRepository;
import com.keldorn.util.HttpHelper;
import com.keldorn.util.UserHandler;
import com.keldorn.util.ValidifyEmail;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UserHttpHandler implements HttpHandler {
    private final EntityManagerFactory emf;

    public UserHttpHandler(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (EntityManager entityManager = emf.createEntityManager()) {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "POST" -> handlePost(exchange, entityManager);
                case "GET" -> handleGet(exchange, entityManager);
                default -> HttpHelper.writeResponse("{\"error\":\"Unsupported method\"}",
                        HttpURLConnection.HTTP_BAD_METHOD, exchange);
            }
        } catch (Exception e) {
            String error = "{\"error\":\"" + e.getMessage() + "\"}";
            HttpHelper.writeResponse(error, HttpURLConnection.HTTP_INTERNAL_ERROR, exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, EntityManager entityManager) throws IOException {
        String error = "{\"error\":\"User ID missing\"}";
        int userId = HttpHelper.getIdFromURI(exchange, 2, error);
        if (userId != -1) {
            handleGetUserResponse(userId, entityManager, exchange);
        }
    }

    private void handlePost(HttpExchange exchange, EntityManager entityManager) throws IOException, InvalidEmailException {
        String data = new String(exchange.getRequestBody().readAllBytes());
        User user = UserHandler.handleCreateUserRequest(data);

        ValidifyEmail.validify(user.getEmail());
        new UserRepository(entityManager).save(user);
        var response = "{\"userId\": %d}"
                .formatted(user.getUserId());
        HttpHelper.writeResponse(response, HttpURLConnection.HTTP_CREATED, exchange);
    }

    private static void handleGetUserResponse(int id, EntityManager entityManager, HttpExchange exchange)
            throws IOException {
        User user = new UserRepository(entityManager).findById(id);

        if (user == null) {
            String notFound = "{\"error\":\"User not found\"}";
            HttpHelper.writeResponse(notFound, HttpURLConnection.HTTP_NOT_FOUND, exchange);
        } else {
            String response = UserHandler.getJsonUser(user);
            HttpHelper.writeResponse(response, HttpURLConnection.HTTP_OK, exchange);
        }
    }
}
