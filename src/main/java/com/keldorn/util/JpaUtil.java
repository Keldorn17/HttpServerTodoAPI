package com.keldorn.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JpaUtil {
    private static EntityManagerFactory emf;

    static {
        try {
            Properties props = new Properties();
            try (InputStream input = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("META-INF/config.properties")) {
                if (input == null) {
                    throw new RuntimeException("config.properties not found in resources!");
                }
                props.load(input);
            }

            Map<String, String> overrideProps = new HashMap<>();
            overrideProps.put("jakarta.persistence.jdbc.url", props.getProperty("db.url"));
            overrideProps.put("jakarta.persistence.jdbc.user", props.getProperty("db.user"));
            overrideProps.put("jakarta.persistence.jdbc.password", props.getProperty("db.password"));

            emf = Persistence.createEntityManagerFactory("TodoAPI-PU", overrideProps);
        } catch (IOException e) {
            throw new RuntimeException("Could not load database config", e);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}
