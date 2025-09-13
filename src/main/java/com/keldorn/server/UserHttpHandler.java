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
                default -> HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_BAD_METHOD,
                        "Unsupported method");
            }
        } catch (Exception e) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, EntityManager entityManager) throws IOException {
        int userId = HttpHelper.getIdFromURI(exchange, 2, "User ID missing");
        if (userId != -1) {
            handleGetUserResponse(userId, entityManager, exchange);
        }
    }

    private void handlePost(HttpExchange exchange, EntityManager entityManager) throws IOException, InvalidEmailException {
        String data = new String(exchange.getRequestBody().readAllBytes());
        User user = UserHandler.handleCreateUserRequest(data);

        ValidifyEmail.validify(user.getEmail());
        new UserRepository(entityManager).save(user);
        HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_CREATED,
                "{\"userId\": %d}".formatted(user.getUserId()));
    }

    private static void handleGetUserResponse(int id, EntityManager entityManager, HttpExchange exchange)
            throws IOException {
        User user = new UserRepository(entityManager).findById(id);

        if (user == null) {
            HttpHelper.sendJsonError(exchange, HttpURLConnection.HTTP_NOT_FOUND, "User not found");
        } else {
            HttpHelper.writeResponse(exchange, HttpURLConnection.HTTP_OK,
                    UserHandler.getJsonUser(user));
        }
    }
}
