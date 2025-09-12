package com.keldorn.server;

import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import com.keldorn.entity.User;
import com.keldorn.repository.UserRepository;
import com.keldorn.util.UserHandler;
import com.keldorn.util.ValidifyEmail;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public class TodoAPI {
    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        try {
            emf = Persistence.createEntityManagerFactory("main.java.com.keldorn.entity");
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/users", exchange -> {
                if (exchange.getRequestMethod().equals("POST")) {
                    String data = new String(exchange.getRequestBody().readAllBytes());
                    User user = UserHandler.handleCreateUserRequest(data);
                    try (EntityManager entityManager = emf.createEntityManager()) {
                        ValidifyEmail.validify(user.getEmail());
                        UserRepository userRepository = new UserRepository(entityManager);
                        userRepository.save(user);
                        var response = "{\"userId\": %d}"
                                .formatted(user.getUserId()).getBytes();
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_CREATED, response.length);
                        exchange.getResponseBody().write(response);
                    } catch (Exception e) {
                        String error = "{\"error\":\"" + e.getMessage() + "\"}";
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                    } finally {
                        exchange.close();
                    }
                }
                else if (exchange.getRequestMethod().equals("GET")) {
                    String path = exchange.getRequestURI().getPath();
                    String[] segments = path.split("/");
                    if (segments.length < 3) {
                        String error = "{\"error\":\"User ID missing\"}";
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                        exchange.close();
                        return;
                    }
                    int userId = Integer.parseInt(segments[2]);

                    try (EntityManager entityManager = emf.createEntityManager()) {
                        UserRepository userRepository = new UserRepository(entityManager);
                        User user = userRepository.findById(userId);

                        if (user == null) {
                            String notFound = "{\"error\":\"User not found\"}";
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, notFound.length());
                            exchange.getResponseBody().write(notFound.getBytes());
                        } else {
                            String response = """
                                    {
                                        "userId": %d,
                                        "email": "%s",
                                        "name": "%s"
                                    }
                                    """.formatted(user.getUserId(), user.getEmail(), user.getName());

                            exchange.getResponseHeaders().set("Content-Type", "application/json");
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
                            exchange.getResponseBody().write(response.getBytes());
                        }
                    } catch (Exception e) {
                        String error = "{\"error\":\"" + e.getMessage() + "\"}";
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                    } finally {
                        exchange.close();
                    }
                }
            });

            server.start();
            System.out.println("Server is listening on port 8080...");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            }));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
