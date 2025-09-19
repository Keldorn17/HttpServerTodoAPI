package com.keldorn.api;

import com.keldorn.domain.entity.User;
import com.keldorn.exception.InvalidEmailException;
import com.keldorn.mapper.TodoMapper;
import com.keldorn.mapper.UserMapper;
import com.keldorn.service.UserService;
import com.keldorn.util.http.ControllerUtils;
import com.keldorn.util.http.UriSegmentReader;
import com.keldorn.util.json.JsonResponse;
import com.keldorn.util.validation.ValidifyEmail;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.exception.ConstraintViolationException;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UserController implements HttpHandler {
    private final EntityManagerFactory emf;
    public final String USER_NOT_FOUND_ERROR = "User not found";

    public UserController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (EntityManager entityManager = emf.createEntityManager()) {
            UserService service = new UserService(entityManager);
            String method = exchange.getRequestMethod();
            switch (method) {
                case "POST" -> handlePost(exchange, service);
                case "GET" -> handleGet(exchange, service);
                case "DELETE" -> handleDelete(exchange, service);
                default -> JsonResponse.sendUnsupportedMethod(exchange);
            }
        } catch (Exception e) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleDelete(HttpExchange exchange, UserService service) throws IOException {
        ControllerUtils.getTodoId(exchange).ifPresent(id -> {
            try {
                ControllerUtils.deleteIfExists(exchange, service.findById(id), service::delete,
                        USER_NOT_FOUND_ERROR, "User successfully deleted.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleGet(HttpExchange exchange, UserService service) throws IOException {
        var dto = UriSegmentReader.getUriCompactAddress(exchange, 2);
        if (dto != null) {
            switch (dto.idCapsulatedUriString()) {
                case ApiRoutes.USER_BY_ID -> ControllerUtils.sendIfExists(exchange, service.findById(dto.id()),
                        UserMapper::getJsonUserDetailed, USER_NOT_FOUND_ERROR);
                case ApiRoutes.USER_TODOS -> ControllerUtils.sendIfExists(exchange, service.findTodosByUserId(dto.id()),
                        TodoMapper::getTodosResponse, USER_NOT_FOUND_ERROR);
                default -> JsonResponse.sendUnknownEndpoint(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange, UserService service) throws IOException, InvalidEmailException {
        String data = ControllerUtils.readRequestBody(exchange);
        User user = UserMapper.handleCreateUserRequest(data);
        ValidifyEmail.validify(user.getEmail());
        try {
            service.save(user);
        } catch (ConstraintViolationException e) {
            JsonResponse.sendError(exchange, HttpURLConnection.HTTP_CONFLICT, "Email already exists");
        }
        JsonResponse.writeResponse(exchange, HttpURLConnection.HTTP_CREATED,
                "{\"userId\": %d}".formatted(user.getUserId()));
    }
}
